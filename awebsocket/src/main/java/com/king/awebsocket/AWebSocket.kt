package com.king.awebsocket

import android.os.Handler
import android.os.Looper
import com.king.awebsocket.state.ReadyState
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import java.util.concurrent.Executor

/**
 * AWebSocket：一个基于okhttp封装的 WebSocket 客户端，轻量易用。
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
class AWebSocket constructor(
    url: String,
    private val okHttpClient: OkHttpClient
) : IWebSocket {

    constructor(url: String) : this(
        url, OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()
    )

    private val requestBuilder = Request.Builder().url(url)

    private var engine: WebSocket? = null

    /**
     * 监听
     */
    private var webSocketListener: WebSocketListener? = null

    /**
     * WebSocket的连接状态
     */
    @Volatile
    private var readState: ReadyState = ReadyState.NOT_YET_CONNECTED

    /**
     * Executor
     */
    private val executor by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MainThreadExecutor()
    }

    /**
     * 构建 WebSocket
     */
    @Synchronized
    private fun newWebSocket() {
        readState = ReadyState.CONNECTING
        engine = okHttpClient.newWebSocket(
            request = requestBuilder.build(),
            listener = object : okhttp3.WebSocketListener() {
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    readState = ReadyState.CLOSED
                    webSocketListener?.also {
                        executor.execute {
                            it.onClosed(this@AWebSocket, code, reason)
                        }
                    }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosing(webSocket, code, reason)
                    readState = ReadyState.CLOSING
                    webSocketListener?.also {
                        executor.execute {
                            it.onClosing(this@AWebSocket, code, reason)
                        }
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    readState = ReadyState.CLOSED
                    webSocketListener?.also {
                        executor.execute {
                            it.onFailure(this@AWebSocket, t, response)
                        }
                    }
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    webSocketListener?.also {
                        executor.execute {
                            it.onMessage(this@AWebSocket, text)
                        }
                    }
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)
                    webSocketListener?.also {
                        executor.execute {
                            it.onMessage(this@AWebSocket, bytes)
                        }
                    }
                }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    readState = ReadyState.OPEN
                    webSocketListener?.also {
                        executor.execute {
                            it.onOpen(this@AWebSocket, response)
                        }
                    }
                }
            })
    }

    /**
     * 重置
     */
    private fun reset() {
        runCatching {
            if (isOpen()) {
                close()
            }
            okHttpClient.dispatcher.cancelAll()
        }
    }

    override fun connect() {
        if (readState == ReadyState.OPEN || readState == ReadyState.CONNECTING) {
            return
        }
        newWebSocket()
    }

    override fun reconnect() {
        reset()
        connect()
    }

    override fun close(): Boolean {
        return close(CLOSE_NORMAL, "")
    }

    override fun close(code: Int, reason: String): Boolean {
        if (readState == ReadyState.CLOSED || readState == ReadyState.CLOSING) {
            return true
        }
        return engine?.close(code, reason) ?: false
    }

    override fun isOpen(): Boolean {
        return readState == ReadyState.OPEN
    }

    override fun isClosing(): Boolean {
        return readState == ReadyState.CLOSING
    }

    override fun isClosed(): Boolean {
        return readState == ReadyState.CLOSED
    }

    override fun getReadyState(): ReadyState {
        return readState
    }

    override fun send(text: String): Boolean {
        if (readState != ReadyState.OPEN) {
            return false
        }
        return engine?.send(text) ?: false
    }

    override fun send(bytes: ByteString): Boolean {
        if (readState != ReadyState.OPEN) {
            return false
        }
        return engine?.send(bytes) ?: false
    }

    override fun addHeader(name: String, value: String) {
        requestBuilder.addHeader(name, value)
    }

    override fun removeHeader(name: String) {
        requestBuilder.removeHeader(name)
    }

    override fun setWebSocketListener(listener: WebSocketListener?) {
        this.webSocketListener = listener
    }

    /**
     * 获取 [WebSocket]
     */
    fun getConnect(): WebSocket? {
        return engine
    }

    companion object {
        /**
         * 正常关闭的状态码；
         *
         * 更多 WebSocket 协议状态码说明可参见：[Section 7.4 of RFC 6455](https://datatracker.ietf.org/doc/html/rfc6455#section-7.4)
         */
        const val CLOSE_NORMAL = 1000

    }

    /**
     * 主线程
     */
    private class MainThreadExecutor : Executor {

        private val handler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }

}
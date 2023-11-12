package com.king.awebsocket

import com.king.awebsocket.state.ReadyState
import okio.ByteString

/**
 * WebSocket
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
interface IWebSocket {

    /**
     * 连接
     */
    fun connect()

    /**
     * 重新连接
     */
    fun reconnect()

    /**
     * 关闭
     */
    fun close(): Boolean

    /**
     * 关闭
     * @param code 关闭状态码；状态码详细说明可参见：[Section 7.4 of RFC 6455](https://datatracker.ietf.org/doc/html/rfc6455#section-7.4)
     * @param reason 原因
     */
    fun close(code: Int, reason: String): Boolean

    /**
     * 连接是否已打开；更多详细状态可参见：[ReadyState]
     * @return
     */
    fun isOpen(): Boolean

    /**
     * 是否正在关闭中；更多详细状态可参见：[ReadyState]
     */
    fun isClosing(): Boolean

    /**
     * 是否已经关闭；更多详细状态可参见：[ReadyState]
     * @return
     */
    fun isClosed(): Boolean

    /**
     * 获取WebSocket的连接状态；更多详细状态可参见：[ReadyState]
     * @return [ReadyState]
     */
    fun getReadyState(): ReadyState

    /**
     * 发送消息；文本消息（类型：0x01）
     * @param text
     */
    fun send(text: String): Boolean

    /**
     * 发送消息；二进制消息（类型：0x02）
     */
    fun send(bytes: ByteString): Boolean

    /**
     * 添加请求头；需在调用[connect]函数之前添加请求头才有效
     */
    fun addHeader(name: String, value: String)

    /**
     * 移除请求头；需在调用[connect]函数之前移除请求头才有效
     */
    fun removeHeader(name: String)

    /**
     * 设置WebSocket监听
     */
    fun setWebSocketListener(listener: WebSocketListener?)

}


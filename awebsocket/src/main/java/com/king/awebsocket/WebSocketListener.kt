package com.king.awebsocket

import okhttp3.Response
import okio.ByteString

/**
 * WebSocket 监听器
 *
 * 参见：[okhttp3.WebSocketListener]
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
abstract class WebSocketListener {

    /**
     * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
     * messages.
     */
    open fun onOpen(webSocket: IWebSocket, response: Response) {

    }

    /** Invoked when a text (type `0x1`) message has been received. */
    open fun onMessage(webSocket: IWebSocket, text: String) {

    }

    /** Invoked when a binary (type `0x2`) message has been received. */
    open fun onMessage(webSocket: IWebSocket, bytes: ByteString) {

    }

    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
     */
    open fun onClosing(webSocket: IWebSocket, code: Int, reason: String) {

    }

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    open fun onClosed(webSocket: IWebSocket, code: Int, reason: String) {

    }

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    open fun onFailure(webSocket: IWebSocket, t: Throwable, response: Response?) {

    }
}
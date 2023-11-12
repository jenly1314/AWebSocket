package com.king.awebsocket.state

/**
 * WebSocket的连接状态
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
enum class ReadyState {
    /**
     * 未连接：尚未建立连接
     */
    NOT_YET_CONNECTED,

    /**
     * 连接中：正在建⽴连接，还没有完成
     */
    CONNECTING,

    /**
     * 连接已打开：连接成功建⽴，可以进⾏通信
     */
    OPEN,

    /**
     * 关闭中：连接正在进⾏关闭握⼿，即将关闭
     */
    CLOSING,

    /**
     * 已关闭：连接已经关闭或者根本没有建⽴
     */
    CLOSED,
}
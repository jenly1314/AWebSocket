package com.king.awebsocket.app

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.king.awebsocket.AWebSocket
import com.king.awebsocket.IWebSocket
import com.king.awebsocket.WebSocketListener
import com.king.awebsocket.app.databinding.ActivityMainBinding
import okhttp3.Response
import okio.ByteString

/**
 * AWebSocket 完整代码示例
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var aWebSocket: AWebSocket? = null

    private var reconnectCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        aWebSocket?.close()
        super.onDestroy()
    }

    private fun getContext() = this

    private fun clickConnectOrDisconnect() {
        if (aWebSocket?.isOpen() == true) {
            aWebSocket?.close()
            return
        }

        if (TextUtils.isEmpty(binding.etUrl.text)) {
            Toast.makeText(getContext(), "请输入连接地址", Toast.LENGTH_SHORT).show()
            return
        }

        val url = binding.etUrl.text.toString()

        try {
            initWebSocket(url)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvContent.text = "${e.message}\n"
        }
    }

    /**
     * 初始化WebSocket
     */
    private fun initWebSocket(url: String) {
        aWebSocket = AWebSocket(url)
        aWebSocket?.also {
            it.setWebSocketListener(object : WebSocketListener() {

                override fun onClosed(webSocket: IWebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    Log.d(TAG, "onClosed: code: $code, reason:$reason")
                    binding.tvContent.append("连接关闭：code: $code\n")
                    Toast.makeText(getContext(), "连接关闭", Toast.LENGTH_SHORT).show()
                    binding.etUrl.isEnabled = true
                    binding.btnConnect.isEnabled = true
                    binding.btnConnect.text = "连接"
                }

                override fun onClosing(webSocket: IWebSocket, code: Int, reason: String) {
                    super.onClosing(webSocket, code, reason)
                    Log.d(TAG, "onClosing: code: $code, reason:$reason")
                }

                override fun onFailure(webSocket: IWebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    Log.w(TAG, "onFailure: ${t.message}")
                    binding.tvContent.append("失败：${t.message}\n")

                    // 自动连接
                    if (reconnectCount < MAX_RECONNECT_COUNT) {
                        reconnectCount++
                        binding.tvContent.append("自动重连： $reconnectCount\n")
                        aWebSocket?.reconnect()
                    } else {
                        binding.etUrl.isEnabled = true
                        binding.progressBar.isVisible = false
                        binding.btnConnect.isEnabled = true
                        binding.btnConnect.text = "连接"
                    }
                }

                override fun onMessage(webSocket: IWebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    Log.d(TAG, "onMessage: $text")
                    binding.tvContent.append("接收：$text\n")
                }

                override fun onMessage(webSocket: IWebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)
                    Log.d(TAG, "onMessage: $bytes")
                    binding.tvContent.append("接收：$bytes\n")
                }

                override fun onOpen(webSocket: IWebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    Log.d(TAG, "onOpen: ${response.isSuccessful}")
                    reconnectCount = 0
                    binding.tvContent.append("连接成功\n")
                    Toast.makeText(getContext(), "连接成功", Toast.LENGTH_SHORT).show()
                    binding.etUrl.isEnabled = false
                    binding.progressBar.isVisible = false
                    binding.btnConnect.isEnabled = true
                    binding.btnConnect.text = "断开"
                }
            })
            binding.btnConnect.isEnabled = false
            binding.progressBar.isVisible = true
            reconnectCount = 0
            it.connect()
        }
    }

    private fun clickSend() {
        if (aWebSocket?.isOpen() != true) {
            Toast.makeText(getContext(), "未连接", Toast.LENGTH_SHORT).show()
            return
        }

        if (!TextUtils.isEmpty(binding.etContent.text)) {
            aWebSocket?.let {
                val data = binding.etContent.text.toString()

                if (it.send(data)) {
                    binding.tvContent.append("发送：${data}\n")
                }
                binding.etContent.setText("")
            }
        }
    }

    private fun clickClear() {
        binding.tvContent.text = ""
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.btnConnect -> clickConnectOrDisconnect()
            R.id.btnSend -> clickSend()
            R.id.btnClear -> clickClear()
        }
    }

    companion object {
        private const val TAG = "AWebSocket"

        /**
         * 重连最大次数
         */
        private const val MAX_RECONNECT_COUNT = 3
    }
}
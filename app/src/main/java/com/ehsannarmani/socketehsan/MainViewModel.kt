package com.ehsannarmani.socketehsan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import okio.ByteString
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

const val WEBSOCKET_URL = "wss://echo.websocket.org"
const val TIME_SSE_URL = "https://echo.websocket.org/.sse"

class MainViewModel: ViewModel() {

    private lateinit var webSocket:WebSocket

    private val _reconnectEnabled = MutableStateFlow(true)
    val reconnectEnabled = _reconnectEnabled.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _times = MutableStateFlow<List<String>>(emptyList())
    val times = _times.asStateFlow()

    private val listener by lazy {
        object :WebSocketListener(){
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _isConnected.update { false }
                if (_reconnectEnabled.value) connect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _error.update { t.message }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                _messages.update { it+text }
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
               _isConnected.update { true }
            }
        }
    }
    private val timeSSEListener by lazy {
        object :EventSourceListener(){
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                super.onEvent(eventSource, id, type, data)

                if (type == "time"){
                    data
                        .replace("T"," ")
                        .replace("Z","")
                        .replace("-","/")
                        .also {parsedTime->
                            _times.update { it+parsedTime }
                        }
                }
            }

            override fun onOpen(eventSource: EventSource, response: Response) {
                super.onOpen(eventSource, response)
            }
        }
    }

    fun connect(){
        val client = OkHttpClient()
        val request = Request.Builder().url(WEBSOCKET_URL).build()
        webSocket = client.newWebSocket(request,listener)
    }

    fun startSSE(){
        val sseClient = OkHttpClient()
            .newBuilder()
            .readTimeout(1,TimeUnit.DAYS)
            .writeTimeout(1,TimeUnit.DAYS)
            .build()
        val request = Request
            .Builder()
            .url(TIME_SSE_URL)
            .addHeader("Accept", "text/event-stream")
            .build()
        EventSources
            .createFactory(sseClient)
            .newEventSource(request,timeSSEListener)
    }

    fun sendMessage(message:String){
        webSocket.send(message)
    }

}
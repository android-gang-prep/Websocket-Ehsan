package com.ehsannarmani.socketehsan

import com.ehsannarmani.socketehsan.utils.isDateTime
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import org.junit.Test

import kotlin.time.Duration.Companion.milliseconds

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MainViewModelTest {

    companion object{
        val SOCKET_TIME_OUT = 1_500.milliseconds
        val SOCKET_RECEIVE_MESSAGE_TIME_OUT = 2_500.milliseconds

        val TIMER_SSE_RECEIVE_TIME_OUT = 2_500.milliseconds
        val SSE_CONNECTION_TIME_OUT = 1_500.milliseconds
    }

    private val viewModel = MainViewModel()

    @Test
    fun `test socket connection is ok`() {
        var isConnected = false
        viewModel.connect(object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                isConnected = true
            }

        })
        Thread.sleep(SOCKET_TIME_OUT.inWholeMilliseconds)
        assert(isConnected)
    }

    @Test
    fun `test sse connection is ok`(){
        var isConnected = false
        viewModel.startSSE(object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                super.onOpen(eventSource, response)
                isConnected = true
            }
        })
        Thread.sleep(SSE_CONNECTION_TIME_OUT.inWholeMilliseconds)
        assert(isConnected)
    }


    @Test
    fun `test websocket send message and receive`(){
        var isMessageReceived = false
        val testMessage = "Hello Dude"
        viewModel.connect(object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                isMessageReceived = text == testMessage
            }
        })
        viewModel.sendMessage(testMessage)
        Thread.sleep(SOCKET_RECEIVE_MESSAGE_TIME_OUT.inWholeMilliseconds)
        assert(isMessageReceived)
    }
    @Test
    fun `test time sse receive time is ok`(){
        var isTimeReceived = false
        viewModel.startSSE(object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                super.onEvent(eventSource, id, type, data)
                isTimeReceived = type == "time" && data.isDateTime()
            }
        })
        Thread.sleep(TIMER_SSE_RECEIVE_TIME_OUT.inWholeMilliseconds)
        assert(isTimeReceived)
    }
}
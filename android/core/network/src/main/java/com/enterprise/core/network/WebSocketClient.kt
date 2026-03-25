package com.enterprise.core.network

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

/** WebSocket client for real-time AI chat streaming. */
@Singleton
class WebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val tokenManager: TokenManager
) {
    private var webSocket: WebSocket? = null
    private val baseUrl = "wss://api.enterprise.com/v1/chat/ws"

    /** Opens a WebSocket connection and emits incoming text frames as a Flow. */
    fun connect(): Flow<WebSocketEvent> = callbackFlow {
        val token = tokenManager.getAccessToken() ?: ""
        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySend(WebSocketEvent.Connected)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                trySend(WebSocketEvent.MessageReceived(text))
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                trySend(WebSocketEvent.Disconnected(code, reason))
                webSocket.close(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                trySend(WebSocketEvent.Error(t))
            }
        }

        webSocket = okHttpClient.newWebSocket(request, listener)

        awaitClose {
            webSocket?.close(1000, "Client disconnecting")
            webSocket = null
        }
    }

    fun send(text: String): Boolean {
        return webSocket?.send(text) ?: false
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnecting")
        webSocket = null
    }
}

/** WebSocket lifecycle events. */
sealed class WebSocketEvent {
    data object Connected : WebSocketEvent()
    data class MessageReceived(val text: String) : WebSocketEvent()
    data class Disconnected(val code: Int, val reason: String) : WebSocketEvent()
    data class Error(val throwable: Throwable) : WebSocketEvent()
}

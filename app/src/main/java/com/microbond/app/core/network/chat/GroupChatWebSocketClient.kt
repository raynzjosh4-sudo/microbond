package com.microbond.app.core.network.chat

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.microbond.app.BuildConfig
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatMessageDto
import com.microbond.app.feature_groups.data.groupchartrepostory.GroupChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

/**
 * Real-time WebSocket client for live group chat events.
 * Listens for NEW_MESSAGE broadcasts and message read receipts.
 */
object GroupChatWebSocketClient {

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var webSocket: WebSocket? = null
    private var activeGroupId: String? = null
    private var activeGroupName: String = ""
    private var appContext: Context? = null
    private var isConnected = false
    private var reconnectJob: Job? = null

    private val client by lazy {
        OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .pingInterval(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Connects to the group chat WebSocket channel for [groupId].
     */
    fun connect(groupId: String, groupName: String, context: Context) {
        if (activeGroupId == groupId && isConnected) return

        disconnect()
        activeGroupId = groupId
        activeGroupName = groupName
        appContext = context.applicationContext

        val token = AuthSessionManager.accessToken.value
        val baseUrl = BuildConfig.BASE_URL
        val wsProtocol = if (baseUrl.startsWith("https://")) "wss://" else "ws://"
        val host = baseUrl.removePrefix("https://").removePrefix("http://").trimEnd('/')
        val wsUrl = if (!token.isNullOrBlank()) {
            "$wsProtocol$host/api/v1/groups/$groupId/chat/ws?token=$token"
        } else {
            "$wsProtocol$host/api/v1/groups/$groupId/chat/ws"
        }

        val requestBuilder = Request.Builder().url(wsUrl)
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        webSocket = client.newWebSocket(requestBuilder.build(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                android.util.Log.d("GroupChatWebSocket", "Connected to chat for $groupId")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleIncomingJson(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                isConnected = false
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                android.util.Log.w("GroupChatWebSocket", "WebSocket failure for $groupId: ${t.message}")
                scheduleReconnect()
            }
        })
    }

    private fun handleIncomingJson(jsonStr: String) {
        try {
            val root = gson.fromJson(jsonStr, JsonObject::class.java) ?: return
            val eventType = root.get("event_type")?.asString ?: root.get("type")?.asString ?: ""

            if (eventType.equals("NEW_MESSAGE", ignoreCase = true) || root.has("message")) {
                val messageElement = root.get("message") ?: root.get("data")
                if (messageElement != null && messageElement.isJsonObject) {
                    val dto = gson.fromJson(messageElement, GroupChatMessageDto::class.java)
                    if (dto != null) {
                        appContext?.let { ctx ->
                            GroupChatRepository.onLiveMessageReceived(dto, activeGroupName, ctx)
                        }
                    }
                }
            } else if (eventType.equals("MESSAGE_READ", ignoreCase = true) || eventType.equals("READ_RECEIPT", ignoreCase = true)) {
                val messageId = root.get("message_id")?.asString
                if (!messageId.isNullOrBlank()) {
                    GroupChatRepository.onMessageReadByPeer(messageId)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("GroupChatWebSocket", "Error parsing incoming chat event", e)
        }
    }

    private fun scheduleReconnect() {
        val targetId = activeGroupId ?: return
        val targetName = activeGroupName
        val context = appContext ?: return

        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            delay(5000L)
            if (!isConnected && activeGroupId == targetId) {
                connect(targetId, targetName, context)
            }
        }
    }

    /**
     * Closes the active WebSocket connection cleanly when user leaves the chat screen.
     */
    fun disconnect() {
        reconnectJob?.cancel()
        try {
            webSocket?.close(1000, "Leaving thread")
        } catch (_: Exception) {}
        webSocket = null
        isConnected = false
        activeGroupId = null
    }
}

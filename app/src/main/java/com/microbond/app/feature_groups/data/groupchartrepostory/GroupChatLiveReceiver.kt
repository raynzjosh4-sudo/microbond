package com.microbond.app.feature_groups.data.groupchartrepostory

import android.content.Context
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatMessageDto
import com.microbond.app.feature_groups.data.GroupChatMapper
import com.microbond.app.feature_groups.data.UserGroupsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles all live/incoming message concerns:
 * - Processing WebSocket push events
 * - Marking messages as read (local + server)
 * - Peer read-receipt acknowledgements
 * - Local message status mutation helpers
 * - Error message formatting utilities
 */
internal object GroupChatLiveReceiver {

    private const val TAG = "GroupChatLiveReceiver"

    /**
     * Processes a live incoming message from the WebSocket.
     * Deduplicates, appends to the list, updates dashboard preview, and marks read.
     */
    fun onLiveMessageReceived(
        dto: GroupChatMessageDto,
        groupName: String,
        context: Context,
        currentUserId: String,
        activeGroupId: String?,
        knownServerIds: MutableSet<Int>,
        messages: MutableStateFlow<List<ThreadEvent>>,
        onMarkRead: (groupId: String, groupName: String, context: Context) -> Unit
    ) {
        val mappedEvent = GroupChatMapper.toThreadEvent(dto, currentUserId)
        knownServerIds.add(mappedEvent.id)

        val currentList = messages.value
        val alreadyExists = currentList.any {
            it.id == mappedEvent.id ||
            (!dto.clientMessageId.isNullOrBlank() && it.id == dto.clientMessageId.hashCode())
        }

        if (!alreadyExists) {
            messages.value = currentList + mappedEvent

            val snippet = when (mappedEvent.type) {
                ThreadBubbleType.VOICE -> "🎤 Voice message"
                ThreadBubbleType.IMAGE -> if (mappedEvent.text.isNotBlank()) "📷 ${mappedEvent.text}" else "📷 Photo"
                ThreadBubbleType.DOCUMENT -> "📄 Document"
                ThreadBubbleType.DEPOSIT -> "💰 Contribution update"
                else -> mappedEvent.text.ifBlank { "New message" }
            }
            UserGroupsRepository.updateLatestMessage(
                groupName = groupName,
                message = snippet,
                time = mappedEvent.time,
                context = context
            )

            if (activeGroupId == dto.groupId && !mappedEvent.isCurrentUser) {
                onMarkRead(dto.groupId ?: "", groupName, context)
            }
        }
    }

    /**
     * Marks all messages in the group as read locally and calls the backend.
     */
    fun markMessagesAsRead(
        groupId: String,
        groupName: String,
        context: Context?,
        scope: kotlinx.coroutines.CoroutineScope
    ) {
        if (groupId.isBlank()) return
        UserGroupsRepository.markGroupAsRead(groupName, groupId, context)

        scope.launch {
            try {
                val response = NetworkClient.apiService.markMessagesRead(groupId)
                if (response.isSuccessful) {
                    android.util.Log.d(TAG, "markMessagesRead: ${response.body()?.markedCount} marked for $groupId")
                } else {
                    android.util.Log.w(TAG, "markMessagesRead failed for $groupId: HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Failed to call markMessagesRead for $groupId", e)
            }
        }
    }

    /**
     * Updates the local status of a message when the peer sends a read receipt.
     */
    fun onMessageReadByPeer(messageId: String, messages: MutableStateFlow<List<ThreadEvent>>) {
        val targetHash = messageId.hashCode()
        val current = messages.value.toMutableList()
        val index = current.indexOfFirst { it.id == targetHash }
        if (index >= 0) {
            current[index] = current[index].copy(status = MessageStatus.READ)
            messages.value = current
        }
    }

    /**
     * Mutates the local status of a message by eventId (e.g. after a send ACK or failure).
     */
    fun updateLocalMessageStatus(
        eventId: Int,
        newStatus: MessageStatus,
        knownServerIds: MutableSet<Int>,
        messages: MutableStateFlow<List<ThreadEvent>>
    ) {
        if (newStatus == MessageStatus.DELIVERED) knownServerIds.add(eventId)
        val current = messages.value.toMutableList()
        val index = current.indexOfFirst { it.id == eventId }
        if (index >= 0) {
            current[index] = current[index].copy(status = newStatus)
            messages.value = current
        }
    }

    // ── Error formatting ──────────────────────────────────────────────────────

    fun formatChatSystemErrorMessage(code: Int, errorBody: String?, gson: com.google.gson.Gson): String {
        val serverDetail = extractErrorDetail(errorBody, gson)?.trim()
        val hasDetail = !serverDetail.isNullOrBlank() &&
                !serverDetail.equals("Forbidden", ignoreCase = true) &&
                !serverDetail.equals("Unauthorized", ignoreCase = true) &&
                !serverDetail.equals("Internal Server Error", ignoreCase = true)

        return when (code) {
            403 -> if (hasDetail) "🔒 $serverDetail"
                   else "🔒 You don't have access to this chat yet. Membership confirmation or approval is required. Tap to retry."
            401 -> "🔒 Session expired. Please log in again to sync messages."
            404 -> "Chat history is currently unavailable for this group."
            in 500..599 -> "Chat server is temporarily unavailable. Messages will sync once it's back online. Tap to retry."
            else -> if (hasDetail) serverDetail!! else "Unable to sync messages right now (HTTP $code). Tap to retry."
        }
    }

    fun formatExceptionSystemMessage(e: Exception): String =
        if (e is java.net.UnknownHostException || e is java.io.IOException)
            "Unable to connect to chat server. Showing offline messages. Tap to retry."
        else
            "Unable to sync messages (${e.localizedMessage ?: "Network error"}). Tap to retry."

    private fun extractErrorDetail(errorBody: String?, gson: com.google.gson.Gson): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val json = gson.fromJson(errorBody, com.google.gson.JsonObject::class.java)
            when {
                json.has("detail") -> {
                    val detail = json.get("detail")
                    if (detail.isJsonPrimitive) detail.asString
                    else if (detail.isJsonObject && detail.asJsonObject.has("message"))
                        detail.asJsonObject.get("message").asString
                    else detail.toString()
                }
                json.has("message") -> json.get("message").asString
                else -> null
            }
        } catch (_: Exception) {
            errorBody.takeIf { it.length < 120 && !it.startsWith("<") }
        }
    }
}

package com.microbond.app.feature_groups.data.groupchartrepostory

import android.content.Context
import android.net.Uri
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.media.MediaCompressor
import com.microbond.app.core.network.MediaUploadHelper
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatMessageDto
import com.microbond.app.core.network.api.goupapis.chatapiservice.SendMessageRequest
import com.microbond.app.feature_groups.data.GroupChatMapper
import com.microbond.app.feature_groups.data.UserGroupsRepository
import com.google.gson.Gson
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Handles sending of all outgoing chat message types:
 * - Plain text / reply
 * - Media (images, voice, documents) via single or multiple URIs
 * - Pre-built MultipartBody.Part (from camera / external pickers)
 * - Structured messages (polls, charts, deposit status, etc.)
 */
internal object GroupChatMediaSender {

    private val gson = Gson()

    fun resolveCurrentUserId(context: Context): String {
        AuthSessionManager.currentUserId?.takeIf { it.isNotBlank() }?.let { return it }
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return prefs.getString("user_id", "") ?: ""
    }

    /**
     * Sends a plain text or reply message with optimistic insert.
     */
    fun sendTextMessage(
        groupId: String,
        text: String,
        replyToEvent: ThreadEvent? = null,
        groupName: String,
        context: Context,
        scope: CoroutineScope,
        messages: kotlinx.coroutines.flow.MutableStateFlow<List<ThreadEvent>>,
        onStatusUpdate: (Int, MessageStatus) -> Unit
    ) {
        if (text.isBlank()) return
        val clientMessageId = UUID.randomUUID().toString()
        val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

        val optimisticEvent = ThreadEvent(
            id = clientMessageId.hashCode(),
            messageId = clientMessageId,
            text = text.trim(),
            time = timeString,
            isCurrentUser = true,
            status = MessageStatus.SENT,
            type = if (replyToEvent != null) ThreadBubbleType.REPLY else ThreadBubbleType.TEXT,
            senderName = "You",
            senderInitials = "Y",
            senderColor = MicrobondActiveGreen,
            quotedSender = replyToEvent?.let { if (it.isCurrentUser) "You" else it.senderName ?: "Member" },
            quotedText = replyToEvent?.text,
            quotedEventId = replyToEvent?.id
        )

        messages.value = messages.value + optimisticEvent
        UserGroupsRepository.updateLatestMessage(groupName, text.trim(), timeString, context)

        if (groupId.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                try {
                    val replyIdToSend = replyToEvent?.messageId ?: replyToEvent?.id?.toString()
                    val request = SendMessageRequest(
                        clientMessageId = clientMessageId,
                        messageType = "TEXT",
                        messageText = text.trim(),
                        replyToMessageId = replyIdToSend
                    )
                    val response = NetworkClient.apiService.sendMessage(groupId, request)
                    val status = if (response.isSuccessful && response.body() != null)
                        MessageStatus.DELIVERED else MessageStatus.FAILED
                    onStatusUpdate(clientMessageId.hashCode(), status)
                } catch (e: Exception) {
                    android.util.Log.e("GroupChatMediaSender", "Failed to send text message", e)
                    onStatusUpdate(clientMessageId.hashCode(), MessageStatus.FAILED)
                }
            }
        }
    }

    /**
     * Sends one or more media URIs to the group.
     */
    fun sendMediaFromUris(
        groupId: String,
        uris: List<Uri>,
        mediaType: String,
        caption: String? = null,
        replyToId: String? = null,
        groupName: String,
        context: Context,
        scope: CoroutineScope,
        messages: kotlinx.coroutines.flow.MutableStateFlow<List<ThreadEvent>>,
        onStatusUpdate: (Int, MessageStatus) -> Unit
    ) {
        if (uris.isEmpty()) return
        val clientMessageId = UUID.randomUUID().toString()
        val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

        val resolvedBubbleType = when (mediaType.uppercase(Locale.getDefault())) {
            "IMAGE", "PHOTO" -> ThreadBubbleType.IMAGE
            "VOICE_NOTE", "VOICE", "AUDIO" -> ThreadBubbleType.VOICE
            "VIDEO" -> ThreadBubbleType.VIDEO
            "DOCUMENT", "DOC" -> ThreadBubbleType.DOCUMENT
            else -> ThreadBubbleType.TEXT
        }

        val firstUriString = uris.first().toString()
        val optimisticRatio = if (resolvedBubbleType == ThreadBubbleType.IMAGE)
            MediaCompressor.getImageAspectRatio(context, uris.first()) else null

        val optimisticEvent = ThreadEvent(
            id = clientMessageId.hashCode(),
            messageId = clientMessageId,
            text = caption ?: "",
            time = timeString,
            isCurrentUser = true,
            status = MessageStatus.SENT,
            type = resolvedBubbleType,
            senderName = "You",
            senderInitials = "Y",
            senderColor = MicrobondActiveGreen,
            imageUri = if (resolvedBubbleType == ThreadBubbleType.IMAGE) firstUriString else null,
            imageUris = if (resolvedBubbleType == ThreadBubbleType.IMAGE && uris.size > 1)
                uris.drop(1).map { it.toString() } else null,
            imageAspectRatio = optimisticRatio,
            voiceUri = if (resolvedBubbleType == ThreadBubbleType.VOICE) firstUriString else null
        )

        messages.value = messages.value + optimisticEvent
        val snippet = when (resolvedBubbleType) {
            ThreadBubbleType.IMAGE -> if (!caption.isNullOrBlank()) "📷 $caption" else "📷 Photo"
            ThreadBubbleType.VOICE -> "🎤 Voice message"
            ThreadBubbleType.DOCUMENT -> "📄 Document"
            else -> caption ?: "Sent media"
        }
        UserGroupsRepository.updateLatestMessage(groupName, snippet, timeString, context)

        if (groupId.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                try {
                    val parts = uris.mapNotNull { uri ->
                        MediaUploadHelper.createMultipartPart(context, uri, "files")
                    }
                    if (parts.isEmpty()) {
                        onStatusUpdate(clientMessageId.hashCode(), MessageStatus.FAILED); return@launch
                    }
                    val response = NetworkClient.apiService.uploadMediaMessage(
                        groupId = groupId,
                        files = parts,
                        clientMessageId = clientMessageId.toRequestBody("text/plain".toMediaTypeOrNull()),
                        mediaType = mediaType.toRequestBody("text/plain".toMediaTypeOrNull()),
                        messageText = caption?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        replyToMessageId = replyToId?.toRequestBody("text/plain".toMediaTypeOrNull())
                    )
                    val status = if (response.isSuccessful) MessageStatus.DELIVERED else MessageStatus.FAILED
                    onStatusUpdate(clientMessageId.hashCode(), status)
                } catch (e: Exception) {
                    android.util.Log.e("GroupChatMediaSender", "Failed to upload media", e)
                    onStatusUpdate(clientMessageId.hashCode(), MessageStatus.FAILED)
                }
            }
        }
    }

    /**
     * Sends structured messages (polls, charts, deposit status, etc.) via JSON.
     */
    fun sendStructuredMessage(
        groupId: String,
        messageType: String,
        text: String?,
        attachments: Any?,
        replyToId: String? = null,
        localEvent: ThreadEvent? = null,
        groupName: String,
        context: Context,
        scope: CoroutineScope,
        messages: kotlinx.coroutines.flow.MutableStateFlow<List<ThreadEvent>>,
        onStatusUpdate: (Int, MessageStatus) -> Unit
    ) {
        val clientMessageId = UUID.randomUUID().toString()
        val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

        val eventToAppend = localEvent?.copy(
            id = clientMessageId.hashCode(),
            messageId = clientMessageId,
            status = MessageStatus.SENT
        ) ?: ThreadEvent(
            id = clientMessageId.hashCode(),
            messageId = clientMessageId,
            text = text ?: "",
            time = timeString,
            isCurrentUser = true,
            status = MessageStatus.SENT,
            type = GroupChatMapper.toThreadEvent(
                GroupChatMessageDto(
                    id = clientMessageId,
                    clientMessageId = clientMessageId,
                    groupId = groupId,
                    senderId = AuthSessionManager.currentUserId ?: "",
                    senderName = "You",
                    messageType = messageType,
                    messageText = text,
                    attachments = gson.toJsonTree(attachments),
                    replyToMessageId = replyToId
                ),
                currentUserId = AuthSessionManager.currentUserId ?: ""
            ).type,
            senderName = "You",
            senderInitials = "Y",
            senderColor = MicrobondActiveGreen
        )

        messages.value = messages.value + eventToAppend
        UserGroupsRepository.updateLatestMessage(groupName, text ?: "Sent update", timeString, context)

        if (groupId.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                try {
                    val response = NetworkClient.apiService.sendMessage(
                        groupId = groupId,
                        request = SendMessageRequest(
                            clientMessageId = clientMessageId,
                            messageType = messageType,
                            messageText = text,
                            attachments = attachments,
                            replyToMessageId = replyToId
                        )
                    )
                    val status = if (response.isSuccessful) MessageStatus.DELIVERED else MessageStatus.FAILED
                    onStatusUpdate(clientMessageId.hashCode(), status)
                } catch (e: Exception) {
                    android.util.Log.e("GroupChatMediaSender", "Failed to send structured message", e)
                    onStatusUpdate(clientMessageId.hashCode(), MessageStatus.FAILED)
                }
            }
        }
    }
}

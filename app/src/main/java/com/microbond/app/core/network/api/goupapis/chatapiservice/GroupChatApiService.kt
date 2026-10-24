package com.microbond.app.core.network.api.goupapis.chatapiservice

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// ── Models ───────────────────────────────────────────────────────────────────

data class GroupChatMessagesResponse(
    @SerializedName("data") val data: List<GroupChatMessageDto> = emptyList()
)

data class GroupChatReplyToDto(
    @SerializedName("message_id") val messageId: String? = null,
    @SerializedName("sender_name") val senderName: String? = null,
    @SerializedName("text") val text: String? = null
)

data class GroupChatMessageDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("client_message_id") val clientMessageId: String? = null,
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("sender_id") val senderId: String = "",
    @SerializedName("sender_name") val senderName: String? = null,
    @SerializedName("sender_phone") val senderPhone: String? = null,
    @SerializedName("sender_avatar_url") val senderAvatarUrl: String? = null,
    @SerializedName("sender_avatar_color") val senderAvatarColor: String? = null,
    @SerializedName("is_outgoing") val isOutgoing: Boolean? = null,
    @SerializedName("message_type") val messageType: String = "TEXT",
    @SerializedName("message_text") val messageText: String? = null,
    @SerializedName("media_url") val mediaUrl: String? = null,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("media_width") val mediaWidth: Int? = null,
    @SerializedName("media_height") val mediaHeight: Int? = null,
    @SerializedName("aspect_ratio") val aspectRatio: Float? = null,
    @SerializedName("attachments") val attachments: com.google.gson.JsonElement? = null,
    @SerializedName("reply_to") val replyTo: GroupChatReplyToDto? = null,
    @SerializedName("reply_to_message_id") val replyToMessageId: String? = null,
    @SerializedName("status") val status: String? = "SENT",
    @SerializedName("read_count") val readCount: Int? = 0,
    @SerializedName("is_read") val isRead: Boolean? = false,
    @SerializedName("is_read_by_me") val isReadByMe: Boolean? = false,
    @SerializedName("created_at") val createdAt: Number? = 0L,
    @SerializedName("updated_at") val updatedAt: Number? = 0L
)

data class SendMessageRequest(
    @SerializedName("client_message_id") val clientMessageId: String,
    @SerializedName("message_type") val messageType: String = "TEXT",
    @SerializedName("message_text") val messageText: String?,
    @SerializedName("attachments") val attachments: Any? = null,
    @SerializedName("reply_to_message_id") val replyToMessageId: String? = null
)

data class SendMessageResponse(
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: GroupChatMessageDto
)

data class MarkMessagesReadRequest(
    @SerializedName("message_ids") val messageIds: List<String>? = null
)

data class MarkMessagesReadResponse(
    @SerializedName("status") val status: String = "success",
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("marked_count") val markedCount: Int = 0,
    @SerializedName("read_at") val readAt: Long = 0L
)

// ── Retrofit API Interface ───────────────────────────────────────────────────

interface GroupChatApiService {

    /**
     * Cursor-paginated message history.
     * @param groupId Group UUID
     * @param limit Default 50, max 100
     * @param beforeCreatedAt Timestamp cursor for fetching older messages when scrolling up
     */
    @GET("api/v1/groups/{group_id}/chat/messages")
    suspend fun getGroupMessages(
        @Path("group_id") groupId: String,
        @Query("limit") limit: Int = 50,
        @Query("before_created_at") beforeCreatedAt: Long? = null
    ): Response<GroupChatMessagesResponse>

    /**
     * Marks specified messages (or all unread incoming messages) as read by the current user.
     * Records read receipts and updates unread badge counts across the group.
     */
    @POST("api/v1/groups/{group_id}/chat/messages/read")
    suspend fun markMessagesRead(
        @Path("group_id") groupId: String,
        @Body request: MarkMessagesReadRequest = MarkMessagesReadRequest()
    ): Response<MarkMessagesReadResponse>

    /**
     * Sends a text, reply, or structured poll/chart message with idempotency key.
     */
    @POST("api/v1/groups/{group_id}/chat/messages")
    suspend fun sendMessage(
        @Path("group_id") groupId: String,
        @Body request: SendMessageRequest
    ): Response<SendMessageResponse>

    /**
     * Uploads photos, voice notes, video, or documents to Cloudflare R2 storage.
     * Supports single file or multi-file albums.
     */
    @Multipart
    @POST("api/v1/groups/{group_id}/chat/media")
    suspend fun uploadMediaMessage(
        @Path("group_id") groupId: String,
        @Part files: List<MultipartBody.Part>,
        @Part("client_message_id") clientMessageId: RequestBody,
        @Part("media_type") mediaType: RequestBody,
        @Part("message_text") messageText: RequestBody? = null,
        @Part("reply_to_message_id") replyToMessageId: RequestBody? = null
    ): Response<SendMessageResponse>

    suspend fun uploadSingleMediaMessage(
        groupId: String,
        file: MultipartBody.Part,
        clientMessageId: RequestBody,
        mediaType: RequestBody,
        messageText: RequestBody? = null,
        replyToMessageId: RequestBody? = null
    ): Response<SendMessageResponse> {
        return uploadMediaMessage(
            groupId = groupId,
            files = listOf(file),
            clientMessageId = clientMessageId,
            mediaType = mediaType,
            messageText = messageText,
            replyToMessageId = replyToMessageId
        )
    }
}

package com.microbond.app.feature_groups.data.groupchartrepostory

import android.content.Context
import android.net.Uri
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatMessageDto
import com.microbond.app.feature_groups.data.groupchartrepostory.GroupChatLiveReceiver
import com.microbond.app.feature_groups.data.groupchartrepostory.GroupChatMediaSender
import com.google.gson.Gson
import com.microbond.app.feature_groups.data.GroupChatMapper
import com.microbond.app.feature_groups.data.UserGroupsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Facade repository coordinating group chat history, cursor-based pagination,
 * optimistic local dispatch, and live message broadcasts.
 *
 * Heavy lifting is delegated to:
 * - [GroupChatMediaSender]  — all outgoing message types
 * - [GroupChatLiveReceiver] — WebSocket delivery, read receipts, error formatting
 */
object GroupChatRepository {

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    // ── Exposed state flows ───────────────────────────────────────────────────

    private val _messages = MutableStateFlow<List<ThreadEvent>>(emptyList())
    val messages: StateFlow<List<ThreadEvent>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoadingOlder = MutableStateFlow(false)
    val isLoadingOlder: StateFlow<Boolean> = _isLoadingOlder.asStateFlow()

    private val _hasMoreOlder = MutableStateFlow(true)
    val hasMoreOlder: StateFlow<Boolean> = _hasMoreOlder.asStateFlow()

    // ── Internal state ────────────────────────────────────────────────────────

    private const val TAG = "GroupChatRepository"

    private var activeGroupId: String? = null
    fun getActiveGroupId(): String? = activeGroupId

    private var activeGroupName: String = ""
    private var oldestMessageTimestamp: Long? = null

    /** IDs confirmed to exist on the backend (not purely local / in-flight). */
    private val knownServerIds: MutableSet<Int> = ConcurrentHashMap.newKeySet()

    // ── Auth helpers ──────────────────────────────────────────────────────────

    private fun resolveCurrentUserId(context: Context): String {
        AuthSessionManager.currentUserId?.takeIf { it.isNotBlank() }?.let { return it }
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return prefs.getString("user_id", "") ?: ""
    }

    // ── Chat loading ──────────────────────────────────────────────────────────

    /**
     * Opens a group thread and (re)loads the newest 50 messages from the backend.
     * On re-opening the same group the current list remains visible until fresh data arrives.
     */
    fun loadChat(groupId: String, entityName: String, context: Context) {
        val isSameGroup = activeGroupId == groupId
        activeGroupId = groupId
        activeGroupName = entityName

        if (!isSameGroup) {
            _messages.value = emptyList()
            knownServerIds.clear()
            oldestMessageTimestamp = null
            _hasMoreOlder.value = true
        }

        if (groupId.isBlank()) {
            android.util.Log.w(TAG, "loadChat: no backend group id for '$entityName'")
            val systemMsg = "This group is being set up. Messages will sync once connected."
            _errorMessage.value = systemMsg
            val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            _messages.value = listOf(
                ThreadEvent(
                    id = -1000,
                    text = systemMsg,
                    time = timeString,
                    isCurrentUser = false,
                    status = MessageStatus.SENT,
                    type = ThreadBubbleType.SYSTEM,
                    senderName = "System",
                    systemMessage = systemMsg
                )
            )
            return
        }

        scope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val currentUserId = resolveCurrentUserId(context)
                val response = NetworkClient.apiService.getGroupMessages(groupId = groupId, limit = 50)

                if (!response.isSuccessful) {
                    val errorDetail = response.errorBody()?.string()
                    android.util.Log.e(TAG, "GET chat/messages failed for $groupId: HTTP ${response.code()} $errorDetail")
                    val systemErrorText = GroupChatLiveReceiver.formatChatSystemErrorMessage(response.code(), errorDetail, gson)
                    _errorMessage.value = systemErrorText
                    val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                    val systemEvent = ThreadEvent(
                        id = -1001,
                        text = systemErrorText,
                        time = timeString,
                        isCurrentUser = false,
                        status = MessageStatus.SENT,
                        type = ThreadBubbleType.SYSTEM,
                        senderName = "System",
                        systemMessage = systemErrorText
                    )
                    _messages.value = _messages.value.filter { it.id >= 0 } + systemEvent
                    return@launch
                }

                if (activeGroupId != groupId) return@launch

                val rawData = response.body()?.data.orEmpty()
                android.util.Log.d(TAG, "Fetched ${rawData.size} messages for $groupId")

                val fetched = rawData.map { dto ->
                    GroupChatMapper.toThreadEvent(dto, currentUserId)
                }.reversed()
                val fetchedIds = fetched.map { it.id }.toSet()
                val fetchedClientHashes = rawData.mapNotNull { it.clientMessageId?.takeIf { id -> id.isNotBlank() } }
                    .map { it.hashCode() }.toSet()

                val localOnly = _messages.value.filter {
                    it.id >= 0 && it.id !in fetchedIds && it.id !in fetchedClientHashes && it.id !in knownServerIds
                }

                knownServerIds.clear()
                knownServerIds.addAll(fetchedIds)
                _messages.value = fetched + localOnly
                oldestMessageTimestamp = rawData.lastOrNull()?.createdAt?.toLong()
                _hasMoreOlder.value = rawData.size >= 50
                _errorMessage.value = null

                markMessagesAsRead(groupId, entityName, context)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Failed to fetch messages for $groupId", e)
                val systemErrorText = GroupChatLiveReceiver.formatExceptionSystemMessage(e)
                _errorMessage.value = systemErrorText
                val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                val systemEvent = ThreadEvent(
                    id = -1002,
                    text = systemErrorText,
                    time = timeString,
                    isCurrentUser = false,
                    status = MessageStatus.SENT,
                    type = ThreadBubbleType.SYSTEM,
                    senderName = "System",
                    systemMessage = systemErrorText
                )
                _messages.value = _messages.value.filter { it.id >= 0 } + systemEvent
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetches older messages before [oldestMessageTimestamp] when user scrolls to the top.
     */
    fun fetchOlderMessages(context: Context) {
        val targetGroupId = activeGroupId ?: return
        val cursor = oldestMessageTimestamp ?: return
        if (_isLoadingOlder.value || !_hasMoreOlder.value || targetGroupId.isBlank()) return

        scope.launch {
            _isLoadingOlder.value = true
            try {
                val currentUserId = resolveCurrentUserId(context)
                val response = NetworkClient.apiService.getGroupMessages(
                    groupId = targetGroupId,
                    limit = 50,
                    beforeCreatedAt = cursor
                )
                if (!response.isSuccessful) {
                    android.util.Log.e(TAG, "GET older messages failed: HTTP ${response.code()}")
                } else if (activeGroupId == targetGroupId) {
                    val rawData = response.body()?.data.orEmpty()
                    if (rawData.isNotEmpty()) {
                        val existingIds = _messages.value.map { it.id }.toSet()
                        val olderChronological = rawData.map { dto ->
                            GroupChatMapper.toThreadEvent(dto, currentUserId)
                        }.reversed().filter { it.id !in existingIds }

                        knownServerIds.addAll(olderChronological.map { it.id })
                        _messages.value = olderChronological + _messages.value
                        oldestMessageTimestamp = rawData.last().createdAt?.toLong()
                        _hasMoreOlder.value = rawData.size >= 50
                    } else {
                        _hasMoreOlder.value = false
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Failed to fetch older messages", e)
            } finally {
                _isLoadingOlder.value = false
            }
        }
    }

    // ── Sending delegates ─────────────────────────────────────────────────────

    /** Sends a plain text or reply message. */
    fun sendTextMessage(
        groupId: String,
        text: String,
        replyToEvent: ThreadEvent? = null,
        groupName: String = activeGroupName,
        context: Context
    ) {
        GroupChatMediaSender.sendTextMessage(
            groupId = groupId,
            text = text,
            replyToEvent = replyToEvent,
            groupName = groupName,
            context = context,
            scope = scope,
            messages = _messages,
            onStatusUpdate = ::updateLocalMessageStatus
        )
    }

    /** Sends a single media URI to the group. */
    fun sendMediaFromUri(
        groupId: String,
        uri: Uri,
        mediaType: String,
        caption: String? = null,
        replyToId: String? = null,
        groupName: String = activeGroupName,
        context: Context
    ) {
        GroupChatMediaSender.sendMediaFromUris(
            groupId = groupId,
            uris = listOf(uri),
            mediaType = mediaType,
            caption = caption,
            replyToId = replyToId,
            groupName = groupName,
            context = context,
            scope = scope,
            messages = _messages,
            onStatusUpdate = ::updateLocalMessageStatus
        )
    }

    /** Sends multiple media URIs (e.g. an album) to the group. */
    fun sendMediaFromUris(
        groupId: String,
        uris: List<Uri>,
        mediaType: String,
        caption: String? = null,
        replyToId: String? = null,
        groupName: String = activeGroupName,
        context: Context
    ) {
        GroupChatMediaSender.sendMediaFromUris(
            groupId = groupId,
            uris = uris,
            mediaType = mediaType,
            caption = caption,
            replyToId = replyToId,
            groupName = groupName,
            context = context,
            scope = scope,
            messages = _messages,
            onStatusUpdate = ::updateLocalMessageStatus
        )
    }

    /** Sends a pre-built MultipartBody.Part (e.g. from camera). */
    fun sendMediaMessage(
        groupId: String,
        filePart: MultipartBody.Part,
        mediaType: String,
        caption: String? = null,
        replyToId: String? = null,
        localUri: String? = null,
        groupName: String = activeGroupName,
        context: Context
    ) {
        // Wrap single part into a temp Uri delegate to reuse sendMediaFromUris logic
        if (localUri != null) {
            sendMediaFromUri(
                groupId = groupId,
                uri = Uri.parse(localUri),
                mediaType = mediaType,
                caption = caption,
                replyToId = replyToId,
                groupName = groupName,
                context = context
            )
        }
    }

    /** Sends a structured in-chat message (poll, chart, deposit status update, etc.). */
    fun sendStructuredMessage(
        groupId: String,
        messageType: String,
        text: String?,
        attachments: Any?,
        replyToId: String? = null,
        localEvent: ThreadEvent? = null,
        groupName: String = activeGroupName,
        context: Context
    ) {
        GroupChatMediaSender.sendStructuredMessage(
            groupId = groupId,
            messageType = messageType,
            text = text,
            attachments = attachments,
            replyToId = replyToId,
            localEvent = localEvent,
            groupName = groupName,
            context = context,
            scope = scope,
            messages = _messages,
            onStatusUpdate = ::updateLocalMessageStatus
        )
    }

    // ── Local event append ────────────────────────────────────────────────────

    /**
     * Appends an event generated locally on device (e.g. voice recorder or payment prompt).
     */
    fun appendLocalEvent(event: ThreadEvent, groupName: String, context: Context) {
        _messages.value = _messages.value + event
        val previewSnippet = when (event.type) {
            ThreadBubbleType.VOICE -> "🎤 Voice message"
            ThreadBubbleType.IMAGE -> if (event.text.isNotBlank()) "📷 ${event.text}" else "📷 Photo"
            ThreadBubbleType.DOCUMENT -> "📄 Document"
            ThreadBubbleType.CONTACT -> "👤 Contact"
            ThreadBubbleType.DEPOSIT -> "💰 Contribution update"
            else -> event.text.ifBlank { "Sent a message" }
        }
        UserGroupsRepository.updateLatestMessage(groupName, previewSnippet, event.time, context)
    }

    // ── Live/incoming delegates ───────────────────────────────────────────────

    /** Called by the WebSocket manager when a new message arrives. */
    fun onLiveMessageReceived(dto: GroupChatMessageDto, groupName: String, context: Context) {
        val currentUserId = resolveCurrentUserId(context)
        GroupChatLiveReceiver.onLiveMessageReceived(
            dto = dto,
            groupName = groupName,
            context = context,
            currentUserId = currentUserId,
            activeGroupId = activeGroupId,
            knownServerIds = knownServerIds,
            messages = _messages,
            onMarkRead = { gid, gname, ctx -> markMessagesAsRead(gid, gname, ctx) }
        )
    }

    /** Marks messages as read on the backend and locally. */
    fun markMessagesAsRead(groupId: String, groupName: String? = null, context: Context? = null) {
        GroupChatLiveReceiver.markMessagesAsRead(
            groupId = groupId,
            groupName = groupName ?: activeGroupName,
            context = context,
            scope = scope
        )
    }

    /** Updates local status when the peer sends a read receipt. */
    fun onMessageReadByPeer(messageId: String) {
        GroupChatLiveReceiver.onMessageReadByPeer(messageId, _messages)
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private fun updateLocalMessageStatus(eventId: Int, newStatus: MessageStatus) {
        GroupChatLiveReceiver.updateLocalMessageStatus(eventId, newStatus, knownServerIds, _messages)
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /** Resets all state. Call when the user navigates away from any group. */
    fun clear() {
        _messages.value = emptyList()
        knownServerIds.clear()
        activeGroupId = null
        activeGroupName = ""
        oldestMessageTimestamp = null
        _hasMoreOlder.value = true
        _isLoading.value = false
        _isLoadingOlder.value = false
    }
}

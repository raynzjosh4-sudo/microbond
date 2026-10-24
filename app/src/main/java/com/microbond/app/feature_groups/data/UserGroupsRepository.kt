package com.microbond.app.feature_groups.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microbond.app.core.data_classes_and_enums.GroupAdminSettings
import com.microbond.app.core.data_classes_and_enums.GroupMemberSummary
import com.microbond.app.core.data_classes_and_enums.GroupPrivateSettings
import com.microbond.app.core.data_classes_and_enums.GroupSavingsType
import com.microbond.app.core.data_classes_and_enums.MemberSaveStatus
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.data_classes_and_enums.TargetSavingsGroupData
import com.microbond.app.core.dummydata.SampleGroup
import com.microbond.app.core.network.ActionMessage
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.network.GroupListSummaryResponse
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.feature_groups.ui.newgroup.NewGroupSetup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Lightweight DTO for safely serializing and deserializing joined groups to SharedPreferences.
 */
data class SavedActionMessageDto(
    val text: String = "",
    val type: String = "GENERAL",
    val hasBlueTicks: Boolean = true,
    val isAlert: Boolean = false,
    val icon: String? = null
)

data class SavedGroupDto(
    val id: String = "",
    val name: String = "",
    val initials: String = "",
    val latestMessage: String = "",
    val time: String = "",
    val description: String = "",
    val currencyCode: String = "UGX",
    val totalBalance: Long = 0L,
    val memberCount: Int = 1,
    val typeName: String = "GOAL_SAVINGS",
    val unreadCount: Int = 0,
    val isFavorite: Boolean = false,
    val isGroup: Boolean = true,
    val avatarColorHex: Long = 0xFF00A884L,
    val groupIconUri: String? = null,
    val isOutgoingMessage: Boolean = true,
    val messageStatusName: String = "DELIVERED",
    val memberStatusesList: List<String> = emptyList(),
    val actionMessagesList: List<SavedActionMessageDto> = emptyList(),
    val userRole: String? = null,
    val isMember: Boolean = true
)

fun SavedGroupDto.toSampleGroup(): SampleGroup {
    val grpType = try {
        SavingsGroupType.valueOf(typeName)
    } catch (_: Exception) {
        SavingsGroupType.GOAL_SAVINGS
    }
    val color = if (avatarColorHex != 0L) Color(avatarColorHex) else MicrobondActiveGreen
    val msgStatus = try {
        MessageStatus.valueOf(messageStatusName)
    } catch (_: Exception) {
        MessageStatus.DELIVERED
    }
    val memStatuses = memberStatusesList.mapNotNull {
        try { MemberSaveStatus.valueOf(it) } catch (_: Exception) { null }
    }
    return SampleGroup(
        id = id.ifBlank { "group_${System.currentTimeMillis()}" },
        name = name,
        initials = initials.ifBlank { name.take(1).uppercase().ifBlank { "G" } },
        latestMessage = latestMessage.ifBlank { "No messages yet" },
        time = time,
        description = description.ifBlank { "$name circle" },
        currencyCode = currencyCode.ifBlank { "UGX" },
        totalBalance = totalBalance,
        memberCount = memberCount.coerceAtLeast(1),
        type = grpType,
        purposeData = TargetSavingsGroupData(
            goalAmount = totalBalance,
            targetDate = "",
            progressPercent = 0.0,
            savingsPace = "On track"
        ),
        members = emptyList(),
        privateSettings = GroupPrivateSettings(),
        adminSettings = GroupAdminSettings(),
        groupIconUri = groupIconUri,
        unreadCount = unreadCount,
        isOutgoingMessage = isOutgoingMessage,
        isFavorite = isFavorite,
        isGroup = isGroup,
        avatarColor = color,
        messageStatus = msgStatus,
        groupType = if (grpType == SavingsGroupType.MERRY_GO_ROUND) GroupSavingsType.SHORT_TERM else GroupSavingsType.LONG_TERM,
        memberStatuses = memStatuses,
        actionMessages = actionMessagesList.map {
            ActionMessage(
                text = it.text,
                type = it.type,
                hasBlueTicks = it.hasBlueTicks,
                isAlert = it.isAlert,
                icon = it.icon
            )
        },
        userRole = userRole,
        isMember = isMember
    )
}

fun SampleGroup.toSavedDto(): SavedGroupDto {
    val colorHex = try {
        avatarColor.value.toLong()
    } catch (_: Exception) {
        0xFF00A884L
    }
    return SavedGroupDto(
        id = id,
        name = name,
        initials = initials,
        latestMessage = latestMessage,
        time = time,
        description = description,
        currencyCode = currencyCode,
        totalBalance = totalBalance,
        memberCount = memberCount,
        typeName = type.name,
        unreadCount = unreadCount,
        isFavorite = isFavorite,
        isGroup = isGroup,
        avatarColorHex = colorHex,
        groupIconUri = groupIconUri,
        isOutgoingMessage = isOutgoingMessage,
        messageStatusName = messageStatus.name,
        memberStatusesList = memberStatuses.map { it.name },
        actionMessagesList = actionMessages.map {
            SavedActionMessageDto(
                text = it.text,
                type = it.type,
                hasBlueTicks = it.hasBlueTicks,
                isAlert = it.isAlert,
                icon = it.icon
            )
        },
        userRole = userRole,
        isMember = isMember
    )
}

fun GroupListSummaryResponse.toSampleGroup(): SampleGroup {
    val isRotating = groupType.equals("ROTATING", ignoreCase = true)
    val lastMsg = lastMessage
    val actionMsg = actionMessages.firstOrNull()

    val displayText: String
    val isOutgoing: Boolean
    val msgStatus: MessageStatus

    if (lastMsg != null) {
        isOutgoing = lastMsg.isOutgoing
        val typeToCheck = (lastMsg.mediaType ?: lastMsg.messageType).uppercase()
        val baseText = when (typeToCheck) {
            "VOICE", "AUDIO" -> "🎤 Voice note"
            "IMAGE" -> if (!lastMsg.text.isNullOrBlank()) "📷 ${lastMsg.text}" else "📷 Photo"
            "VIDEO" -> if (!lastMsg.text.isNullOrBlank()) "🎥 ${lastMsg.text}" else "🎥 Video"
            "DOCUMENT", "FILE", "PDF" -> "📄 Document"
            "DEPOSIT", "PAYMENT" -> "💰 Contribution update"
            "POLL" -> "📊 Poll"
            "LOCATION" -> "📍 Location"
            "CONTACT" -> "👤 Contact"
            else -> lastMsg.text?.ifBlank { null } ?: lastMsg.rawText?.ifBlank { null } ?: "Sent a message"
        }
        displayText = if (isOutgoing) {
            baseText
        } else if (!lastMsg.senderName.isNullOrBlank()) {
            "${lastMsg.senderName}: $baseText"
        } else {
            baseText
        }
        msgStatus = when (lastMsg.status.uppercase()) {
            "READ" -> MessageStatus.READ
            "DELIVERED" -> MessageStatus.DELIVERED
            "PENDING" -> MessageStatus.PENDING
            "FAILED" -> MessageStatus.FAILED
            else -> MessageStatus.SENT
        }
    } else if (actionMsg != null && actionMsg.text.isNotBlank()) {
        displayText = actionMsg.text
        isOutgoing = false
        msgStatus = if (actionMsg.hasBlueTicks) MessageStatus.READ else MessageStatus.DELIVERED
    } else {
        displayText = "No messages yet"
        isOutgoing = false
        msgStatus = MessageStatus.DELIVERED
    }

    val displayTime = when {
        lastMsg != null && lastMsg.timestamp > 0 -> formatTimestamp(lastMsg.timestamp)
        lastActivityTimestamp > 0 -> formatTimestamp(lastActivityTimestamp)
        else -> ""
    }

    // Compute circular status ring from ring_status
    val computedMemberStatuses = mutableListOf<MemberSaveStatus>()
    ringStatus?.let { r ->
        val paid = r.paidOnTimeCount.coerceAtLeast(0)
        val missed = r.missedDeadlineCount.coerceAtLeast(0)
        val total = r.totalExpectedMembers.coerceAtLeast(paid + missed)
        val pending = (total - paid - missed).coerceAtLeast(0)

        if (paid > 0 || missed > 0) {
            repeat(paid) { computedMemberStatuses.add(MemberSaveStatus.SAVED) }
            repeat(missed) { computedMemberStatuses.add(MemberSaveStatus.MISSED) }
            repeat(pending) { computedMemberStatuses.add(MemberSaveStatus.PENDING) }
        }
    }

    return SampleGroup(
        id = groupId,
        name = groupName,
        initials = groupName.take(1).uppercase().ifBlank { "G" },
        latestMessage = displayText,
        time = displayTime,
        description = "$groupName circle",
        currencyCode = "UGX",
        totalBalance = 0L,
        memberCount = ringStatus?.totalExpectedMembers?.takeIf { it > 0 } ?: 1,
        type = if (isRotating) SavingsGroupType.MERRY_GO_ROUND else SavingsGroupType.GOAL_SAVINGS,
        purposeData = TargetSavingsGroupData(
            goalAmount = 0L,
            targetDate = "",
            progressPercent = ringStatus?.percentage?.toDouble() ?: 0.0,
            savingsPace = "On track"
        ),
        members = emptyList(),
        privateSettings = GroupPrivateSettings(),
        adminSettings = GroupAdminSettings(),
        groupIconUri = avatarUrl,
        unreadCount = unreadCount,
        avatarColor = MicrobondActiveGreen,
        isOutgoingMessage = isOutgoing,
        messageStatus = msgStatus,
        isPublic = false,
        memberStatuses = computedMemberStatuses,
        actionMessages = actionMessages,
        userRole = "MEMBER",
        isMember = true
    )
}

fun SampleGroup.isMemberOrAdmin(): Boolean {
    val role = userRole?.trim()?.uppercase()
    return isMember && (role == "ADMIN" || role == "MEMBER" || role == "CREATOR" || role == "TREASURER" || role == "SECRETARY" || role == "CHAIRPERSON")
}

fun DiscoverableContactGroup.toJoinedSampleGroup(): SampleGroup {
    val isRotating = type.equals("ROTATING", ignoreCase = true)
    return SampleGroup(
        id = id.ifBlank { "group_${System.currentTimeMillis()}" },
        name = name,
        initials = name.take(1).uppercase().ifBlank { "G" },
        latestMessage = "Joined circle via contacts",
        time = "Just now",
        description = "$name savings circle",
        currencyCode = "UGX",
        totalBalance = 0L,
        memberCount = memberCount + 1,
        type = if (isRotating) SavingsGroupType.MERRY_GO_ROUND else SavingsGroupType.GOAL_SAVINGS,
        purposeData = TargetSavingsGroupData(
            goalAmount = 0L,
            targetDate = "",
            progressPercent = 0.0,
            savingsPace = "On track"
        ),
        members = emptyList(),
        privateSettings = GroupPrivateSettings(),
        adminSettings = GroupAdminSettings(),
        groupIconUri = groupImageUrl,
        unreadCount = 0,
        avatarColor = MicrobondActiveGreen,
        isPublic = false,
        memberStatuses = emptyList(),
        userRole = "MEMBER",
        isMember = true
    )
}

fun NewGroupSetup.toSampleGroup(): SampleGroup {
    val isRotating = groupType == SavingsGroupType.MERRY_GO_ROUND
    return SampleGroup(
        id = groupId?.ifBlank { null } ?: "group_${System.currentTimeMillis()}",
        name = name,
        initials = name.take(1).uppercase().ifBlank { "G" },
        latestMessage = "Circle created",
        time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
        description = "$name savings circle",
        currencyCode = "UGX",
        totalBalance = 0L,
        memberCount = participants.size + 1,
        type = groupType,
        purposeData = TargetSavingsGroupData(
            goalAmount = 0L,
            targetDate = "",
            progressPercent = 0.0,
            savingsPace = "On track"
        ),
        members = participants.map {
            GroupMemberSummary(
                name = it.name,
                phoneNumber = it.subtitle.filter(Char::isDigit).ifBlank { "0000000000" },
                role = "Member",
                trustScore = 100,
                contributionStatus = MemberSaveStatus.SAVED
            )
        },
        privateSettings = GroupPrivateSettings(),
        adminSettings = GroupAdminSettings(),
        unreadCount = 0,
        avatarColor = MicrobondActiveGreen,
        isPublic = false,
        memberStatuses = emptyList(),
        userRole = "ADMIN",
        isMember = true
    )
}

fun formatTimestamp(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    val millis = if (timestamp < 10_000_000_000L) timestamp * 1000L else timestamp
    val date = Date(millis)
    val now = Date()
    val diffMillis = now.time - date.time
    val diffHours = diffMillis / (1000 * 60 * 60)
    return when {
        diffHours < 24 -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        diffHours < 48 -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}

/**
 * In-memory & persisted repository for user joined groups shown on the Chart / Chat screen.
 * Keeps joined groups alive across thread transitions, navigation tabs, and app sessions
 * without flickering or re-fetching on every screen appearance.
 */
object UserGroupsRepository {
    private const val PREFS_NAME = "user_groups_prefs"
    private const val KEY_SAVED_GROUPS = "saved_joined_groups"

    private var appContext: Context? = null
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _joinedGroups = MutableStateFlow<List<SampleGroup>>(emptyList())
    val joinedGroups: StateFlow<List<SampleGroup>> = _joinedGroups.asStateFlow()

    private val _totalUnreadCount = MutableStateFlow(0)
    val totalUnreadCount: StateFlow<Int> = _totalUnreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasFetched = MutableStateFlow(false)
    val hasFetched: StateFlow<Boolean> = _hasFetched.asStateFlow()

    /**
     * Initializes the repository from local storage so joined groups are available immediately
     * in memory on application launch.
     */
    fun load(context: Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawJson = prefs.getString(KEY_SAVED_GROUPS, null)
        if (!rawJson.isNullOrBlank()) {
            try {
                val type = object : TypeToken<List<SavedGroupDto>>() {}.type
                val dtos: List<SavedGroupDto> = gson.fromJson(rawJson, type)
                if (!dtos.isNullOrEmpty()) {
                    val groups = dtos.map { it.toSampleGroup() }
                        .filter { it.isMemberOrAdmin() }
                    _joinedGroups.value = groups
                    _totalUnreadCount.value = groups.sumOf { it.unreadCount }
                    persist(context, groups)
                }
            } catch (e: Exception) {
                android.util.Log.e("UserGroupsRepository", "Failed to deserialize saved groups", e)
            }
        }
    }

    /**
     * Adds or updates a group in memory and persists it to local storage.
     */
    fun saveGroup(group: SampleGroup, context: Context? = null) {
        val targetContext = context?.applicationContext ?: appContext
        if (!group.isMemberOrAdmin()) {
            deleteGroups(setOf(group.name), targetContext)
            return
        }
        val current = _joinedGroups.value.toMutableList()
        val index = current.indexOfFirst {
            it.name.trim().equals(group.name.trim(), ignoreCase = true) ||
            (it.id.isNotBlank() && it.id == group.id)
        }
        if (index >= 0) {
            current[index] = group
        } else {
            current.add(0, group)
        }
        _joinedGroups.value = current
        _totalUnreadCount.value = current.sumOf { it.unreadCount }
        targetContext?.let { persist(it, current) }
    }

    fun addGroup(group: SampleGroup, context: Context? = null) {
        saveGroup(group, context)
    }

    fun addGroupFromSetup(setup: NewGroupSetup, context: Context? = null) {
        saveGroup(setup.toSampleGroup(), context)
    }

    fun addGroupFromDiscovered(discovered: DiscoverableContactGroup, context: Context? = null) {
        saveGroup(discovered.toJoinedSampleGroup(), context)
    }

    /**
     * Updates the latest message snippet and timestamp for a group in memory and storage,
     * bringing the group to the top of the chat list.
     */
    fun updateLatestMessage(
        groupName: String,
        message: String,
        time: String,
        context: Context? = null
    ) {
        val targetContext = context?.applicationContext ?: appContext
        val current = _joinedGroups.value.toMutableList()
        val index = current.indexOfFirst { it.name.trim().equals(groupName.trim(), ignoreCase = true) }
        if (index >= 0) {
            val updated = current[index].copy(
                latestMessage = message,
                time = time
            )
            current.removeAt(index)
            current.add(0, updated)
            _joinedGroups.value = current
            targetContext?.let { persist(it, current) }
        }
    }

    /**
     * Marks a group's unread messages as read in memory, on disk, and on the backend server.
     */
    fun markGroupAsRead(groupName: String, context: Context? = null) {
        markGroupAsRead(groupName = groupName, groupId = null, context = context)
    }

    /**
     * Marks a group's unread messages as read in memory, on disk, and on the backend server.
     * @param groupName Name of group
     * @param groupId Backend UUID of group (if known)
     */
    fun markGroupAsRead(groupName: String, groupId: String?, context: Context? = null) {
        val targetContext = context?.applicationContext ?: appContext
        val current = _joinedGroups.value.toMutableList()
        val index = current.indexOfFirst {
            (!groupId.isNullOrBlank() && it.id == groupId) ||
            it.name.trim().equals(groupName.trim(), ignoreCase = true)
        }

        var resolvedGroupId = groupId
        if (index >= 0) {
            if (resolvedGroupId.isNullOrBlank()) {
                resolvedGroupId = current[index].id
            }
            if (current[index].unreadCount > 0) {
                current[index] = current[index].copy(unreadCount = 0)
                _joinedGroups.value = current
                _totalUnreadCount.value = current.sumOf { it.unreadCount }
                targetContext?.let { persist(it, current) }
            }
        }

        // Send mark-read receipt to backend API
        if (!resolvedGroupId.isNullOrBlank() && !resolvedGroupId.startsWith("group_")) {
            scope.launch {
                try {
                    val response = NetworkClient.apiService.markMessagesRead(resolvedGroupId)
                    if (response.isSuccessful) {
                        android.util.Log.d("UserGroupsRepository", "Marked messages read for group $resolvedGroupId (server count: ${response.body()?.markedCount})")
                    } else {
                        android.util.Log.w("UserGroupsRepository", "markMessagesRead failed: HTTP ${response.code()}")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("UserGroupsRepository", "Exception calling markMessagesRead for $resolvedGroupId", e)
                }
            }
        }
    }

    /**
     * Marks all groups as read in memory and on disk.
     */
    fun markAllAsRead(context: Context? = null) {
        val targetContext = context?.applicationContext ?: appContext
        val current = _joinedGroups.value.map { it.copy(unreadCount = 0) }
        _joinedGroups.value = current
        _totalUnreadCount.value = 0
        targetContext?.let { persist(it, current) }
    }

    /**
     * Deletes one or more groups by name from memory and persistent storage.
     */
    fun deleteGroups(names: Set<String>, context: Context? = null) {
        val targetContext = context?.applicationContext ?: appContext
        val updated = _joinedGroups.value.filterNot { it.name in names }
        _joinedGroups.value = updated
        _totalUnreadCount.value = updated.sumOf { it.unreadCount }
        targetContext?.let { persist(it, updated) }
    }

    /**
     * Syncs user joined groups and total unread count from the backend in the background
     * without blowing away currently displayed in-memory groups.
     */
    suspend fun syncWithBackend(context: Context? = null, force: Boolean = false) {
        val targetContext = context?.applicationContext ?: appContext
        if (_isLoading.value) return
        _isLoading.value = true
        try {
            // 1. Fetch group dashboard summaries with latest messages and unread counts
            val response = NetworkClient.apiService.getGroupDashboardSummaries()
            if (response.isSuccessful && response.body() != null) {
                val apiGroups = response.body()!!.map { it.toSampleGroup() }
                mergeAndSetGroups(apiGroups, targetContext)
                _hasFetched.value = true
            }

            // 2. Fetch total unread count from unread-count endpoint
            try {
                val unreadResponse = NetworkClient.apiService.getTotalUnreadCount()
                if (unreadResponse.isSuccessful && unreadResponse.body() != null) {
                    _totalUnreadCount.value = unreadResponse.body()!!.totalUnreadCount
                } else {
                    _totalUnreadCount.value = _joinedGroups.value.sumOf { it.unreadCount }
                }
            } catch (_: Exception) {
                _totalUnreadCount.value = _joinedGroups.value.sumOf { it.unreadCount }
            }
        } catch (e: Exception) {
            android.util.Log.e("UserGroupsRepository", "Failed to sync groups from API", e)
        } finally {
            _isLoading.value = false
        }
    }

    private fun mergeAndSetGroups(apiGroups: List<SampleGroup>, context: Context?) {
        val current = _joinedGroups.value
        val currentMapByName = current.associateBy { it.name.trim().lowercase() }
        val currentMapById = current.filter { it.id.isNotBlank() }.associateBy { it.id }

        // Preserve locally managed fields like isFavorite and actionMessages fallback
        val enrichedApiGroups = apiGroups.map { apiGroup ->
            val existing = currentMapById[apiGroup.id] ?: currentMapByName[apiGroup.name.trim().lowercase()]
            if (existing != null) {
                apiGroup.copy(
                    isFavorite = existing.isFavorite,
                    actionMessages = if (apiGroup.actionMessages.isNotEmpty()) apiGroup.actionMessages else existing.actionMessages,
                    userRole = existing.userRole ?: "MEMBER",
                    isMember = true,
                    groupIconUri = apiGroup.groupIconUri?.takeIf { it.isNotBlank() } ?: existing.groupIconUri
                )
            } else {
                apiGroup.copy(isMember = true, userRole = "MEMBER")
            }
        }.filter { it.isMemberOrAdmin() }

        val apiNames = enrichedApiGroups.map { it.name.trim().lowercase() }.toSet()
        val apiIds = enrichedApiGroups.map { it.id }.filter { it.isNotBlank() }.toSet()

        // Preserve ONLY groups created locally by the current user (ADMIN role)
        // that have not yet been returned by the backend. DO NOT keep unjoined or discovered groups.
        val localOnly = current.filter { localGroup ->
            val isLocalAdmin = localGroup.userRole.equals("ADMIN", ignoreCase = true)
            val notYetOnServer = localGroup.name.trim().lowercase() !in apiNames && (localGroup.id.isBlank() || localGroup.id !in apiIds)
            isLocalAdmin && notYetOnServer && localGroup.isMemberOrAdmin()
        }

        val merged = (enrichedApiGroups + localOnly).filter { it.isMemberOrAdmin() }
        _joinedGroups.value = merged
        _totalUnreadCount.value = merged.sumOf { it.unreadCount }
        context?.let { persist(it, merged) }
    }

    private fun persist(context: Context, groups: List<SampleGroup>) {
        try {
            val dtos = groups.map { it.toSavedDto() }
            val json = gson.toJson(dtos)
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_SAVED_GROUPS, json)
                .apply()
        } catch (e: Exception) {
            android.util.Log.e("UserGroupsRepository", "Failed to persist groups", e)
        }
    }

    fun findByName(name: String): SampleGroup? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null
        return _joinedGroups.value.firstOrNull { it.name.trim().equals(trimmed, ignoreCase = true) }
    }

    fun findById(id: String): SampleGroup? {
        if (id.isBlank()) return null
        return _joinedGroups.value.firstOrNull { it.id == id }
    }

    /**
     * Updates group profile (name, description, or groupIconUri) in memory and on disk.
     */
    fun updateGroupProfile(
        idOrName: String,
        name: String? = null,
        description: String? = null,
        iconUri: String? = null,
        context: Context? = null
    ) {
        val targetContext = context?.applicationContext ?: appContext
        val updated = _joinedGroups.value.map { group ->
            if (group.id == idOrName || group.name.equals(idOrName, ignoreCase = true)) {
                group.copy(
                    name = name?.takeIf { it.isNotBlank() } ?: group.name,
                    description = description ?: group.description,
                    groupIconUri = if (iconUri == "") null else (iconUri ?: group.groupIconUri)
                )
            } else {
                group
            }
        }
        _joinedGroups.value = updated
        targetContext?.let { persist(it, updated) }
    }
}


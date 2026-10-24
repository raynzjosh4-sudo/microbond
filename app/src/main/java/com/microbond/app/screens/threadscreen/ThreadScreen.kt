package com.microbond.app.screens.threadscreen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.microbond.app.R
import com.microbond.app.feature_groups.data.groupchartrepostory.GroupChatRepository
import com.microbond.app.feature_groups.data.UserGroupsRepository
import com.microbond.app.feature_groups.data.toJoinedSampleGroup
import com.microbond.app.core.network.api.deposits.DepositWidgetRepository
import com.microbond.app.core.network.chat.GroupChatWebSocketClient
import com.microbond.app.core.components.threadbubbles.bubblewallpaper.ThreadWallpaper
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.ContactSendItem
import com.microbond.app.core.components.threadbubbles.threads.rememberThreadSelectionState
import com.microbond.app.core.components.usersheet.UserProfileSheetData
import com.microbond.app.core.data_classes_and_enums.AttachmentOption
import com.microbond.app.core.data_classes_and_enums.DocumentData
import com.microbond.app.core.data_classes_and_enums.GroupAdminSettings
import com.microbond.app.core.data_classes_and_enums.GroupPrivateSettings
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.data_classes_and_enums.TargetSavingsGroupData
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.payment.PaymentContext
import com.microbond.app.core.payment.PaymentFlowManager
import com.microbond.app.core.payment.PaymentFlowState
import com.microbond.app.core.payment.PaymentMethod
import com.microbond.app.core.payment.PaymentStep
import com.microbond.app.core.components.groupintrocard.rotating.RotatingGroupPreviewUiState
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.network.DiscoveredGroupsRepository
import com.microbond.app.core.network.RotatingGroupRepository
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.dummydata.SampleGroup
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.GroupJoinRequest
import com.microbond.app.core.network.api.goupapis.UpdateGroupProfileRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondCardSurfaceDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.feature_groups.ui.addmember.AddGroupMemberScreen
import com.microbond.app.feature_settings.groupsetttingscreen.GroupSettingsScreen
import com.microbond.app.core.network.api.deposits.DepositStatusFilter
import com.microbond.app.screens.threadscreen.threadscreenwidgets.ThreadAttachmentHandler
import com.microbond.app.screens.threadscreen.threadscreenwidgets.ThreadAttachmentPickerOverlay
import com.microbond.app.screens.threadscreen.threadscreenwidgets.ThreadBottomSheets
import com.microbond.app.screens.threadscreen.threadscreenwidgets.ThreadBubbleList
import com.microbond.app.screens.threadscreen.threadscreenwidgets.ThreadInputSection
import com.microbond.app.screens.threadscreen.threadscreenwidgets.ThreadScreenTopBar
import com.microbond.app.screens.threadscreen.threadscreenwidgets.rememberThreadVoiceRecorder
import com.microbond.app.screens.threadscreen.threadscreenwidgets.replyPreviewText
import kotlinx.coroutines.launch
import safePainterResource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * High-level coordinator screen for a Group or Direct Message thread.
 * Coordinates TopBar, Chat Message List, Input Section, Attachment Picker, and Modal Sheets.
 */
@Composable
fun ThreadScreen(
    entityName: String,
    groupId: String? = null,
    groupType: SavingsGroupType? = null,
    discoveredGroup: DiscoverableContactGroup? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val effectiveGroupType = groupType ?: SavingsGroupType.MERRY_GO_ROUND
    val memberAvatar = safePainterResource(R.drawable.ic_account_box)
    val resolvedDiscoveredGroup = remember(entityName, discoveredGroup) {
        discoveredGroup ?: DiscoveredGroupsRepository.findByName(entityName)
    }

    val effectiveGroupId = remember(groupId, entityName, resolvedDiscoveredGroup) {
        groupId?.takeIf { it.isNotBlank() }
            ?: resolvedDiscoveredGroup?.id?.takeIf { it.isNotBlank() }
            ?: resolvedDiscoveredGroup?.groupId?.takeIf { it.isNotBlank() }
            ?: UserGroupsRepository.findByName(entityName)?.id?.takeIf { it.isNotBlank() }
            ?: ""
    }

    var rotatingPreviewState by remember(entityName, resolvedDiscoveredGroup) {
        mutableStateOf(
            if (resolvedDiscoveredGroup != null) {
                RotatingGroupPreviewUiState.fromDiscoverableGroup(resolvedDiscoveredGroup)
            } else {
                RotatingGroupPreviewUiState.fromGroupNameOnly(entityName)
            }
        )
    }

    val rotatingGroupRepo = remember { RotatingGroupRepository() }
    LaunchedEffect(entityName, resolvedDiscoveredGroup?.id) {
        val targetGroupId = resolvedDiscoveredGroup?.id ?: resolvedDiscoveredGroup?.groupId
        if (!targetGroupId.isNullOrBlank()) {
            val result = rotatingGroupRepo.fetchRotatingGroupDetails(targetGroupId)
            result.getOrNull()?.let { liveState ->
                rotatingPreviewState = liveState
            }
        }
    }

    val joinedGroups by UserGroupsRepository.joinedGroups.collectAsState()
    val localJoinedGroup = remember(effectiveGroupId, entityName, joinedGroups) {
        joinedGroups.find {
            (effectiveGroupId.isNotBlank() && it.id == effectiveGroupId) ||
            it.name.trim().equals(entityName.trim(), ignoreCase = true)
        }
    }

    var isMember by remember(effectiveGroupId, localJoinedGroup, resolvedDiscoveredGroup) {
        mutableStateOf(localJoinedGroup?.isMember ?: (resolvedDiscoveredGroup == null && effectiveGroupId.isBlank()))
    }
    var userRole by remember(effectiveGroupId, localJoinedGroup) {
        mutableStateOf(localJoinedGroup?.userRole)
    }
    var groupDescription by remember(entityName, localJoinedGroup, resolvedDiscoveredGroup) {
        mutableStateOf(localJoinedGroup?.description?.takeIf { it.isNotBlank() }
            ?: resolvedDiscoveredGroup?.description?.takeIf { it.isNotBlank() })
    }
    // Mutable display state – updated instantly when user edits name/icon via GroupSettingsScreen
    var displayedGroupName by remember(entityName, localJoinedGroup) {
        mutableStateOf(localJoinedGroup?.name?.takeIf { it.isNotBlank() } ?: entityName)
    }
    var displayedGroupIconUri by remember(localJoinedGroup) {
        mutableStateOf(localJoinedGroup?.groupIconUri)
    }
    var membersCanInviteViaLink by remember { mutableStateOf(false) }
    var membersCanEditSettings by remember { mutableStateOf(false) }
    var membersCanSendMessages by remember { mutableStateOf(true) }
    var adminsMustApproveNewMembers by remember { mutableStateOf(false) }
    var isJoinPending by remember { mutableStateOf(false) }
    var inviteCode by remember { mutableStateOf<String?>(null) }
    var memberCount by remember(localJoinedGroup, resolvedDiscoveredGroup) {
        mutableIntStateOf(localJoinedGroup?.memberCount ?: resolvedDiscoveredGroup?.memberCount ?: 1)
    }
    var isCreator by remember(localJoinedGroup) { mutableStateOf(localJoinedGroup != null) }
    var isJoinLoading by remember { mutableStateOf(false) }
    var showAddDescriptionDialog by remember { mutableStateOf(false) }
    var newDescriptionInput by remember { mutableStateOf("") }
    var isSavingDescription by remember { mutableStateOf(false) }
    var showAddMemberSheet by remember { mutableStateOf(false) }

    LaunchedEffect(effectiveGroupId) {
        if (effectiveGroupId.isNotBlank()) {
            try {
                val permRes = NetworkClient.apiService.getGroupPermissions(effectiveGroupId)
                if (permRes.isSuccessful && permRes.body()?.data != null) {
                    val p = permRes.body()!!.data!!
                    membersCanEditSettings = p.membersCanEditSettings
                    membersCanSendMessages = p.membersCanSendMessages
                    membersCanInviteViaLink = p.membersCanInviteViaLink
                    adminsMustApproveNewMembers = p.adminsMustApproveNewMembers
                }
            } catch (e: Exception) {
                android.util.Log.w("ThreadScreen", "Failed to fetch group permissions: ${e.message}")
            }

            try {
                val res = NetworkClient.apiService.getGroupSettingsSummary(effectiveGroupId)
                if (res.isSuccessful) {
                    val summary = res.body()
                    if (summary != null) {
                        if (summary.isMember != null) {
                            isMember = summary.isMember
                        } else if (!summary.userRole.isNullOrBlank()) {
                            isMember = true
                        }
                        if (!summary.userRole.isNullOrBlank()) {
                            userRole = summary.userRole
                        }
                        if (!summary.description.isNullOrBlank()) {
                            groupDescription = summary.description
                        }
                        if (summary.memberCount > 0) {
                            memberCount = summary.memberCount
                        }
                        if (!summary.inviteCode.isNullOrBlank()) {
                            inviteCode = summary.inviteCode
                        }
                        val currentUid = AuthSessionManager.currentUserId
                        if (!currentUid.isNullOrBlank() && !summary.createdByUserId.isNullOrBlank()) {
                            isCreator = summary.createdByUserId == currentUid
                        }
                        summary.governanceGates.let { gov ->
                            membersCanInviteViaLink = gov.membersCanInviteViaLink
                            membersCanEditSettings = gov.membersCanEditSettings
                        }
                        val isUserMemberOrAdmin = summary.isMember == true || summary.userRole?.uppercase() in setOf("ADMIN", "MEMBER")
                        if (!isUserMemberOrAdmin) {
                            UserGroupsRepository.deleteGroups(setOf(entityName), context)
                        } else if (localJoinedGroup != null) {
                            UserGroupsRepository.saveGroup(
                                localJoinedGroup.copy(
                                    isMember = true,
                                    userRole = summary.userRole ?: localJoinedGroup.userRole ?: "MEMBER"
                                ),
                                context
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("ThreadScreen", "Failed to fetch group summary: ${e.message}")
            }
        }
    }

    // ── State ────────────────────────────────────────────────────────────────
    var inputValue by remember { mutableStateOf("") }
    var replyingToEvent by remember { mutableStateOf<ThreadEvent?>(null) }
    var selectedDepositEvent by remember { mutableStateOf<ThreadEvent?>(null) }
    var selectedDepositFilter by remember { mutableStateOf<DepositStatusFilter?>(null) }
    var selectedUserSheet by remember { mutableStateOf<UserProfileSheetData?>(null) }
    var weeklyTapLabel by remember { mutableStateOf<String?>(null) }
    var weeklyTapValue by remember { mutableStateOf(0) }
    var showAttachmentPicker by remember { mutableStateOf(false) }
    var isAttachmentPickerExpanded by remember { mutableStateOf(false) }
    var attachmentPanelHeight by remember { mutableStateOf(0.dp) }
    var showGroupSettings by remember { mutableStateOf(false) }
    var showRotatingGroupDetailsSheet by remember { mutableStateOf(false) }
    var selectedAttachmentOption by remember { mutableStateOf<AttachmentOption?>(null) }
    var attachmentPreviewSummary by remember { mutableStateOf<String?>(null) }
    var selectedContactPreview by remember { mutableStateOf<List<ContactSendItem>>(emptyList()) }
    var selectedDocument by remember { mutableStateOf<DocumentData?>(null) }
    var documentCaption by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val selectionState = rememberThreadSelectionState()
    var highlightedReplyEventId by remember { mutableStateOf<Int?>(null) }
    var replyHighlightRequest by remember { mutableIntStateOf(0) }

    // ── Live Group Chat State & WebSocket ────────────────────────────────────
    val threadEvents by GroupChatRepository.messages.collectAsState()
    val isLoading by GroupChatRepository.isLoading.collectAsState()
    val isLoadingOlder by GroupChatRepository.isLoadingOlder.collectAsState()
    val hasMoreOlder by GroupChatRepository.hasMoreOlder.collectAsState()

    LaunchedEffect(effectiveGroupId, entityName) {
        GroupChatRepository.loadChat(effectiveGroupId, entityName, context)
        if (effectiveGroupId.isNotBlank()) {
            GroupChatWebSocketClient.connect(effectiveGroupId, entityName, context)
        }
    }

    DisposableEffect(effectiveGroupId) {
        onDispose {
            GroupChatWebSocketClient.disconnect()
        }
    }

    // Trigger pagination when scrolling near the end of loaded items (older messages)
    LaunchedEffect(listState.firstVisibleItemIndex, hasMoreOlder, isLoadingOlder) {
        val totalItems = threadEvents.size
        if (hasMoreOlder && !isLoadingOlder && totalItems > 10) {
            val lastVisibleIndex = listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size
            if (lastVisibleIndex >= totalItems - 5) {
                GroupChatRepository.fetchOlderMessages(context)
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    fun appendOutgoingEvent(event: ThreadEvent) {
        val replySource = replyingToEvent
        val finalEvent = if (replySource == null) event
        else event.copy(
            quotedSender = if (replySource.isCurrentUser) "You" else replySource.senderName ?: "Unknown",
            quotedText = replySource.replyPreviewText(),
            quotedEventId = replySource.id,
            quotedAccentColor = if (replySource.isCurrentUser) MicrobondActiveGreen else replySource.senderColor
        )
        if (replySource != null) replyingToEvent = null

        val replyToId = replySource?.messageId ?: replySource?.id?.toString()

        when (finalEvent.type) {
            ThreadBubbleType.TEXT, ThreadBubbleType.REPLY -> {
                GroupChatRepository.sendTextMessage(
                    groupId = effectiveGroupId,
                    text = finalEvent.text,
                    replyToEvent = replySource,
                    groupName = entityName,
                    context = context
                )
            }
            ThreadBubbleType.IMAGE -> {
                val uris = buildList {
                    finalEvent.imageUri?.let { add(Uri.parse(it)) }
                    finalEvent.imageUris?.forEach { add(Uri.parse(it)) }
                }
                if (uris.isNotEmpty()) {
                    GroupChatRepository.sendMediaFromUris(
                        groupId = effectiveGroupId,
                        uris = uris,
                        mediaType = "IMAGE",
                        caption = finalEvent.text.takeIf { it.isNotBlank() },
                        replyToId = replyToId,
                        groupName = entityName,
                        context = context
                    )
                } else {
                    GroupChatRepository.appendLocalEvent(finalEvent, entityName, context)
                }
            }
            ThreadBubbleType.VOICE -> {
                val vUri = finalEvent.voiceUri
                if (!vUri.isNullOrBlank()) {
                    GroupChatRepository.sendMediaFromUri(
                        groupId = effectiveGroupId,
                        uri = Uri.parse(vUri),
                        mediaType = "VOICE_NOTE",
                        caption = null,
                        replyToId = replyToId,
                        groupName = entityName,
                        context = context
                    )
                } else {
                    GroupChatRepository.appendLocalEvent(finalEvent, entityName, context)
                }
            }
            ThreadBubbleType.DOCUMENT -> {
                val docUri = finalEvent.documentData?.uri
                if (!docUri.isNullOrBlank()) {
                    GroupChatRepository.sendMediaFromUri(
                        groupId = effectiveGroupId,
                        uri = Uri.parse(docUri),
                        mediaType = "DOCUMENT",
                        caption = finalEvent.text.takeIf { it.isNotBlank() },
                        replyToId = replyToId,
                        groupName = entityName,
                        context = context
                    )
                } else {
                    GroupChatRepository.appendLocalEvent(finalEvent, entityName, context)
                }
            }
            ThreadBubbleType.POLL -> {
                val poll = finalEvent.pollData
                if (poll != null) {
                    val attachments = mapOf(
                        "question" to poll.question,
                        "subtitle" to poll.questionSubtitle,
                        "is_multiple_choice" to poll.isMultipleChoice,
                        "options" to poll.options.map { mapOf("label" to it.label, "percentage" to it.percentage) }
                    )
                    GroupChatRepository.sendStructuredMessage(
                        groupId = effectiveGroupId,
                        messageType = "POLL",
                        text = poll.question,
                        attachments = attachments,
                        replyToId = replyToId,
                        localEvent = finalEvent,
                        groupName = entityName,
                        context = context
                    )
                } else {
                    GroupChatRepository.appendLocalEvent(finalEvent, entityName, context)
                }
            }
            ThreadBubbleType.STAT_CUSTOM_CHART -> {
                val chart = finalEvent.statCustomChart
                if (chart != null) {
                    val attachments = mapOf(
                        "title" to chart.title,
                        "subtitle" to chart.subtitle,
                        "x_label" to chart.xLabel,
                        "y_label" to chart.yLabel,
                        "primary_color_value" to chart.primaryColorValue,
                        "secondary_color_value" to chart.secondaryColorValue,
                        "x_values" to chart.xValues,
                        "y_values" to chart.yValues
                    )
                    GroupChatRepository.sendStructuredMessage(
                        groupId = effectiveGroupId,
                        messageType = "STAT_CUSTOM_CHART",
                        text = chart.title,
                        attachments = attachments,
                        replyToId = replyToId,
                        localEvent = finalEvent,
                        groupName = entityName,
                        context = context
                    )
                } else {
                    GroupChatRepository.appendLocalEvent(finalEvent, entityName, context)
                }
            }
            ThreadBubbleType.DEPOSIT_STATUS -> {
                val attachments = mapOf(
                    "full_deposit_count" to finalEvent.fullDepositCount,
                    "half_deposit_count" to finalEvent.halfDepositCount,
                    "not_deposited_count" to finalEvent.notDepositedCount
                )
                GroupChatRepository.sendStructuredMessage(
                    groupId = effectiveGroupId,
                    messageType = "DEPOSIT_STATUS",
                    text = "Deposit Status",
                    attachments = attachments,
                    replyToId = replyToId,
                    localEvent = finalEvent,
                    groupName = entityName,
                    context = context
                )
            }
            else -> {
                GroupChatRepository.appendLocalEvent(
                    event = finalEvent,
                    groupName = entityName,
                    context = context
                )
            }
        }
    }

    // ── Payment ──────────────────────────────────────────────────────────────
    var paymentState by remember { mutableStateOf(PaymentFlowState()) }
    val paymentContext = remember(entityName) {
        PaymentContext(
            amountDue = 0L,
            amountDueDisplay = "UGX 0",
            partialAllowed = false,
            registeredPhone = "",
            registeredPhoneDisplay = ""
        )
    }

    fun appendPaymentPrompts(prompts: List<ThreadEvent>) {
        prompts.forEach { prompt ->
            GroupChatRepository.appendLocalEvent(prompt, entityName, context)
        }
        if (prompts.isNotEmpty()) coroutineScope.launch { listState.animateScrollToItem(0) }
    }

    // ── Voice Recording ──────────────────────────────────────────────────────
    val voiceRecorderState = rememberThreadVoiceRecorder { voiceUri, duration ->
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        appendOutgoingEvent(
            ThreadEvent(
                id = (threadEvents.maxOfOrNull { it.id } ?: 0) + 1,
                text = "%d:%02d".format(duration / 60, duration % 60),
                time = time,
                isCurrentUser = true,
                status = MessageStatus.SENT,
                type = ThreadBubbleType.VOICE,
                senderName = "You",
                senderInitials = "Y",
                senderColor = Color(0xFF00A884),
                voiceUri = voiceUri
            )
        )
        coroutineScope.launch { listState.animateScrollToItem(0) }
    }

    val showScrollToBottom by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 }
    }

    BackHandler(enabled = showGroupSettings || showAttachmentPicker || selectionState.isSelectionMode) {
        when {
            selectionState.isSelectionMode -> selectionState.clearSelection()
            showGroupSettings -> showGroupSettings = false
            showAttachmentPicker -> showAttachmentPicker = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ThreadScreenTopBar(
                    entityName = displayedGroupName,
                    selectionState = selectionState,
                    memberAvatar = memberAvatar,
                    groupIconUri = displayedGroupIconUri,
                    onNavigateBack = onNavigateBack,
                    onReplyFromSelection = { targetId ->
                        targetId?.let { id -> replyingToEvent = threadEvents.find { it.id == id } }
                    },
                    onShowGroupSettings = { showGroupSettings = true }
                )
            },
            containerColor = MicrobondDarkBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = attachmentPanelHeight.coerceAtMost(300.dp))
            ) {
                ThreadWallpaper(modifier = Modifier.fillMaxSize())
                Column(modifier = Modifier.fillMaxSize().imePadding()) {
                    // ── Message list ──
                    Box(modifier = Modifier.weight(1f)) {
                        ThreadBubbleList(
                            threadEvents = threadEvents,
                            listState = listState,
                            coroutineScope = coroutineScope,
                            selectionState = selectionState,
                            highlightedReplyEventId = highlightedReplyEventId,
                            replyHighlightRequest = replyHighlightRequest,
                            paymentState = paymentState,
                            showScrollToBottom = showScrollToBottom,
                            groupType = effectiveGroupType,
                            groupName = entityName,
                            discoveredGroup = resolvedDiscoveredGroup,
                            rotatingGroupState = rotatingPreviewState,
                            onOpenRotatingSheet = { showRotatingGroupDetailsSheet = true },
                            isMember = isMember,
                            userRole = userRole,
                            groupDescription = groupDescription,
                            groupIconUri = displayedGroupIconUri,
                            membersCanInviteViaLink = membersCanInviteViaLink,
                            membersCanEditSettings = membersCanEditSettings,
                            isCreator = isCreator,
                            memberCount = memberCount,
                            isJoinLoading = isJoinLoading,
                            isJoinPending = isJoinPending,
                            onJoinClick = {
                                coroutineScope.launch {
                                    if (effectiveGroupId.isBlank()) {
                                        Toast.makeText(context, "Group ID not found", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }
                                    isJoinLoading = true
                                    try {
                                        val joinRes = NetworkClient.apiService.joinGroup(
                                            GroupJoinRequest(
                                                groupId = effectiveGroupId,
                                                guarantorIds = emptyList(),
                                                inviteCode = inviteCode
                                            )
                                        )
                                        if (joinRes.isSuccessful) {
                                            val body = joinRes.body()
                                            val isPending = body?.membershipStatus.equals("PENDING", ignoreCase = true) ||
                                                            body?.requiresAdminApproval == true
                                            if (isPending) {
                                                isJoinPending = true
                                                Toast.makeText(context, "Join request submitted. Awaiting admin approval.", Toast.LENGTH_LONG).show()
                                            } else {
                                                isMember = true
                                                userRole = "MEMBER"
                                                memberCount += 1
                                                val joined = resolvedDiscoveredGroup?.toJoinedSampleGroup() ?: SampleGroup(
                                                    id = effectiveGroupId.ifBlank { "group_${System.currentTimeMillis()}" },
                                                    name = entityName,
                                                    initials = entityName.take(1).uppercase().ifBlank { "G" },
                                                    latestMessage = "You joined the group",
                                                    time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                                                    description = groupDescription ?: "$entityName circle",
                                                    currencyCode = "UGX",
                                                    totalBalance = 0L,
                                                    memberCount = memberCount,
                                                    type = effectiveGroupType,
                                                    purposeData = TargetSavingsGroupData(
                                                        goalAmount = 0L,
                                                        targetDate = "",
                                                        progressPercent = 0.0,
                                                        savingsPace = "On track"
                                                    ),
                                                    members = emptyList(),
                                                    privateSettings = GroupPrivateSettings(),
                                                    adminSettings = GroupAdminSettings(),
                                                    avatarColor = MicrobondActiveGreen
                                                )
                                                UserGroupsRepository.saveGroup(joined, context)
                                                Toast.makeText(context, "Joined $entityName!", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            val err = joinRes.errorBody()?.string() ?: joinRes.message()
                                            Toast.makeText(context, "Could not join: $err", Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Connection error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isJoinLoading = false
                                    }
                                }
                            },
                            onAddMembersClick = {
                                val isAdmin = userRole?.uppercase() == "ADMIN" || isCreator
                                if (!isAdmin) {
                                    Toast.makeText(context, "Only Group Admins can add members directly.", Toast.LENGTH_SHORT).show()
                                } else {
                                    showAddMemberSheet = true
                                }
                            },
                            onInviteLinkClick = {
                                val canInvite = userRole?.uppercase() == "ADMIN" || isCreator || membersCanInviteViaLink
                                if (!canInvite) {
                                    Toast.makeText(context, "Only admins can share invite links for this group.", Toast.LENGTH_SHORT).show()
                                } else {
                                    val code = inviteCode?.ifBlank { null } ?: effectiveGroupId.take(8).uppercase()
                                    val shareText = "Hey! Join our savings group \"$entityName\" on Microbond: microbond://join?code=$code"
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Invite via link"))
                                }
                            },
                            onAddDescriptionClick = {
                                val canEdit = userRole?.uppercase() == "ADMIN" || isCreator || membersCanEditSettings
                                if (!canEdit) {
                                    Toast.makeText(context, "Only group admins can edit this group's info.", Toast.LENGTH_SHORT).show()
                                } else {
                                    newDescriptionInput = groupDescription.orEmpty()
                                    showAddDescriptionDialog = true
                                }
                            },
                            onUserTap = { event ->
                                selectedUserSheet = UserProfileSheetData(
                                    name = event.senderName ?: if (event.isCurrentUser) "You" else "Unknown member",
                                    phone = event.senderPhone ?: "No phone on file",
                                    avatarRes = event.senderAvatarRes,
                                    avatarColor = event.senderColor,
                                    avatarUrl = event.senderAvatarUrl
                                )
                            },
                            onReplyScrollRequest = { targetId ->
                                val targetIndex = threadEvents.asReversed().indexOfFirst { it.id == targetId }
                                if (targetIndex >= 0) {
                                    highlightedReplyEventId = targetId
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(targetIndex)
                                        replyHighlightRequest += 1
                                    }
                                }
                            },
                            onSwipeToReply = { replyingToEvent = it },
                            onDepositReceiptClick = { selectedDepositEvent = it },
                            onDepositPillClick = { filter -> selectedDepositFilter = filter },
                            onWeeklyNumberTap = { label, value ->
                                weeklyTapLabel = label
                                weeklyTapValue = value
                            },
                            onPaymentActionClick = { action ->
                                if (paymentState.step == PaymentStep.AWAITING_METHOD) {
                                    val method = when (action) {
                                        "Mobile Money" -> PaymentMethod.MOBILE_MONEY
                                        "Card" -> PaymentMethod.CARD
                                        "Bank transfer" -> PaymentMethod.BANK_TRANSFER
                                        else -> null
                                    }
                                    if (method != null) {
                                        val selected = PaymentFlowManager.selectMethod(method, paymentContext)
                                        appendPaymentPrompts(selected.promptBubbles)
                                        paymentState = selected.copy(promptBubbles = emptyList())
                                        inputValue = paymentContext.amountDue.toString()
                                    }
                                } else if (action.contains("/pay", ignoreCase = true) && paymentState.step == PaymentStep.IDLE) {
                                    val started = PaymentFlowManager.onSlashPayTyped()
                                    appendPaymentPrompts(started.promptBubbles)
                                    paymentState = started.copy(promptBubbles = emptyList())
                                    inputValue = ""
                                }
                            },
                            onRetryLoadMessages = {
                                GroupChatRepository.loadChat(effectiveGroupId, entityName, context)
                            }
                        )

                        if (isLoading && threadEvents.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MicrobondActiveGreen,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    // ── Input bar (Locked when messages restricted or non-member) ──
                    val isUserAdmin = isCreator || userRole?.uppercase() == "ADMIN"
                    val canSendMessages = isUserAdmin || membersCanSendMessages

                    if (!isMember) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = MicrobondCardSurfaceDark,
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0xFF2A3942))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = MicrobondInactiveGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isJoinPending) "Join request pending approval" else "Join this group to send messages",
                                    color = MicrobondInactiveGray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else if (!canSendMessages) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = MicrobondCardSurfaceDark,
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0xFF2A3942))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = MicrobondInactiveGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Only admins can send messages",
                                    color = MicrobondInactiveGray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        ThreadInputSection(
                            inputValue = inputValue,
                            paymentState = paymentState,
                            paymentContext = paymentContext,
                            replyingToEvent = replyingToEvent,
                            isVoiceRecording = voiceRecorderState.isVoiceRecording,
                            isVoiceRecordingLocked = voiceRecorderState.isVoiceRecordingLocked,
                            recordingDurationSeconds = voiceRecorderState.recordingDurationSeconds,
                            threadEvents = threadEvents,
                            coroutineScope = coroutineScope,
                            onInputValueChange = { inputValue = it },
                            onPaymentStateChange = { paymentState = it },
                            onAppendOutgoingEvent = ::appendOutgoingEvent,
                            onAppendPaymentPrompts = ::appendPaymentPrompts,
                            onScrollToBottom = { coroutineScope.launch { listState.animateScrollToItem(0) } },
                            onCancelReply = { replyingToEvent = null },
                            onAttachmentPickerToggle = { showAttachmentPicker = !showAttachmentPicker },
                            onAttachmentPickerDismiss = { showAttachmentPicker = false },
                            onRecordingStateChange = voiceRecorderState.onRecordingStateChange,
                            onRecordingStart = voiceRecorderState.onRecordingStart,
                            onRecordingFinish = voiceRecorderState.onRecordingFinish,
                            onOptionSelected = { /* Handled by overlay */ },
                            onImagesSelected = { _, _ -> /* Handled by overlay */ }
                        )
                    }
                }
            }
        }

        // ── Attachment screens (full-screen overlays) ─────────────────────────
        ThreadAttachmentHandler(
            entityName = entityName,
            groupId = effectiveGroupId,
            selectedAttachmentOption = selectedAttachmentOption,
            attachmentPreviewSummary = attachmentPreviewSummary,
            selectedContactPreview = selectedContactPreview,
            selectedDocument = selectedDocument,
            documentCaption = documentCaption,
            threadEvents = threadEvents,
            coroutineScope = coroutineScope,
            onAppendOutgoingEvent = ::appendOutgoingEvent,
            onScrollToBottom = { coroutineScope.launch { listState.animateScrollToItem(0) } },
            onAttachmentOptionDone = { selectedAttachmentOption = null },
            onPreviewSummaryChange = { attachmentPreviewSummary = it },
            onSelectedContactPreviewChange = { selectedContactPreview = it },
            onSelectedDocumentChange = { selectedDocument = it },
            onDocumentCaptionChange = { documentCaption = it }
        )

        // ── Group settings overlay ─────────────────────────────────────────────
        if (showGroupSettings) {
            val resolvedRole = when {
                isCreator || userRole?.uppercase(java.util.Locale.getDefault()) == "ADMIN" -> com.microbond.app.feature_settings.subgroupsettingsscreens.access.GroupMemberRole.ADMIN
                else -> com.microbond.app.feature_settings.subgroupsettingsscreens.access.GroupMemberRole.MEMBER
            }
            GroupSettingsScreen(
                groupName = displayedGroupName,
                onBack = { showGroupSettings = false },
                memberCount = memberCount,
                groupType = effectiveGroupType,
                viewerRole = resolvedRole,
                groupId = effectiveGroupId,
                initialDescription = groupDescription,
                initialGroupIconUri = displayedGroupIconUri,
                modifier = Modifier.fillMaxSize(),
                onProfileUpdated = { newName, newDesc, newIcon ->
                    if (newName != null) displayedGroupName = newName
                    if (newDesc != null) groupDescription = newDesc
                    if (newIcon != null) displayedGroupIconUri = newIcon
                }
            )
        }

        // ── Attachment picker panel overlay ───────────────────────────────────
        ThreadAttachmentPickerOverlay(
            visible = showAttachmentPicker,
            onDismiss = { showAttachmentPicker = false },
            onExpandedChange = { isAttachmentPickerExpanded = it },
            onHeightChanged = { attachmentPanelHeight = it },
            onImagesSelected = { imageUris, captionText ->
                val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                if (imageUris.isNotEmpty()) {
                    appendOutgoingEvent(
                        ThreadEvent(
                            id = (threadEvents.maxOfOrNull { it.id } ?: 0) + 1,
                            text = captionText,
                            time = time,
                            isCurrentUser = true,
                            status = MessageStatus.SENT,
                            type = ThreadBubbleType.IMAGE,
                            senderName = "You",
                            senderInitials = "Y",
                            senderColor = Color(0xFF00A884),
                            imageUri = imageUris.firstOrNull()?.toString(),
                            imageUris = if (imageUris.size > 1) imageUris.drop(1).map { it.toString() } else null
                        )
                    )
                }
                coroutineScope.launch { listState.animateScrollToItem(0) }
            },
            onDepositStatusSelected = {
                coroutineScope.launch {
                    var fullCount = 0
                    var halfCount = 0
                    var notDepCount = 0
                    if (effectiveGroupId.isNotBlank()) {
                        try {
                            val res = DepositWidgetRepository.fetchDailyWidget(effectiveGroupId)
                            val widgetData = res.getOrNull()
                            if (widgetData != null) {
                                fullCount = widgetData.summary.full.count
                                halfCount = widgetData.summary.partial.count
                                notDepCount = widgetData.summary.notDeposited.count
                                if (fullCount == 0 && halfCount == 0 && notDepCount == 0 && widgetData.totalMembers > 0) {
                                    notDepCount = widgetData.totalMembers
                                }
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("ThreadScreen", "Failed to fetch deposit widget data", e)
                        }
                    }

                    appendOutgoingEvent(
                        ThreadEvent(
                            id = (threadEvents.maxOfOrNull { it.id } ?: 0) + 1,
                            text = "",
                            time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                            isCurrentUser = true,
                            status = MessageStatus.SENT,
                            type = ThreadBubbleType.DEPOSIT_STATUS,
                            senderName = "You",
                            senderInitials = "Y",
                            fullDepositCount = fullCount,
                            halfDepositCount = halfCount,
                            notDepositedCount = notDepCount
                        )
                    )
                    listState.animateScrollToItem(0)
                }
            },
            onOptionSelected = { option ->
                selectedAttachmentOption = option
                attachmentPreviewSummary = null
            }
        )

        // ── Bottom sheets ──────────────────────────────────────────────────────
        ThreadBottomSheets(
            entityName = entityName,
            groupId = effectiveGroupId,
            selectedDepositEvent = selectedDepositEvent,
            selectedUserSheet = selectedUserSheet,
            weeklyTapLabel = weeklyTapLabel,
            weeklyTapValue = weeklyTapValue,
            groupType = effectiveGroupType,
            discoveredGroup = resolvedDiscoveredGroup,
            rotatingGroupState = rotatingPreviewState,
            showRotatingGroupDetailsSheet = showRotatingGroupDetailsSheet,
            selectedDepositFilter = selectedDepositFilter,
            onDepositDismiss = { selectedDepositEvent = null },
            onUserSheetDismiss = { selectedUserSheet = null },
            onWeeklyTapDismiss = { weeklyTapLabel = null },
            onRotatingSheetDismiss = { showRotatingGroupDetailsSheet = false },
            onDepositFilterDismiss = { selectedDepositFilter = null },
            onClaimSlotConfirmed = { selectedGuarantorId, slotNumber, groupName ->
                val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                val textMsg = if (selectedGuarantorId != null) {
                    "🎉 You requested to join $groupName. A vouch request was sent to your mutual contact."
                } else {
                    "🎉 You joined $groupName!"
                }
                GroupChatRepository.appendLocalEvent(
                    ThreadEvent(
                        id = System.currentTimeMillis().toInt(),
                        text = textMsg,
                        time = time,
                        isCurrentUser = true,
                        status = MessageStatus.SENT,
                        type = ThreadBubbleType.SYSTEM,
                        senderName = "System"
                    ),
                    groupName = groupName,
                    context = context
                )
                if (resolvedDiscoveredGroup != null) {
                    if (selectedGuarantorId == null) {
                        UserGroupsRepository.addGroup(
                            resolvedDiscoveredGroup.toJoinedSampleGroup().copy(isMember = true, userRole = "MEMBER"),
                            context
                        )
                    }
                } else {
                    UserGroupsRepository.updateLatestMessage(
                        groupName = groupName,
                        message = textMsg,
                        time = time,
                        context = context
                    )
                }
                coroutineScope.launch { listState.animateScrollToItem(0) }
            }
        )

        // ── Add Group Description Dialog ───────────────────────────────────────
        if (showAddDescriptionDialog) {
            AlertDialog(
                onDismissRequest = { if (!isSavingDescription) showAddDescriptionDialog = false },
                title = {
                    Text(
                        text = "Add Group Description",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Provide a description for members of $entityName:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8696A0)
                        )
                        OutlinedTextField(
                            value = newDescriptionInput,
                            onValueChange = { newDescriptionInput = it },
                            placeholder = { Text("e.g. Weekly savings for emergencies and welfare.", color = Color(0xFF667781)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MicrobondActiveGreen,
                                unfocusedBorderColor = Color(0xFF2E3D46),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = MicrobondActiveGreen
                            ),
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isSavingDescription = true
                                try {
                                    val patchRes = NetworkClient.apiService.updateGroupProfile(
                                        groupId = effectiveGroupId,
                                        payload = UpdateGroupProfileRequest(description = newDescriptionInput.trim())
                                    )
                                    if (patchRes.isSuccessful) {
                                        groupDescription = newDescriptionInput.trim()
                                        showAddDescriptionDialog = false
                                        Toast.makeText(context, "Description updated", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val err = patchRes.errorBody()?.string() ?: patchRes.message()
                                        Toast.makeText(context, "Failed: $err", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSavingDescription = false
                                }
                            }
                        },
                        enabled = !isSavingDescription && newDescriptionInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MicrobondActiveGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(if (isSavingDescription) "Saving..." else "Save")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showAddDescriptionDialog = false },
                        enabled = !isSavingDescription,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF8696A0)
                        )
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = Color(0xFF1F2C34),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // ── Direct Add Member Sheet ──────────────────────────────────────────
        if (showAddMemberSheet) {
            AddGroupMemberScreen(
                groupId = effectiveGroupId,
                groupName = entityName,
                inviteCode = inviteCode,
                onBack = { showAddMemberSheet = false },
                onEditPermissions = {
                    showAddMemberSheet = false
                    showGroupSettings = true
                },
                onMembersAdded = { addedIds ->
                    memberCount += addedIds.size
                    showAddMemberSheet = false
                }
            )
        }
    }
}

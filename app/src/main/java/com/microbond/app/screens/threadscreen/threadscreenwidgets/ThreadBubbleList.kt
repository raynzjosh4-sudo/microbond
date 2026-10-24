package com.microbond.app.screens.threadscreen.threadscreenwidgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.components.threadbubbles.bubbletype.callandcontactbubble.MicroBondContactBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.chartbubbles.MicroBondTextBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.documentbubble.MicroBondDocumentBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.linkandlocationbubbles.MicroBondLocationBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.mediabubbles.MicroBondImageBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.mediabubbles.MicroBondVideoBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.mediabubbles.MicroBondVoiceBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.moneybubble.MicroBondDepositBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.moneybubble.MicroBondDepositStatusBubble
import com.microbond.app.core.network.api.deposits.DepositStatusFilter
import com.microbond.app.core.components.threadbubbles.bubbletype.moneybubble.MicroBondPaymentUpdateBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.moneybubble.MicroBondSavingsReceivedBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.pollandquestionbubbles.MicroBondPollBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.pollandquestionbubbles.MicroBondSecurityPollBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.rulesbubbles.MicroBondSecurityRuleUpdateBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.rulesbubbles.MicroBondSecurityRulesBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondGroupHealthBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondStatAreaBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondStatBoxPlotBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondStatCandlestickBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondStatGroupProgressBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondStatGroupSnapshotBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondStatHeatmapBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondStatHistogramBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondStatScatterBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondStatWaterfallBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.MicroBondWeeklyReportBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble.StatCustomChartBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.systembubbles.MicroBondActionPromptBubble
import com.microbond.app.core.components.threadbubbles.bubbletype.systembubbles.MicroBondSystemBubble
import com.microbond.app.core.components.threadbubbles.threads.ThreadBubbleWrapper
import com.microbond.app.core.components.threadbubbles.threads.ThreadSelectionState
import com.microbond.app.core.components.threadbubbles.widgets.ScrollToBottomFab
import com.microbond.app.core.components.threadbubbles.widgets.statusicon.MicroBondDateDivider
import com.microbond.app.core.data_classes_and_enums.GroupHealthData
import com.microbond.app.core.data_classes_and_enums.StatAreaData
import com.microbond.app.core.data_classes_and_enums.StatBoxPlotData
import com.microbond.app.core.data_classes_and_enums.StatCandlestickData
import com.microbond.app.core.data_classes_and_enums.StatCustomChartData
import com.microbond.app.core.data_classes_and_enums.StatGroupProgressData
import com.microbond.app.core.data_classes_and_enums.StatGroupSnapshotData
import com.microbond.app.core.data_classes_and_enums.StatHeatmapData
import com.microbond.app.core.data_classes_and_enums.StatHistogramData
import com.microbond.app.core.data_classes_and_enums.StatScatterData
import com.microbond.app.core.data_classes_and_enums.StatWaterfallData
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.payment.PaymentFlowState
import com.microbond.app.core.payment.PaymentMethod
import com.microbond.app.core.payment.PaymentStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import com.microbond.app.core.components.groupintrocard.GroupDatePill
import com.microbond.app.core.components.groupintrocard.GroupEncryptionNoticePill
import com.microbond.app.core.components.groupintrocard.rotating.RotatingGroupIntroCard
import com.microbond.app.core.components.groupintrocard.rotating.RotatingGroupPreviewUiState
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import androidx.compose.foundation.lazy.itemsIndexed
import com.microbond.app.core.components.groupintrocard.GroupIntroCard
import com.microbond.app.feature_groups.data.mapper.ChatJsonHelpers
import safePainterResource

private fun getEventTimestamp(event: ThreadEvent): Long {
    val raw = event.fullDate?.toLongOrNull()
    return if (raw != null && raw > 0L) {
        ChatJsonHelpers.toMillis(raw)
    } else {
        System.currentTimeMillis()
    }
}

@Composable
fun ThreadBubbleList(
    threadEvents: List<ThreadEvent>,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    selectionState: ThreadSelectionState,
    highlightedReplyEventId: Int?,
    replyHighlightRequest: Int,
    paymentState: PaymentFlowState,
    showScrollToBottom: Boolean,
    onUserTap: (ThreadEvent) -> Unit,
    onReplyScrollRequest: (targetId: Int) -> Unit,
    onSwipeToReply: (ThreadEvent) -> Unit,
    onDepositReceiptClick: (ThreadEvent) -> Unit,
    onWeeklyNumberTap: (label: String, value: Int) -> Unit,
    onPaymentActionClick: (action: String) -> Unit,
    groupType: SavingsGroupType? = null,
    groupName: String = "",
    discoveredGroup: DiscoverableContactGroup? = null,
    rotatingGroupState: RotatingGroupPreviewUiState? = null,
    onOpenRotatingSheet: () -> Unit = {},
    isMember: Boolean = true,
    userRole: String? = null,
    groupDescription: String? = null,
    groupIconUri: String? = null,
    membersCanInviteViaLink: Boolean = false,
    membersCanEditSettings: Boolean = false,
    isCreator: Boolean = false,
    memberCount: Int = 1,
    isJoinLoading: Boolean = false,
    isJoinPending: Boolean = false,
    onJoinClick: () -> Unit = {},
    onAddMembersClick: () -> Unit = {},
    onInviteLinkClick: () -> Unit = {},
    onAddDescriptionClick: () -> Unit = {},
    onDepositPillClick: (DepositStatusFilter) -> Unit = {},
    onRetryLoadMessages: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val reversedEvents = threadEvents.asReversed()
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            reverseLayout = true
        ) {
            itemsIndexed(reversedEvents, key = { _, event -> event.id }) { index, event ->
                val senderAvatarPainter = safePainterResource(event.senderAvatarRes)
                val imagePainter = safePainterResource(event.imageRes)

                val meta = event.toBubbleMeta(
                    avatarPainter = senderAvatarPainter,
                    onUserTap = { onUserTap(event) },
                    onReplyClick = { targetId -> onReplyScrollRequest(targetId) }
                )
                val isSelected = selectionState.isSelected(event.id)

                ThreadBubbleWrapper(
                    isSelected = isSelected,
                    onLongClick = { selectionState.toggleSelection(event.id) },
                    onClick = {
                        if (selectionState.isSelectionMode) {
                            selectionState.toggleSelection(event.id)
                        }
                    },
                    swipeToReplyEnabled = event.type != ThreadBubbleType.SYSTEM && event.type != ThreadBubbleType.ACTION_PROMPT,
                    highlightRequest = if (event.id == highlightedReplyEventId) replyHighlightRequest else 0,
                    onSwipeToReply = { onSwipeToReply(event) }
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        when (event.type) {
                            ThreadBubbleType.TEXT, ThreadBubbleType.REPLY -> MicroBondTextBubble(
                                text = event.text, meta = meta
                            )
                            ThreadBubbleType.VOICE -> MicroBondVoiceBubble(
                                duration = event.text,
                                meta = meta,
                                avatar = senderAvatarPainter,
                                avatarInitials = event.senderInitials,
                                audioUri = event.voiceUri
                            )
                            ThreadBubbleType.IMAGE -> MicroBondImageBubble(
                                painter = imagePainter,
                                caption = event.text.takeIf { it.isNotBlank() },
                                meta = meta,
                                imageUri = event.imageUri,
                                imageUris = event.imageUris,
                                imageRes = event.imageRes,
                                imageResList = event.imageResList,
                                aspectRatio = event.imageAspectRatio
                            )
                            ThreadBubbleType.DEPOSIT -> MicroBondDepositBubble(
                                amount = event.amount.orEmpty(),
                                paymentMethod = event.paymentMethod.orEmpty(),
                                referenceId = event.referenceId.orEmpty(),
                                cycleNote = event.cycleNote.orEmpty(),
                                isVerified = event.isVerified,
                                meta = meta,
                                fullDate = event.fullDate,
                                requiredAmount = event.requiredAmount,
                                depositedAmount = event.depositedAmount,
                                onViewReceiptClick = { onDepositReceiptClick(event) }
                            )
                            ThreadBubbleType.DEPOSIT_STATUS -> MicroBondDepositStatusBubble(
                                meta = meta,
                                fullDepositCount = event.fullDepositCount,
                                halfDepositCount = event.halfDepositCount,
                                notDepositedCount = event.notDepositedCount,
                                onPillClick = onDepositPillClick
                            )
                            ThreadBubbleType.WEEKLY_REPORT -> event.weeklyReport?.let { report ->
                                MicroBondWeeklyReportBubble(
                                    meta = meta,
                                    report = report,
                                    onNumberTap = { label, value -> onWeeklyNumberTap(label, value) }
                                )
                            }
                            ThreadBubbleType.SECURITY_RULES -> event.securityRules?.let { rules ->
                                MicroBondSecurityRulesBubble(
                                    meta = meta,
                                    data = rules,
                                    onViewAllClick = { /* TODO */ },
                                    onRuleClick = { _ -> /* TODO */ }
                                )
                            }
                            ThreadBubbleType.SECURITY_POLL -> event.securityPoll?.let { poll ->
                                MicroBondSecurityPollBubble(
                                    meta = meta,
                                    data = poll,
                                    onOptionClick = { _ -> /* TODO */ }
                                )
                            }
                            ThreadBubbleType.SAVINGS_RECEIVED -> event.savingsReceived?.let { received ->
                                MicroBondSavingsReceivedBubble(meta = meta, data = received)
                            }
                            ThreadBubbleType.SECURITY_RULE_UPDATE -> event.securityRuleUpdate?.let { update ->
                                MicroBondSecurityRuleUpdateBubble(meta = meta, data = update)
                            }
                            ThreadBubbleType.LOCATION -> event.locationPlace?.let { place ->
                                MicroBondLocationBubble(place = place, meta = meta)
                            }
                            ThreadBubbleType.STAT_GROUP_PROGRESS -> {
                                MicroBondStatGroupProgressBubble(meta = meta, data = event.statGroupProgress ?: StatGroupProgressData())
                            }
                            ThreadBubbleType.STAT_GROUP_SNAPSHOT -> {
                                MicroBondStatGroupSnapshotBubble(meta = meta, data = event.statGroupSnapshot ?: StatGroupSnapshotData())
                            }
                            ThreadBubbleType.GROUP_HEALTH -> {
                                MicroBondGroupHealthBubble(meta = meta, data = event.groupHealth ?: GroupHealthData())
                            }
                            ThreadBubbleType.STAT_HISTOGRAM -> {
                                MicroBondStatHistogramBubble(meta = meta, data = event.statHistogram ?: StatHistogramData())
                            }
                            ThreadBubbleType.STAT_CUSTOM_CHART -> {
                                val chart = event.statCustomChart ?: StatCustomChartData(
                                    chartId = "custom_chart",
                                    title = event.text.ifBlank { "Group Analytics" },
                                    subtitle = "Custom chart",
                                    xLabel = "Period",
                                    yLabel = "Amount",
                                    xValues = listOf(1f, 2f, 3f, 4f, 5f),
                                    yValues = listOf(10f, 25f, 15f, 30f, 45f),
                                    primaryColorValue = 0xFF00A884,
                                    secondaryColorValue = 0xFF3B82F6
                                )
                                StatCustomChartBubble(meta = meta, data = chart)
                            }
                            ThreadBubbleType.STAT_CANDLESTICK -> {
                                MicroBondStatCandlestickBubble(meta = meta, data = event.statCandlestick ?: StatCandlestickData())
                            }
                            ThreadBubbleType.STAT_AREA -> {
                                MicroBondStatAreaBubble(meta = meta, data = event.statArea ?: StatAreaData())
                            }
                            ThreadBubbleType.STAT_WATERFALL -> {
                                MicroBondStatWaterfallBubble(meta = meta, data = event.statWaterfall ?: StatWaterfallData())
                            }
                            ThreadBubbleType.STAT_SCATTER -> {
                                MicroBondStatScatterBubble(meta = meta, data = event.statScatter ?: StatScatterData())
                            }
                            ThreadBubbleType.STAT_BOXPLOT -> {
                                MicroBondStatBoxPlotBubble(meta = meta, data = event.statBoxPlot ?: StatBoxPlotData())
                            }
                            ThreadBubbleType.STAT_HEATMAP -> {
                                MicroBondStatHeatmapBubble(meta = meta, data = event.statHeatmap ?: StatHeatmapData())
                            }
                            ThreadBubbleType.PAYMENT_UPDATE -> event.paymentUpdate?.let { update ->
                                MicroBondPaymentUpdateBubble(
                                    meta = meta,
                                    depositedAmountText = update.depositedAmountText,
                                    requiredAmountText = update.requiredAmountText,
                                    reminderText = update.reminderText,
                                    dateText = update.dateText,
                                    title = update.title,
                                    paymentPhoneText = update.paymentPhoneText,
                                    paymentMethodText = update.paymentMethodText
                                )
                            }
                            ThreadBubbleType.DOCUMENT -> event.documentData?.let { doc ->
                                MicroBondDocumentBubble(
                                    name = doc.fileName,
                                    size = doc.fileSize,
                                    meta = meta,
                                    uri = doc.uri,
                                    fileType = doc.fileType
                                )
                            }
                            ThreadBubbleType.CONTACT -> event.contactData.takeIf { it.isNotEmpty() }?.let { contacts ->
                                MicroBondContactBubble(contacts = contacts, meta = meta)
                            }
                            ThreadBubbleType.SYSTEM -> {
                                val msg = (event.systemMessage ?: event.text).takeIf { it.isNotBlank() }
                                if (msg != null) {
                                    val isRetryable = event.id < 0
                                    MicroBondSystemBubble(
                                        text = msg,
                                        onClick = if (isRetryable && onRetryLoadMessages != null) {
                                            { onRetryLoadMessages() }
                                        } else null
                                    )
                                }
                            }
                            ThreadBubbleType.POLL -> event.pollData?.let { poll ->
                                MicroBondPollBubble(
                                    question = poll.question,
                                    options = poll.options.map { it.label },
                                    meta = meta,
                                    allowMultipleAnswers = poll.isMultipleChoice,
                                    questionSubtitle = poll.questionSubtitle,
                                    footerText = poll.footerText
                                )
                            }
                            ThreadBubbleType.VIDEO -> event.videoData?.let { video ->
                                MicroBondVideoBubble(
                                    painter = null,
                                    duration = video.duration,
                                    meta = meta
                                )
                            }
                            ThreadBubbleType.ACTION_PROMPT -> {
                                val msg = event.actionPromptData?.message ?: event.text
                                if (msg.isNotBlank()) {
                                    MicroBondActionPromptBubble(
                                        message = msg,
                                        actionText = event.actionPromptData?.actionText,
                                        onActionClick = { action -> onPaymentActionClick(action) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Dynamic date divider between consecutive messages on different calendar days
                val nextIndex = index + 1
                val currentDayKey = ChatJsonHelpers.getDayKey(getEventTimestamp(event))
                val previousEvent = reversedEvents.getOrNull(nextIndex)
                if (previousEvent != null) {
                    val previousDayKey = ChatJsonHelpers.getDayKey(getEventTimestamp(previousEvent))
                    if (currentDayKey != previousDayKey) {
                        MicroBondDateDivider(ChatJsonHelpers.formatDateDivider(getEventTimestamp(event)))
                    }
                }
            }

            item(key = "group_intro_card_header") {
                val oldestEvent = threadEvents.firstOrNull()
                val topDateText = if (oldestEvent != null) {
                    ChatJsonHelpers.formatDateDivider(getEventTimestamp(oldestEvent))
                } else {
                    "Today"
                }

                val titleText = if (isCreator) "You created this group" else groupName
                val subtitleText = if (memberCount <= 1) "Group · Only you" else "Group · $memberCount members"

                val isAdmin = userRole?.uppercase() == "ADMIN" || isCreator
                val canAddMembers = isMember && isAdmin
                val canInvite = isMember && (isAdmin || membersCanInviteViaLink)
                val canEditDesc = isMember && (isAdmin || membersCanEditSettings)
                val canEditSettings = isMember && (isAdmin || membersCanEditSettings)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GroupDatePill(dateText = topDateText)
                    GroupEncryptionNoticePill()

                    GroupIntroCard(
                        name = titleText,
                        memberCountText = subtitleText,
                        description = groupDescription,
                        iconUri = groupIconUri,
                        isMember = isMember,
                        canAddMembers = canAddMembers,
                        canInviteViaLink = canInvite,
                        canEditDescription = canEditDesc,
                        canEditSettings = canEditSettings,
                        isJoinLoading = isJoinLoading,
                        joinButtonText = if (isJoinPending) "Requested" else "Join Group",
                        isJoinEnabled = !isJoinPending && !isJoinLoading,
                        onJoinClick = onJoinClick,
                        onAddMembersClick = onAddMembersClick,
                        onInviteLinkClick = onInviteLinkClick,
                        onAddDescriptionClick = onAddDescriptionClick,
                        groupSpecificContent = {
                            if (groupType == SavingsGroupType.MERRY_GO_ROUND) {
                                val state = rotatingGroupState ?: remember(groupName, discoveredGroup) {
                                    if (discoveredGroup != null) {
                                        RotatingGroupPreviewUiState.fromDiscoverableGroup(discoveredGroup)
                                    } else {
                                        RotatingGroupPreviewUiState.fromGroupNameOnly(groupName)
                                    }
                                }
                                RotatingGroupIntroCard(
                                    state = state,
                                    onOpenDetailsSheet = onOpenRotatingSheet
                                )
                            }
                        }
                    )
                }
            }
        }

        ScrollToBottomFab(
            visible = showScrollToBottom,
            onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

package com.microbond.app.screens.threadscreen.threadscreenwidgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.microbond.app.core.components.threadbubbles.itemscreens.AdditionalAttachmentScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.AttachmentPreviewScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.ContactAttachmentScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.DocumentAttachmentScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.LocationAttachmentScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.PollAttachmentScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.RulesAttachmentScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.SaveReportAttachmentScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.StatisticsScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.ContactSendItem
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.sendscreens.SendContactsScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.documentscreens.DocumentPreviewScreen
import com.microbond.app.core.data_classes_and_enums.AttachmentOption
import com.microbond.app.core.data_classes_and_enums.ContactBubbleData
import com.microbond.app.core.data_classes_and_enums.DocumentData
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.PollOption
import com.microbond.app.core.data_classes_and_enums.SecurityRulesData
import com.microbond.app.core.data_classes_and_enums.SecurityRuleUpdateData
import com.microbond.app.core.data_classes_and_enums.StandardPollData
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.data_classes_and_enums.WeeklyReportData
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ThreadAttachmentHandler(
    entityName: String,
    groupId: String = "",
    selectedAttachmentOption: AttachmentOption?,
    attachmentPreviewSummary: String?,
    selectedContactPreview: List<ContactSendItem>,
    selectedDocument: DocumentData?,
    documentCaption: String,
    threadEvents: List<ThreadEvent>,
    coroutineScope: CoroutineScope,
    onAppendOutgoingEvent: (ThreadEvent) -> Unit,
    onScrollToBottom: () -> Unit,
    onAttachmentOptionDone: () -> Unit,
    onPreviewSummaryChange: (String?) -> Unit,
    onSelectedContactPreviewChange: (List<ContactSendItem>) -> Unit,
    onSelectedDocumentChange: (DocumentData?) -> Unit,
    onDocumentCaptionChange: (String) -> Unit,
) {
    val option = selectedAttachmentOption ?: return
    val previewSummary = attachmentPreviewSummary
    fun nextId() = (threadEvents.maxOfOrNull { it.id } ?: 0) + 1
    fun now() = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

    if (previewSummary == null) {
        val onBack = { onAttachmentOptionDone() }
        val onPreview: (String) -> Unit = { onPreviewSummaryChange(it) }

        when (option) {
            AttachmentOption.DEPOSIT_STATUS -> Unit

            AttachmentOption.SAVE_REPORT -> SaveReportAttachmentScreen(
                onBack = onBack,
                onCreateRequest = { selectedSections, period, selectedDate ->
                    val reportSections = buildSet {
                        if (selectedSections.contains("Week summary")) add("WEEK TOTALS")
                        if (selectedSections.contains("Daily records") ||
                            selectedSections.contains("Contribution history")) add("DAILY SAVING")
                        if (selectedSections.contains("Member balances")) add("BALANCE COUNTS")
                        if (selectedSections.contains("Members who saved") ||
                            selectedSections.contains("Members who missed") ||
                            selectedSections.contains("All members")) add("MEMBERS")
                        if (selectedSections.contains("Group activity")) add("ACTIVITY")
                        if (selectedSections.contains("Payout rotation")) add("PAYOUT ROTATION")
                    }
                    val sampleReport = WeeklyReportData()
                    val dateRange = when (period) {
                        "Day" -> selectedDate
                        "Week" -> "Week ending $selectedDate"
                        "Month" -> selectedDate
                        "5 Years" -> "5 years ending $selectedDate"
                        else -> "Custom period ending $selectedDate"
                    }
                    onAppendOutgoingEvent(
                        ThreadEvent(
                            id = nextId(), text = "", time = now(),
                            isCurrentUser = true, status = MessageStatus.SENT,
                            type = ThreadBubbleType.WEEKLY_REPORT,
                            senderName = "You", senderInitials = "Y",
                            senderColor = Color(0xFF00A884),
                            weeklyReport = sampleReport.copy(
                                weekLabel = "${period.uppercase(Locale.getDefault())} SAVING REPORT",
                                dateRange = dateRange,
                                asOf = "$selectedDate · ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())}",
                                visibleSections = reportSections,
                                filterSections = true
                            )
                        )
                    )
                    onAttachmentOptionDone()
                    onPreviewSummaryChange(null)
                    onScrollToBottom()
                }
            )

            AttachmentOption.LOCATION -> LocationAttachmentScreen(onBack, onPreview)

            AttachmentOption.CONTACT -> ContactAttachmentScreen(
                onBack = onBack,
                initiallySelected = selectedContactPreview,
                onPreview = { contacts ->
                    onSelectedContactPreviewChange(contacts)
                    onPreviewSummaryChange("contacts-selected")
                }
            )

            AttachmentOption.DOCUMENT -> DocumentAttachmentScreen(
                onBack = onBack,
                onPreview = { document ->
                    onSelectedDocumentChange(document)
                    onDocumentCaptionChange("")
                    onPreviewSummaryChange("document-selected")
                }
            )

            AttachmentOption.POLL -> PollAttachmentScreen(
                onBack = onBack,
                onSend = { question, options, allowMultipleAnswers ->
                    onAppendOutgoingEvent(
                        ThreadEvent(
                            id = nextId(), text = "", time = now(),
                            isCurrentUser = true, status = MessageStatus.SENT,
                            type = ThreadBubbleType.POLL,
                            senderName = "You", senderInitials = "Y",
                            senderColor = Color(0xFF00A884),
                            pollData = StandardPollData(
                                question = question,
                                options = options.mapIndexed { index, label ->
                                    PollOption(id = "poll-option-$index", label = label, percentage = 0, isSelected = false)
                                },
                                isMultipleChoice = allowMultipleAnswers,
                                questionSubtitle = if (allowMultipleAnswers) "Select all that apply" else "Choose one option"
                            )
                        )
                    )
                    onAttachmentOptionDone()
                    onPreviewSummaryChange(null)
                    onScrollToBottom()
                }
            )

            AttachmentOption.RULES -> RulesAttachmentScreen(
                onBack = onBack,
                groupName = entityName,
                onSend = { selectedRules, singleRuleText, singleRuleStatus ->
                    val rulesToPost = if (singleRuleText.isNotBlank()) listOf(singleRuleText) else selectedRules
                    if (rulesToPost.isNotEmpty()) {
                        onAppendOutgoingEvent(
                            ThreadEvent(
                                id = nextId(), text = "", time = now(),
                                isCurrentUser = true, status = MessageStatus.SENT,
                                type = if (singleRuleText.isNotBlank()) ThreadBubbleType.SECURITY_RULE_UPDATE
                                       else ThreadBubbleType.SECURITY_RULES,
                                senderName = "You", senderInitials = "Y",
                                senderColor = Color(0xFF00A884),
                                securityRules = if (singleRuleText.isBlank()) SecurityRulesData(
                                    title = "Group rules", rules = rulesToPost,
                                    pinnedLabel = "Pinned for everyone", viewAllLabel = "View all rules"
                                ) else null,
                                securityRuleUpdate = if (singleRuleText.isNotBlank()) SecurityRuleUpdateData(
                                    ruleText = singleRuleText,
                                    title = when (singleRuleStatus) {
                                        "Up to date" -> "Active group rule"
                                        "Changed" -> "Group rule updated"
                                        "New" -> "New group rule"
                                        else -> "Group rule"
                                    },
                                    statusLabel = when (singleRuleStatus) {
                                        "Up to date" -> "Active rule"
                                        "Changed" -> "Updated rule"
                                        "New" -> "New rule"
                                        else -> "Rule status unavailable"
                                    },
                                    approvalNote = "Requested from group",
                                    approvalDate = "Today"
                                ) else null
                            )
                        )
                        onScrollToBottom()
                    }
                    onAttachmentOptionDone()
                    onPreviewSummaryChange(null)
                }
            )

            AttachmentOption.STATISTICS -> StatisticsScreen(
                groupId = groupId,
                onBack = onBack,
                onPreview = onPreview,
                onSendChart = { chart ->
                    onAppendOutgoingEvent(
                        ThreadEvent(
                            id = nextId(), text = chart.title, time = now(),
                            isCurrentUser = true, status = MessageStatus.SENT,
                            type = ThreadBubbleType.STAT_CUSTOM_CHART,
                            senderName = "You", senderInitials = "Y",
                            senderColor = Color(chart.primaryColorValue.toInt()),
                            statCustomChart = chart
                        )
                    )
                    onAttachmentOptionDone()
                    onPreviewSummaryChange(null)
                    onScrollToBottom()
                }
            )

            AttachmentOption.GALLERY,
            AttachmentOption.CAMERA,
            AttachmentOption.EVENT,
            AttachmentOption.AI_IMAGES -> AdditionalAttachmentScreen(option, onBack, onPreview)
        }
    } else {
        when {
            option == AttachmentOption.CONTACT -> SendContactsScreen(
                contacts = selectedContactPreview,
                onBack = { onPreviewSummaryChange(null) },
                onSend = {
                    if (selectedContactPreview.isNotEmpty()) {
                        val appMembers = threadEvents.filter {
                            !it.isCurrentUser && !it.senderName.equals("System", ignoreCase = true)
                        }
                        val appMemberNames = appMembers.mapNotNull { it.senderName }
                            .map { it.replace("~", "").trim().lowercase(Locale.getDefault()) }.toSet()
                        val appMemberPhones = appMembers.mapNotNull { it.senderPhone?.filter(Char::isDigit) }
                            .filter(String::isNotBlank).toSet()
                        val contactsToShare = selectedContactPreview.filterNot { contact ->
                            contact.name.replace("~", "").trim().lowercase(Locale.getDefault()) in appMemberNames ||
                            contact.phone.filter(Char::isDigit) in appMemberPhones
                        }
                        if (contactsToShare.isNotEmpty()) {
                            onAppendOutgoingEvent(
                                ThreadEvent(
                                    id = nextId(), text = "", time = now(),
                                    isCurrentUser = true, status = MessageStatus.SENT,
                                    type = ThreadBubbleType.CONTACT,
                                    senderName = "You", senderInitials = "Y",
                                    contactData = contactsToShare.map { contact ->
                                        ContactBubbleData(id = contact.id, name = contact.name,
                                            phone = contact.phone, photoUri = contact.photoUri)
                                    }
                                )
                            )
                            onScrollToBottom()
                        }
                    }
                    onAttachmentOptionDone()
                    onPreviewSummaryChange(null)
                    onSelectedContactPreviewChange(emptyList())
                }
            )

            option == AttachmentOption.DOCUMENT && selectedDocument != null -> DocumentPreviewScreen(
                document = selectedDocument,
                onBack = { onPreviewSummaryChange(null) },
                caption = documentCaption,
                onCaptionChange = onDocumentCaptionChange,
                destinationLabel = "$entityName (You)",
                onSend = {
                    onAppendOutgoingEvent(
                        ThreadEvent(
                            id = nextId(), text = documentCaption, time = now(),
                            isCurrentUser = true, status = MessageStatus.SENT,
                            type = ThreadBubbleType.DOCUMENT,
                            senderName = "You", senderInitials = "Y",
                            documentData = selectedDocument
                        )
                    )
                    onScrollToBottom()
                    onAttachmentOptionDone()
                    onPreviewSummaryChange(null)
                    onSelectedDocumentChange(null)
                    onDocumentCaptionChange("")
                }
            )

            else -> AttachmentPreviewScreen(
                option = option,
                summary = previewSummary,
                onBack = { onPreviewSummaryChange(null) },
                onClose = {
                    onAttachmentOptionDone()
                    onPreviewSummaryChange(null)
                }
            )
        }
    }
}

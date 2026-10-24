package com.microbond.app.feature_groups.data.mapper

import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatMessageDto
import com.microbond.app.core.ui.theme.AvatarColorHelper
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import java.util.Locale

/**
 * Clean facade coordinating chat message deserialization into Compose-ready [ThreadEvent]s.
 * Complex parsing is delegated to modular sub-mappers:
 * - [ChatJsonHelpers]: Safe JSON parsing & time formatting
 * - [ChatBubbleTypeResolver]: 30+ visual bubble classification
 * - [ChatChartMessageMapper]: Financial charts & diagrams
 * - [ChatSpecialMessageMapper]: Polls, documents, security rules, and reports
 */
object GroupChatMapper {

    fun toThreadEvent(dto: GroupChatMessageDto, currentUserId: String): ThreadEvent {
        val isSystem = dto.senderId.equals("SYSTEM", ignoreCase = true) ||
                dto.messageType.equals("SYSTEM", ignoreCase = true) ||
                dto.messageType.equals("SYSTEM_EVENT", ignoreCase = true)

        val isOutgoing = if (isSystem) false else (dto.isOutgoing ?: (currentUserId.isNotBlank() && dto.senderId.equals(currentUserId, ignoreCase = true)))
        val textLower = (dto.messageText ?: "").lowercase(Locale.getDefault())

        val attachments = ChatJsonHelpers.jsonElementToMap(dto.attachments)
        val attachmentList = ChatJsonHelpers.jsonElementToList(dto.attachments)

        val resolvedMediaUrl = dto.mediaUrl?.takeIf { it.isNotBlank() }
            ?: (attachmentList.firstOrNull()?.get("url") as? String)?.takeIf { it.isNotBlank() }
            ?: (attachments["url"] as? String)?.takeIf { it.isNotBlank() }

        val resolvedMediaType = dto.mediaType?.takeIf { it.isNotBlank() }
            ?: (attachmentList.firstOrNull()?.get("media_type") as? String)?.takeIf { it.isNotBlank() }
            ?: (attachments["media_type"] as? String)?.takeIf { it.isNotBlank() }

        val resolvedImageUris = if (attachmentList.size > 1) {
            attachmentList.mapNotNull { it["url"] as? String }.filter { it.isNotBlank() }
        } else null

        val resolvedAspectRatio = dto.aspectRatio
            ?: (attachmentList.firstOrNull()?.get("aspect_ratio") as? Number)?.toFloat()
            ?: (attachments["aspect_ratio"] as? Number)?.toFloat()

        // 1. Resolve visual bubble type
        val hasReplyTo = !dto.replyToMessageId.isNullOrBlank() || dto.replyTo != null
        val bubbleType = ChatBubbleTypeResolver.resolve(
            isSystem = isSystem,
            messageType = dto.messageType,
            mediaType = resolvedMediaType,
            hasReplyTo = hasReplyTo,
            textLower = textLower
        )

        // 2. Format timestamp
        val createdAtMillis = dto.createdAt?.toLong() ?: 0L
        val timeFormatted = ChatJsonHelpers.formatTime(createdAtMillis)

        // 3. Sender presentation
        val resolvedSenderName = when {
            isOutgoing -> "You"
            isSystem -> "System"
            !dto.senderName.isNullOrBlank() -> dto.senderName
            else -> (attachments["sender_name"] as? String)
                ?: (attachments["senderName"] as? String)
                ?: "Member"
        }

        val resolvedSenderColor = if (isOutgoing) {
            MicrobondActiveGreen
        } else {
            AvatarColorHelper.getColor(dto.senderId)
        }

        // 4. Message status
        val msgStatus = when (dto.status?.uppercase(Locale.getDefault())) {
            "READ" -> MessageStatus.READ
            "DELIVERED" -> MessageStatus.DELIVERED
            "FAILED" -> MessageStatus.FAILED
            "PENDING" -> MessageStatus.PENDING
            else -> MessageStatus.SENT
        }

        // 5. Special bubble data extractions
        val amount = (attachments["amount"] as? String) ?: (attachments["deposited_amount"] as? String)
        val paymentMethod = (attachments["method"] as? String) ?: (attachments["payment_method"] as? String)
        val referenceId = (attachments["reference_id"] as? String) ?: (attachments["reference"] as? String)
        val cycleNote = attachments["cycle_note"] as? String
        val isVerified = (attachments["is_verified"] as? Boolean) ?: true

        // Location & Documents
        val locationPlace = (attachments["place"] as? String) ?: (attachments["location"] as? String)
        val locationSubtitle = (attachments["address"] as? String) ?: (attachments["subtitle"] as? String)
        val documentData = if (bubbleType == ThreadBubbleType.DOCUMENT) ChatSpecialMessageMapper.parseDocument(dto, attachments, resolvedMediaUrl) else null
        val videoData = if (bubbleType == ThreadBubbleType.VIDEO) ChatSpecialMessageMapper.parseVideo(dto, attachments) else null
        val pollData = if (bubbleType == ThreadBubbleType.POLL) ChatSpecialMessageMapper.parsePoll(dto, attachments) else null
        val securityRules = if (bubbleType == ThreadBubbleType.SECURITY_RULES) ChatSpecialMessageMapper.parseSecurityRules(dto, attachments) else null
        val securityRuleUpdate = if (bubbleType == ThreadBubbleType.SECURITY_RULE_UPDATE) ChatSpecialMessageMapper.parseSecurityRuleUpdate(dto, attachments) else null
        val securityPoll = if (bubbleType == ThreadBubbleType.SECURITY_POLL) ChatSpecialMessageMapper.parseSecurityPoll(dto, attachments) else null
        val savingsReceived = if (bubbleType == ThreadBubbleType.SAVINGS_RECEIVED) ChatSpecialMessageMapper.parseSavingsReceived(dto, resolvedSenderName) else null
        val actionPromptData = if (bubbleType == ThreadBubbleType.ACTION_PROMPT) ChatSpecialMessageMapper.parseActionPrompt(dto, attachments) else null
        val paymentUpdate = if (bubbleType == ThreadBubbleType.PAYMENT_UPDATE) ChatSpecialMessageMapper.parsePaymentUpdate(amount, attachments, timeFormatted, paymentMethod) else null
        val weeklyReport = if (bubbleType == ThreadBubbleType.WEEKLY_REPORT) ChatSpecialMessageMapper.parseWeeklyReport(dto, attachments) else null

        // 6. Chart extractions
        val chartPayload = (attachments["chart"] as? Map<*, *>) ?: attachments
        val statHistogram = if (bubbleType == ThreadBubbleType.STAT_HISTOGRAM) ChatChartMessageMapper.parseHistogram(chartPayload) else null
        val statCandlestick = if (bubbleType == ThreadBubbleType.STAT_CANDLESTICK) ChatChartMessageMapper.parseCandlestick(chartPayload) else null
        val statArea = if (bubbleType == ThreadBubbleType.STAT_AREA) ChatChartMessageMapper.parseArea(chartPayload) else null
        val statWaterfall = if (bubbleType == ThreadBubbleType.STAT_WATERFALL) ChatChartMessageMapper.parseWaterfall(chartPayload) else null
        val statScatter = if (bubbleType == ThreadBubbleType.STAT_SCATTER) ChatChartMessageMapper.parseScatter(chartPayload) else null
        val statBoxPlot = if (bubbleType == ThreadBubbleType.STAT_BOXPLOT) ChatChartMessageMapper.parseBoxPlot(chartPayload) else null
        val statHeatmap = if (bubbleType == ThreadBubbleType.STAT_HEATMAP) ChatChartMessageMapper.parseHeatmap(chartPayload) else null
        val statGroupSnapshot = if (bubbleType == ThreadBubbleType.STAT_GROUP_SNAPSHOT) ChatChartMessageMapper.parseGroupSnapshot(chartPayload) else null
        val groupHealth = if (bubbleType == ThreadBubbleType.GROUP_HEALTH) ChatChartMessageMapper.parseGroupHealth(chartPayload) else null
        val statGroupProgress = if (bubbleType == ThreadBubbleType.STAT_GROUP_PROGRESS) ChatChartMessageMapper.parseGroupProgress(chartPayload) else null
        val statCustomChart = if (bubbleType == ThreadBubbleType.STAT_CUSTOM_CHART) ChatChartMessageMapper.parseCustomChart(chartPayload, dto.messageText ?: "") else null

        // 7. Stable integer ID
        val numericId = if (dto.id.isNotBlank()) {
            dto.id.hashCode()
        } else if (!dto.clientMessageId.isNullOrBlank()) {
            dto.clientMessageId.hashCode()
        } else {
            (dto.createdAt?.toLong() ?: System.currentTimeMillis()).toInt()
        }

        val resolvedPhone = if (isSystem) null else (
            dto.senderPhone ?:
            (attachments["sender_phone"] as? String) ?:
            (attachments["phone"] as? String) ?:
            (attachments["senderPhone"] as? String)
        )

        val resolvedAvatarUrl = if (isSystem) null else (
            dto.senderAvatarUrl ?:
            (attachments["sender_avatar_url"] as? String) ?:
            (attachments["avatar_url"] as? String) ?:
            (attachments["senderAvatarUrl"] as? String)
        )

        val fullDepositCount = (attachments["full_deposit_count"] as? Number)?.toInt()
            ?: (attachments["fullDepositCount"] as? Number)?.toInt()
            ?: 0
        val halfDepositCount = (attachments["half_deposit_count"] as? Number)?.toInt()
            ?: (attachments["halfDepositCount"] as? Number)?.toInt()
            ?: (attachments["partial_deposit_count"] as? Number)?.toInt()
            ?: 0
        val notDepositedCount = (attachments["not_deposited_count"] as? Number)?.toInt()
            ?: (attachments["notDepositedCount"] as? Number)?.toInt()
            ?: (attachments["no_deposit_count"] as? Number)?.toInt()
            ?: 0

        val resolvedMessageId = dto.id.takeIf { it.isNotBlank() } ?: dto.clientMessageId

        val replySender = dto.replyTo?.senderName
        val replyText = dto.replyTo?.text
        val replyEventId = (dto.replyTo?.messageId ?: dto.replyToMessageId)?.hashCode()

        return ThreadEvent(
            id = numericId,
            messageId = resolvedMessageId,
            text = dto.messageText ?: "",
            time = timeFormatted,
            isCurrentUser = isOutgoing,
            status = if (isSystem) null else msgStatus,
            type = bubbleType,
            senderName = if (isSystem) null else resolvedSenderName,
            senderPhone = resolvedPhone,
            senderInitials = resolvedSenderName.take(1).uppercase(Locale.getDefault()).ifBlank { "?" },
            senderColor = resolvedSenderColor,
            senderAvatarUrl = resolvedAvatarUrl,
            quotedSender = replySender,
            quotedText = replyText,
            quotedEventId = replyEventId,
            fullDepositCount = fullDepositCount,
            halfDepositCount = halfDepositCount,
            notDepositedCount = notDepositedCount,
            imageUri = if (bubbleType == ThreadBubbleType.IMAGE) resolvedMediaUrl else null,
            imageUris = if (bubbleType == ThreadBubbleType.IMAGE && !resolvedImageUris.isNullOrEmpty()) resolvedImageUris.drop(1) else null,
            imageAspectRatio = if (bubbleType == ThreadBubbleType.IMAGE) resolvedAspectRatio else null,
            voiceUri = if (bubbleType == ThreadBubbleType.VOICE) resolvedMediaUrl else null,
            amount = amount,
            paymentMethod = paymentMethod,
            referenceId = referenceId,
            cycleNote = cycleNote,
            isVerified = isVerified,
            receiptUrl = attachments["receipt_url"] as? String,
            fullDate = if (createdAtMillis > 0L) createdAtMillis.toString() else null,
            locationPlace = locationPlace,
            locationSubtitle = locationSubtitle,
            documentData = documentData,
            videoData = videoData,
            pollData = pollData,
            securityRules = securityRules,
            securityRuleUpdate = securityRuleUpdate,
            securityPoll = securityPoll,
            savingsReceived = savingsReceived,
            actionPromptData = actionPromptData,
            paymentUpdate = paymentUpdate,
            weeklyReport = weeklyReport,
            statHistogram = statHistogram,
            statCandlestick = statCandlestick,
            statArea = statArea,
            statWaterfall = statWaterfall,
            statScatter = statScatter,
            statBoxPlot = statBoxPlot,
            statHeatmap = statHeatmap,
            statGroupSnapshot = statGroupSnapshot,
            groupHealth = groupHealth,
            statGroupProgress = statGroupProgress,
            statCustomChart = statCustomChart,
            systemMessage = if (bubbleType == ThreadBubbleType.SYSTEM) (dto.messageText ?: "") else null
        )
    }
}

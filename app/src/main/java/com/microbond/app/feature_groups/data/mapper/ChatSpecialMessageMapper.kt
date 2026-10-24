package com.microbond.app.feature_groups.data.mapper

import com.microbond.app.core.data_classes_and_enums.ActionPromptData
import com.microbond.app.core.data_classes_and_enums.DocumentData
import com.microbond.app.core.data_classes_and_enums.PaymentUpdateData
import com.microbond.app.core.data_classes_and_enums.PollOption
import com.microbond.app.core.data_classes_and_enums.SavingsReceivedData
import com.microbond.app.core.data_classes_and_enums.SecurityPollData
import com.microbond.app.core.data_classes_and_enums.SecurityRuleUpdateData
import com.microbond.app.core.data_classes_and_enums.SecurityRulesData
import com.microbond.app.core.data_classes_and_enums.StandardPollData
import com.microbond.app.core.data_classes_and_enums.VideoData
import com.microbond.app.core.data_classes_and_enums.WeeklyReportData
import com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatMessageDto

object ChatSpecialMessageMapper {

    fun parsePollOptions(rawOptions: Any?): List<PollOption> {
        val list = rawOptions as? List<*> ?: return emptyList()
        return list.mapIndexedNotNull { index, item ->
            when (item) {
                is Map<*, *> -> {
                    val label = item["label"]?.toString() ?: item["text"]?.toString() ?: "Option ${index + 1}"
                    val pct = (item["percentage"] as? Number)?.toInt() ?: 0
                    val sel = (item["is_selected"] as? Boolean) ?: false
                    PollOption(id = "opt_$index", label = label, percentage = pct, isSelected = sel)
                }
                is String -> {
                    PollOption(id = "opt_$index", label = item, percentage = 0, isSelected = false)
                }
                else -> null
            }
        }
    }

    fun parsePoll(dto: GroupChatMessageDto, attachments: Map<String, Any?>): StandardPollData {
        return StandardPollData(
            question = dto.messageText ?: "Poll question",
            questionSubtitle = (attachments["subtitle"] as? String) ?: "",
            footerText = (attachments["footer"] as? String) ?: "No votes yet",
            options = parsePollOptions(attachments["options"])
        )
    }

    fun parseSecurityRules(dto: GroupChatMessageDto, attachments: Map<String, Any?>): SecurityRulesData {
        return SecurityRulesData(
            title = (attachments["title"] as? String) ?: "Group rules",
            rules = (attachments["rules"] as? List<*>)?.mapNotNull { it?.toString() } ?: listOf(dto.messageText ?: "")
        )
    }

    fun parseSecurityRuleUpdate(dto: GroupChatMessageDto, attachments: Map<String, Any?>): SecurityRuleUpdateData {
        return SecurityRuleUpdateData(
            ruleText = dto.messageText ?: "Group rules updated",
            title = (attachments["title"] as? String) ?: "Group rule updated"
        )
    }

    fun parseSecurityPoll(dto: GroupChatMessageDto, attachments: Map<String, Any?>): SecurityPollData {
        return SecurityPollData(
            questionTitle = dto.messageText ?: "Security poll",
            questionSubtitle = (attachments["subtitle"] as? String) ?: "",
            options = parsePollOptions(attachments["options"])
        )
    }

    fun parseSavingsReceived(dto: GroupChatMessageDto, senderName: String): SavingsReceivedData {
        return SavingsReceivedData(
            title = "Savings received",
            subtitle = dto.messageText ?: "Your payout has been disbursed",
            recipientName = senderName
        )
    }

    fun parseActionPrompt(dto: GroupChatMessageDto, attachments: Map<String, Any?>): ActionPromptData {
        return ActionPromptData(
            message = dto.messageText ?: "Action required",
            actionText = (attachments["action_text"] as? String) ?: "View details"
        )
    }

    fun parsePaymentUpdate(
        amount: String?,
        attachments: Map<String, Any?>,
        timeFormatted: String,
        paymentMethod: String?
    ): PaymentUpdateData {
        return PaymentUpdateData(
            depositedAmountText = amount ?: "UGX 0",
            requiredAmountText = attachments["required_amount"] as? String,
            dateText = timeFormatted,
            paymentMethodText = paymentMethod
        )
    }

    fun parseWeeklyReport(dto: GroupChatMessageDto, attachments: Map<String, Any?>): WeeklyReportData {
        return WeeklyReportData(
            weekLabel = (attachments["week_label"] as? String) ?: "WEEKLY SAVING REPORT",
            groupName = (attachments["group_name"] as? String) ?: "",
            dailyTarget = (attachments["daily_target"] as? String) ?: "UGX 10,000"
        )
    }

    fun parseDocument(dto: GroupChatMessageDto, attachments: Map<String, Any?>, mediaUrl: String?): DocumentData {
        return DocumentData(
            fileName = (attachments["file_name"] as? String) ?: (attachments["filename"] as? String) ?: dto.messageText ?: "Document",
            fileType = (attachments["file_type"] as? String) ?: (attachments["mime_type"] as? String) ?: "PDF",
            fileSize = (attachments["file_size"] as? String) ?: "1.2 MB",
            uri = mediaUrl
        )
    }

    fun parseVideo(dto: GroupChatMessageDto, attachments: Map<String, Any?>): VideoData {
        return VideoData(
            duration = (attachments["duration"] as? String) ?: "0:15",
            caption = dto.messageText
        )
    }
}

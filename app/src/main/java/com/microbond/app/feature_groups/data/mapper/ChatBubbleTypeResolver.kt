package com.microbond.app.feature_groups.data.mapper

import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import java.util.Locale

object ChatBubbleTypeResolver {

    fun resolve(
        isSystem: Boolean,
        messageType: String,
        mediaType: String?,
        hasReplyTo: Boolean,
        textLower: String
    ): ThreadBubbleType {
        if (isSystem) {
            return when {
                textLower.contains("rule updated") || textLower.contains("rule update") -> ThreadBubbleType.SECURITY_RULE_UPDATE
                textLower.contains("rule") -> ThreadBubbleType.SECURITY_RULES
                textLower.contains("action") || textLower.contains("prompt") -> ThreadBubbleType.ACTION_PROMPT
                textLower.contains("contributed") || textLower.contains("deposit") -> ThreadBubbleType.DEPOSIT
                textLower.contains("payout") || textLower.contains("received") -> ThreadBubbleType.SAVINGS_RECEIVED
                else -> ThreadBubbleType.SYSTEM
            }
        }

        return when (messageType.uppercase(Locale.getDefault())) {
            "TEXT" -> {
                if (hasReplyTo) ThreadBubbleType.REPLY else ThreadBubbleType.TEXT
            }
            "MEDIA" -> when (mediaType?.uppercase(Locale.getDefault())) {
                "IMAGE", "PHOTO" -> ThreadBubbleType.IMAGE
                "VOICE_NOTE", "VOICE", "AUDIO" -> ThreadBubbleType.VOICE
                "VIDEO" -> ThreadBubbleType.VIDEO
                "DOCUMENT", "DOC", "FILE" -> ThreadBubbleType.DOCUMENT
                else -> ThreadBubbleType.TEXT
            }
            "SYSTEM_EVENT", "SYSTEM" -> {
                when {
                    textLower.contains("rule updated") || textLower.contains("rule update") -> ThreadBubbleType.SECURITY_RULE_UPDATE
                    textLower.contains("rule") -> ThreadBubbleType.SECURITY_RULES
                    textLower.contains("action") || textLower.contains("prompt") -> ThreadBubbleType.ACTION_PROMPT
                    textLower.contains("contributed") || textLower.contains("deposit") -> ThreadBubbleType.DEPOSIT
                    textLower.contains("payout") || textLower.contains("received") -> ThreadBubbleType.SAVINGS_RECEIVED
                    else -> ThreadBubbleType.SYSTEM
                }
            }
            "DEPOSIT", "CONTRIBUTION" -> ThreadBubbleType.DEPOSIT
            "DEPOSIT_STATUS" -> ThreadBubbleType.DEPOSIT_STATUS
            "SAVINGS_RECEIVED" -> ThreadBubbleType.SAVINGS_RECEIVED
            "PAYMENT_UPDATE" -> ThreadBubbleType.PAYMENT_UPDATE
            "WEEKLY_REPORT" -> ThreadBubbleType.WEEKLY_REPORT
            "POLL", "VOTE" -> ThreadBubbleType.POLL
            "SECURITY_POLL" -> ThreadBubbleType.SECURITY_POLL
            "GROUP_HEALTH" -> ThreadBubbleType.GROUP_HEALTH
            "STAT_HISTOGRAM" -> ThreadBubbleType.STAT_HISTOGRAM
            "STAT_CANDLESTICK" -> ThreadBubbleType.STAT_CANDLESTICK
            "STAT_AREA" -> ThreadBubbleType.STAT_AREA
            "STAT_WATERFALL" -> ThreadBubbleType.STAT_WATERFALL
            "STAT_SCATTER" -> ThreadBubbleType.STAT_SCATTER
            "STAT_BOX_PLOT", "STAT_BOXPLOT" -> ThreadBubbleType.STAT_BOXPLOT
            "STAT_HEATMAP" -> ThreadBubbleType.STAT_HEATMAP
            "STAT_GROUP_SNAPSHOT" -> ThreadBubbleType.STAT_GROUP_SNAPSHOT
            "STAT_GROUP_PROGRESS" -> ThreadBubbleType.STAT_GROUP_PROGRESS
            "STAT_CUSTOM_CHART" -> ThreadBubbleType.STAT_CUSTOM_CHART
            "CONTACT" -> ThreadBubbleType.CONTACT
            "LOCATION" -> ThreadBubbleType.LOCATION
            else -> ThreadBubbleType.TEXT
        }
    }
}

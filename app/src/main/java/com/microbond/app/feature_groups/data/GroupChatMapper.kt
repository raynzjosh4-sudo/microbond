package com.microbond.app.feature_groups.data

import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatMessageDto

/**
 * Backward compatibility delegator.
 * Core chat mapping logic has been modularized into sub-files under [com.microbond.app.feature_groups.data.mapper]:
 * - GroupChatMapper: Orchestration facade
 * - ChatBubbleTypeResolver: Message bubble classification
 * - ChatJsonHelpers: Safe JSON parsing & timestamp formatting
 * - ChatChartMessageMapper: Financial chart parsing
 * - ChatSpecialMessageMapper: Polls, security rules, and reports
 */
object GroupChatMapper {

    fun toThreadEvent(dto: GroupChatMessageDto, currentUserId: String): ThreadEvent {
        return com.microbond.app.feature_groups.data.mapper.GroupChatMapper.toThreadEvent(dto, currentUserId)
    }
}

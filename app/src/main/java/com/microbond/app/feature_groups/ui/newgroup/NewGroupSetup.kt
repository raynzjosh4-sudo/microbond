package com.microbond.app.feature_groups.ui.newgroup

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType

/**
 * The final product of the new-group creation flow.
 * Passed back to [NewGroupFlow] → [microbondApp] on completion.
 */
data class NewGroupSetup(
    val name: String,
    val participants: List<ContactEntry> = emptyList(),
    val groupId: String? = null,
    val inviteCode: String? = null,
    val groupType: SavingsGroupType = SavingsGroupType.GOAL_SAVINGS
)


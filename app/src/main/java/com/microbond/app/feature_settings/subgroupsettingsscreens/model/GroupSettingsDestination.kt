package com.microbond.app.feature_settings.subgroupsettingsscreens.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType

data class GroupSettingsDestination(
    val page: GroupSettingsPage,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

fun groupSettingsDestinations(groupType: SavingsGroupType): List<GroupSettingsDestination> =
    listOf(
        GroupSettingsDestination(
            GroupSettingsPage.PERSONAL,
            "Your settings",
            "Private preferences for you",
            Icons.Default.Lock
        ),
        GroupSettingsDestination(
            GroupSettingsPage.TYPE_RULES,
            typeRulesTitle(groupType),
            "Rules and financial controls for this group",
            Icons.Default.Settings
        ),
        GroupSettingsDestination(
            GroupSettingsPage.GOVERNANCE,
            "Governance & audit",
            "Approvals, signers, and transaction oversight",
            Icons.Default.Security
        ),
        GroupSettingsDestination(
            GroupSettingsPage.TRUST,
            "Trust & members",
            "Member reputation and eligibility rules",
            Icons.Default.VerifiedUser
        ),
        GroupSettingsDestination(
            GroupSettingsPage.LIQUIDITY,
            "Liquidity & reminders",
            "Fund stability, due dates, and escalation",
            Icons.Default.Notifications
        ),
        GroupSettingsDestination(
            GroupSettingsPage.PRIVACY,
            "Privacy & records",
            "Group rules, exports, and retention",
            Icons.Default.Shield
        ),
        GroupSettingsDestination(
            GroupSettingsPage.TRANSACTION_MESSAGES,
            transactionMessagesTitle(groupType),
            "Notices posted when transactions are confirmed",
            Icons.Default.Payments
        ),
        GroupSettingsDestination(
            GroupSettingsPage.REMINDERS_REPORTS,
            "Reminders & reports",
            "Scheduled summaries and payment reminders",
            Icons.Default.CalendarMonth
        ),
        GroupSettingsDestination(
            GroupSettingsPage.MILESTONES_SECURITY,
            milestonesTitle(groupType),
            "Progress announcements and safety updates",
            Icons.AutoMirrored.Filled.ShowChart
        )
    )

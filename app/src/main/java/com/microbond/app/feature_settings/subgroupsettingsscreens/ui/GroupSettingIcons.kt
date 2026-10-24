package com.microbond.app.feature_settings.subgroupsettingsscreens.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

internal fun iconFor(title: String): ImageVector = when {
    title.contains("member", ignoreCase = true) || title.contains("vouch", ignoreCase = true) ->
        Icons.Default.Groups
    title.contains("payment", ignoreCase = true) ||
        title.contains("payout", ignoreCase = true) ||
        title.contains("contribution", ignoreCase = true) ->
        Icons.Default.Payments
    title.contains("bill", ignoreCase = true) ||
        title.contains("wallet", ignoreCase = true) ||
        title.contains("reserve", ignoreCase = true) ->
        Icons.Default.AccountBalance
    title.contains("reminder", ignoreCase = true) ||
        title.contains("schedule", ignoreCase = true) ||
        title.contains("period", ignoreCase = true) ->
        Icons.Default.Schedule
    title.contains("privacy", ignoreCase = true) ||
        title.contains("private", ignoreCase = true) ||
        title.contains("lock", ignoreCase = true) ->
        Icons.Default.Lock
    title.contains("trust", ignoreCase = true) ||
        title.contains("security", ignoreCase = true) ||
        title.contains("audit", ignoreCase = true) ->
        Icons.Default.Shield
    title.contains("report", ignoreCase = true) ||
        title.contains("progress", ignoreCase = true) ||
        title.contains("snapshot", ignoreCase = true) ->
        Icons.AutoMirrored.Filled.ShowChart
    title.contains("alert", ignoreCase = true) ||
        title.contains("warning", ignoreCase = true) ||
        title.contains("dispute", ignoreCase = true) ->
        Icons.Default.Warning
    title.contains("charter", ignoreCase = true) ||
        title.contains("agreement", ignoreCase = true) ||
        title.contains("rule", ignoreCase = true) ->
        Icons.Default.Info
    title.contains("message", ignoreCase = true) ||
        title.contains("notify", ignoreCase = true) ||
        title.contains("announce", ignoreCase = true) ->
        Icons.Default.Notifications
    title.contains("group", ignoreCase = true) ||
        title.contains("roster", ignoreCase = true) ->
        Icons.Default.Group
    else -> Icons.Default.Settings
}

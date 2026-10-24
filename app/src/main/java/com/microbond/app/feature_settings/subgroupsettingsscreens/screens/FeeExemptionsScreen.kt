package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet

@Composable
fun FeeExemptionsScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val exemptMembers = remember { mutableStateListOf<String>() }
    var showMemberPicker by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Fee exemptions",
                subtitle = groupName,
                onBack = onBack
            )
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            WaSettingsSectionHeader("Exempt members")

            exemptMembers.forEach { member ->
                WaSettingsRow(
                    icon = Icons.Default.Person,
                    title = member,
                    subtitle = "Exempt from fees",
                    onClick = { /* Remove exemption logic */ }
                )
                WaSettingsDivider()
            }

            WaSettingsRow(
                icon = Icons.Default.PersonAdd,
                title = "Add member to exemptions",
                subtitle = "Selected members will not be automatically charged late fees or penalties.",
                onClick = { showMemberPicker = true }
            )

            WaSettingsDivider()
            
            if (exemptMembers.isEmpty()) {
                Text(
                    text = "No members are currently exempt.",
                    color = androidx.compose.ui.graphics.Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
                )
            }
        }
    }

    if (showMemberPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select member",
            options = listOf("John Doe", "Jane Smith", "Alice Johnson", "Bob Williams"),
            currentSelection = selectedMember,
            onSelect = { 
                selectedMember = it
                if (!exemptMembers.contains(it)) {
                    exemptMembers.add(it)
                }
                showMemberPicker = false 
            },
            onDismiss = { showMemberPicker = false }
        )
    }
}

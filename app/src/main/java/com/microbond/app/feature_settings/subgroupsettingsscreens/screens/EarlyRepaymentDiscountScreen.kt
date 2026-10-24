package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet

@Composable
fun EarlyRepaymentDiscountScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var enableDiscount by remember { mutableStateOf(false) }
    var discountRate by remember { mutableStateOf("5%") }
    var showRatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Early repayment discount",
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
            WaSettingsSectionHeader("Incentives")

            WaSettingsSwitchRow(
                icon = Icons.Default.LocalOffer,
                title = "Offer early repayment discounts",
                subtitle = "Reduce the total interest owed if a member repays their loan ahead of the scheduled deadline.",
                checked = enableDiscount,
                onCheckedChange = { enableDiscount = it }
            )

            if (enableDiscount) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.Percent,
                    title = "Discount rate",
                    value = discountRate,
                    onClick = { showRatePicker = true }
                )
            }
        }
    }

    if (showRatePicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select discount rate",
            options = listOf("2%", "5%", "10%", "15%", "20%"),
            currentSelection = discountRate,
            onSelect = { 
                discountRate = it 
                showRatePicker = false 
            },
            onDismiss = { showRatePicker = false }
        )
    }
}

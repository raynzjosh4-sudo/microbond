package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow

@Composable
fun MyActiveLoanDetailsScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "My active loan details",
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
            Spacer(modifier = Modifier.height(16.dp))

            // Big balance card at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Outstanding Balance", color = MicrobondInactiveGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "2,500,000 UGX",
                        color = MicrobondTextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Includes 12% interest",
                        color = MicrobondActiveGreen,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            WaSettingsSectionHeader("Loan terms & actions")

            WaSettingsValueRow(
                icon = Icons.Default.CalendarToday,
                title = "Next payment due",
                value = "Oct 15, 2026",
                onClick = {}
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Payment,
                title = "Make a repayment",
                subtitle = "Pay via your registered mobile money or card.",
                onClick = { /* Open payment flow */ }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Receipt,
                title = "View repayment schedule",
                subtitle = "See past payments and upcoming installments.",
                onClick = { /* Open schedule */ }
            )
        }
    }
}

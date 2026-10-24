package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.switchbutton.MicrobondSwitch
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.BillSplitSettingsScreen
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.GenericSettingsScreen
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.GoalSavingsSettingsScreen
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.MerryGoRoundSettingsScreen

@Composable
fun GroupFinancialSettingsScreen(
    groupType: SavingsGroupType,
    onBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            SettingsTopBar(title = "Rules & financials", onBack = onBack)
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(insets)
        ) {
            Text(
                text = "Set up the rules for ${groupType.uiLabel}",
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 4.dp)
            )

            when (groupType) {
                SavingsGroupType.MERRY_GO_ROUND -> MerryGoRoundSettingsScreen()
                SavingsGroupType.GOAL_SAVINGS -> GoalSavingsSettingsScreen()
                SavingsGroupType.BILL_SPLIT -> BillSplitSettingsScreen()
                else -> GenericSettingsScreen()
            }
        }
    }
}

// ── Reusable UI Components ──────────────────────────────────────────────────

@Composable
private fun SettingsTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MicrobondDarkBackground)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MicrobondTextPrimary
            )
        }
        Text(
            text = title,
            color = MicrobondTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

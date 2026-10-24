package com.microbond.app.feature_groups.ui.newgroup.financialsettings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.feature_groups.ui.newgroup.widgets.SettingsSelectionRow
import com.microbond.app.feature_groups.ui.newgroup.widgets.SettingsSwitchRow
import com.microbond.app.feature_groups.ui.newgroup.widgets.FullScreenSettingPicker
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.subscreens.ContributionAmountScreen
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.subscreens.CollectionFrequencyScreen
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.subscreens.MaximumCapacityScreen
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.subscreens.PayoutsPerCycleScreen
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.subscreens.LatePenaltyScreen
import com.microbond.app.feature_groups.ui.newgroup.financialsettings.subscreens.GroupStartTimeScreen

@Composable
fun MerryGoRoundSettingsScreen() {
    var partialPayments by remember { mutableStateOf(false) }
    var advanceDeposits by remember { mutableStateOf(true) }
    
    // Values
    var amount by remember { mutableStateOf("UGX 10,000") }
    var frequency by remember { mutableStateOf("Weekly") }
    var capacity by remember { mutableStateOf("150 members") }
    var payoutsPerCycle by remember { mutableStateOf("10 members per cycle") }
    var strategy by remember { mutableStateOf("Randomized by App") }
    var kycTier by remember { mutableStateOf("Tier 2 (ID Verified)") }
    var gracePeriod by remember { mutableStateOf("24 hours") }
    var latePenalty by remember { mutableStateOf("UGX 5,000") }
    var defaultAction by remember { mutableStateOf("Charge guarantor after 48h") }

    // Picker state
    var currentPicker by remember { mutableStateOf(SettingPickerType.NONE) }

    SettingsSelectionRow(
        icon = Icons.Default.AccountBalanceWallet,
        title = "Contribution amount",
        value = amount,
        description = "The fixed amount each member must deposit during every collection cycle.",
        onClick = { currentPicker = SettingPickerType.CONTRIBUTION_AMOUNT }
    )

    SettingsSelectionRow(
        icon = Icons.Default.Timer,
        title = "Collection frequency",
        value = frequency,
        onClick = { currentPicker = SettingPickerType.FREQUENCY }
    )

    SettingsSelectionRow(
        icon = Icons.Default.People,
        title = "Maximum member capacity",
        value = capacity,
        onClick = { currentPicker = SettingPickerType.CAPACITY }
    )

    SettingsSelectionRow(
        icon = Icons.Default.FormatListNumbered,
        title = "Payouts per cycle",
        value = payoutsPerCycle,
        onClick = { currentPicker = SettingPickerType.PAYOUTS_PER_CYCLE }
    )

    SettingsSelectionRow(
        icon = Icons.Default.FormatListNumbered,
        title = "Payout order strategy",
        value = strategy,
        onClick = { currentPicker = SettingPickerType.STRATEGY }
    )

    SettingsSelectionRow(
        icon = Icons.Default.Loop,
        title = "Number of cycles",
        value = "15 cycles",
        onClick = {}
    )

    var groupStartTime by remember { mutableStateOf("Starts when group is full") }

    SettingsSelectionRow(
        icon = Icons.Default.DateRange,
        title = "Group start time",
        value = groupStartTime,
        onClick = { currentPicker = SettingPickerType.GROUP_START_TIME }
    )

    Spacer(Modifier.height(16.dp))
    Text(
        text = "Governance & Trust",
        color = MicrobondInactiveGray,
        fontSize = 14.sp,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
    )

    SettingsSelectionRow(
        icon = Icons.Default.VpnKey,
        title = "Minimum KYC Tier",
        value = kycTier,
        onClick = { currentPicker = SettingPickerType.KYC_TIER }
    )

    SettingsSelectionRow(
        icon = Icons.Default.VpnKey,
        title = "Minimum KYC Tier",
        value = kycTier,
        onClick = { currentPicker = SettingPickerType.KYC_TIER }
    )

    var requiresGuarantor by remember { mutableStateOf(true) }
    SettingsSwitchRow(
        icon = Icons.Default.GroupAdd,
        title = "Require Guarantor",
        description = "New members must have an accepted guarantor to occupy a payout slot.",
        checked = requiresGuarantor,
        onCheckedChange = { requiresGuarantor = it }
    )

    Spacer(Modifier.height(16.dp))
    Text(
        text = "Liquidity & Penalties",
        color = MicrobondInactiveGray,
        fontSize = 14.sp,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
    )

    SettingsSelectionRow(
        icon = Icons.Default.Timer,
        title = "Extra time to pay",
        value = gracePeriod,
        onClick = { currentPicker = SettingPickerType.GRACE_PERIOD }
    )

    SettingsSelectionRow(
        icon = Icons.Default.Warning,
        title = "Late payment penalty",
        value = latePenalty,
        onClick = { currentPicker = SettingPickerType.LATE_PENALTY }
    )

    SettingsSelectionRow(
        icon = Icons.Default.Warning,
        title = "Default Action (Guarantor)",
        value = defaultAction,
        onClick = { currentPicker = SettingPickerType.DEFAULT_ACTION }
    )

    Spacer(Modifier.height(16.dp))
    Text(
        text = "Payment Flexibility",
        color = MicrobondInactiveGray,
        fontSize = 14.sp,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
    )

    SettingsSwitchRow(
        icon = Icons.Default.Paid,
        title = "Allow partial payments",
        description = "Members can deposit a fraction of the amount and pay the rest later before deadline.",
        checked = partialPayments,
        onCheckedChange = { partialPayments = it }
    )

    SettingsSwitchRow(
        icon = Icons.Default.DateRange,
        title = "Allow advance deposits",
        description = "Members can deposit a large amount upfront (e.g. 100,000) to cover future cycles automatically.",
        checked = advanceDeposits,
        onCheckedChange = { advanceDeposits = it }
    )

    when (currentPicker) {
        SettingPickerType.FREQUENCY -> CollectionFrequencyScreen(
            currentValue = frequency,
            onSave = { 
                frequency = it
                currentPicker = SettingPickerType.NONE 
            },
            onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.CONTRIBUTION_AMOUNT -> ContributionAmountScreen(
            currentValue = amount,
            onSave = { 
                amount = it
                currentPicker = SettingPickerType.NONE 
            },
            onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.CAPACITY -> MaximumCapacityScreen(
            currentValue = capacity,
            onSave = { 
                capacity = it
                currentPicker = SettingPickerType.NONE 
            },
            onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.PAYOUTS_PER_CYCLE -> PayoutsPerCycleScreen(
            currentValue = payoutsPerCycle,
            onSave = { 
                payoutsPerCycle = it
                currentPicker = SettingPickerType.NONE 
            },
            onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.STRATEGY -> FullScreenSettingPicker(
            title = "Payout strategy", currentValue = strategy,
            options = listOf("Randomized by App", "Admin-Assigned", "First-Come, First-Served"),
            onSave = { strategy = it }, onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.KYC_TIER -> FullScreenSettingPicker(
            title = "Minimum KYC Tier", currentValue = kycTier,
            options = listOf("Unverified", "Tier 1 (Phone Verified)", "Tier 2 (ID Verified)", "Tier 3 (Address Verified)"),
            onSave = { kycTier = it }, onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.GRACE_PERIOD -> FullScreenSettingPicker(
            title = "Extra time to pay", currentValue = gracePeriod,
            options = listOf("None", "12 hours", "24 hours", "48 hours"),
            onSave = { gracePeriod = it }, onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.LATE_PENALTY -> LatePenaltyScreen(
            currentValue = latePenalty,
            onSave = { 
                latePenalty = it
                currentPicker = SettingPickerType.NONE 
            },
            onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.GROUP_START_TIME -> GroupStartTimeScreen(
            currentValue = groupStartTime,
            onSave = { 
                groupStartTime = it
                currentPicker = SettingPickerType.NONE 
            },
            onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.DEFAULT_ACTION -> FullScreenSettingPicker(
            title = "Guarantor Action", currentValue = defaultAction,
            options = listOf("Charge guarantor after 24h", "Charge guarantor after 48h", "Charge guarantor immediately"),
            onSave = { defaultAction = it }, onBack = { currentPicker = SettingPickerType.NONE }
        )
        SettingPickerType.NONE -> {}
    }
}

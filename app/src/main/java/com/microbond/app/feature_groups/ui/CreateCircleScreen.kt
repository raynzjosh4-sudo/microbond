package com.microbond.app.feature_groups.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.data_classes_and_enums.ChartPreview
import com.microbond.app.core.dummydata.sampleChats
import com.microbond.app.feature_groups.data.TrustConnection
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

enum class CircleStructure(val label: String) {
    ROSCA("Rotating circle"),
    VAULT("Savings vault"),
    INVESTMENT("Investment club")
}

data class CircleSetup(
    val structure: CircleStructure,
    val name: String,
    val avatarColor: Color,
    val objective: String,
    val contributionAmount: String,
    val frequency: String,
    val targetAmount: String,
    val participantNames: Set<String>,
    val payoutOrder: String,
    val requireDepositApproval: Boolean,
    val requireAdminApproval: Boolean,
    val membersCanInvite: Boolean
)

@Composable
fun CreateCircleScreen(
    onBack: () -> Unit,
    onCreateCircle: (CircleSetup) -> Unit,
    trustedConnections: List<TrustConnection> = emptyList()
) {
    var structure by remember { mutableStateOf(CircleStructure.ROSCA) }
    var name by remember { mutableStateOf("") }
    var avatarColor by remember { mutableStateOf(Color(0xFF23483D)) }
    var objective by remember { mutableStateOf("") }
    var contribution by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Weekly") }
    var target by remember { mutableStateOf("") }
    var payoutOrder by remember { mutableStateOf("Invite order") }
    var requireDepositApproval by remember { mutableStateOf(false) }
    var requireAdminApproval by remember { mutableStateOf(true) }
    var membersCanInvite by remember { mutableStateOf(false) }
    var selectedParticipants by remember { mutableStateOf(emptySet<String>()) }

    val contacts = remember(trustedConnections) {
        (sampleChats.filterNot(ChartPreview::isGroup).map { it.name to it.message } +
            trustedConnections.map { it.name to it.phoneNumber })
            .distinctBy { it.first }
    }
    val validAmount = contribution.toLongOrNull()?.let { it > 0 } == true
    val validTarget = structure == CircleStructure.ROSCA || target.toLongOrNull()?.let { it > 0 } == true
    val isValid = name.isNotBlank() && validAmount && validTarget && selectedParticipants.isNotEmpty()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Create a circle",
                showBackButton = true,
                showMoreIcon = false,
                onBackClick = onBack
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MicrobondDarkBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        onCreateCircle(
                            CircleSetup(
                                structure = structure,
                                name = name.trim(),
                                avatarColor = avatarColor,
                                objective = objective.trim(),
                                contributionAmount = contribution,
                                frequency = frequency,
                                targetAmount = target,
                                participantNames = selectedParticipants,
                                payoutOrder = payoutOrder,
                                requireDepositApproval = requireDepositApproval,
                                requireAdminApproval = requireAdminApproval,
                                membersCanInvite = membersCanInvite
                            )
                        )
                    },
                    enabled = isValid,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MicrobondActiveGreen,
                        contentColor = Color(0xFF07130F)
                    )
                ) {
                    Text("Create circle", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(insets)
                .padding(bottom = 12.dp)
        ) {
            FormSectionTitle("Group structure")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircleStructure.entries.forEach { option ->
                    FilterChip(
                        selected = structure == option,
                        onClick = { structure = option },
                        label = { Text(option.label) }
                    )
                }
            }

            FormSectionTitle("Circle profile")
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Circle name",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "Optional group avatar",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
            )
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(Color(0xFF23483D), Color(0xFF2B5668), Color(0xFF74513D), Color(0xFF57466F)).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (avatarColor == color) Modifier.border(2.dp, MicrobondActiveGreen, CircleShape)
                                else Modifier
                            )
                            .clickable { avatarColor = color },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = MicrobondTextPrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            AppTextField(
                value = objective,
                onValueChange = { objective = it },
                label = "Objective (optional)",
                modifier = Modifier.padding(horizontal = 16.dp),
                singleLine = false
            )

            FormSectionTitle("Contribution cycle")
            AppTextField(
                value = contribution,
                onValueChange = { contribution = it.filter(Char::isDigit) },
                label = "Contribution amount (UGX)",
                modifier = Modifier.padding(horizontal = 16.dp),
                keyboardType = KeyboardType.Number
            )
            Text(
                text = "Payment frequency",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 8.dp)
            )
            OptionChips(
                options = listOf("Weekly", "Monthly"),
                selected = frequency,
                onSelect = { frequency = it }
            )
            AppTextField(
                value = target,
                onValueChange = { target = it.filter(Char::isDigit) },
                label = if (structure == CircleStructure.ROSCA) "Total target (UGX, optional)" else "Total target (UGX)",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                keyboardType = KeyboardType.Number
            )

            if (structure == CircleStructure.ROSCA) {
                FormSectionTitle("Payout order")
                OptionChips(
                    options = listOf("Invite order", "Random draw", "Admin set"),
                    selected = payoutOrder,
                    onSelect = { payoutOrder = it }
                )
            }

            FormSectionTitle("Invite participants")
            Text(
                text = "Choose trusted contacts to invite",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            contacts.forEach { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedParticipants = if (contact.first in selectedParticipants) {
                                selectedParticipants - contact.first
                            } else {
                                selectedParticipants + contact.first
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = contact.first in selectedParticipants,
                        onCheckedChange = { checked ->
                            selectedParticipants = if (checked) {
                                selectedParticipants + contact.first
                            } else {
                                selectedParticipants - contact.first
                            }
                        }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(contact.first, color = MicrobondTextPrimary, fontSize = 15.sp)
                        Text(contact.second, color = MicrobondInactiveGray, fontSize = 12.sp, maxLines = 1)
                    }
                }
            }

            FormSectionTitle("Governance")
            SettingSwitchRow(
                title = "Require approval for contributions",
                checked = requireDepositApproval,
                onCheckedChange = { requireDepositApproval = it }
            )
            SettingSwitchRow(
                title = "Require admin approval for withdrawals",
                checked = requireAdminApproval,
                onCheckedChange = { requireAdminApproval = it }
            )
            SettingSwitchRow(
                title = "Allow members to invite others",
                checked = membersCanInvite,
                onCheckedChange = { membersCanInvite = it }
            )
        }
    }
}

@Composable
internal fun FormSectionTitle(title: String) {
    Text(
        text = title,
        color = MicrobondTextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, top = 18.dp, bottom = 8.dp)
    )
}

@Composable
internal fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MicrobondActiveGreen,
            unfocusedBorderColor = MicrobondDividerDark,
            focusedTextColor = MicrobondTextPrimary,
            unfocusedTextColor = MicrobondTextPrimary,
            focusedLabelColor = MicrobondActiveGreen,
            unfocusedLabelColor = MicrobondInactiveGray,
            cursorColor = MicrobondActiveGreen
        )
    )
}

@Composable
internal fun OptionChips(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(option) }
            )
        }
    }
}

@Composable
internal fun SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MicrobondTextPrimary,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
    HorizontalDivider(color = MicrobondDividerDark, thickness = 0.5.dp)
}
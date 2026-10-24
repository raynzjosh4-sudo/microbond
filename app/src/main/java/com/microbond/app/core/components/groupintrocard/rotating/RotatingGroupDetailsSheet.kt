package com.microbond.app.core.components.groupintrocard.rotating

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground

/**
 * Dedicated BottomSheet displaying the full, detailed API data for a Rotating Group (MERRY_GO_ROUND / ROSCA).
 *
 * Pixel-accurate implementation matching the official Microbond rotating group specification:
 * - Top Bar: Back arrow, uppercase group name, dynamic subtitle, 3-dots more menu
 * - Pinned Summary Banner: Pushpin icon, contribution per cycle, vertical divider, lump sum payout, bottom divider
 * - Section 1: The Financial Deal (Contribution, Frequency, Lump Sum, Current Round)
 * - Section 2: Rotation Roster (Live line slots, active turn, your open slot, emergency swap footnote)
 * - Section 3: Social Trust (Mutual contacts voucher selection with radio button & avatar initial)
 * - Section 4: Rules & Obligations (KYC, Late Penalty, Endorsement, Arrears Policy)
 * - Sticky Bottom Bar: Vivid green JOIN GROUP pill button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RotatingGroupDetailsSheet(
    state: RotatingGroupPreviewUiState,
    onDismissRequest: () -> Unit,
    onJoinGroupClick: (selectedGuarantorId: String?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedGuarantorId by remember {
        mutableStateOf(state.mutualContacts.firstOrNull()?.userId)
    }

    var isFinancialDealExpanded by remember { mutableStateOf(true) }
    var isRotationRosterExpanded by remember { mutableStateOf(true) }
    var isSocialTrustExpanded by remember { mutableStateOf(true) }
    var isRulesExpanded by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MicrobondDarkBackground,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MicrobondDarkBackground)
                .navigationBarsPadding()
        ) {
            // ── Top Bar ────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = state.name.uppercase(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = state.sheetHeaderSubtitle,
                        color = Color(0xFF8696A0),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { /* More options */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color.White
                    )
                }
            }

            // ── Pinned Summary Banner ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = null,
                    tint = Color(0xFF25D366),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.contributionFormatted,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val contributionLabel = if (state.frequencyFormatted.isBlank() || state.frequencyFormatted.equals("Per cycle", ignoreCase = true)) {
                        "Per cycle contribution"
                    } else {
                        "${state.frequencyFormatted} contribution"
                    }
                    Text(
                        text = contributionLabel,
                        color = Color(0xFF8696A0),
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(34.dp)
                        .background(Color(0xFF233138))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.potPayoutAmountOnly,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Lump sum you receive",
                        color = Color(0xFF8696A0),
                        fontSize = 12.sp
                    )
                }
            }

            HorizontalDivider(
                color = Color(0xFF1F2C34),
                thickness = 0.8.dp
            )

            // ── Scrollable Content Sections ────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ── Section 1: The Financial Deal ──────────────────────────────
                CollapsibleSectionHeader(
                    number = "1",
                    title = "THE FINANCIAL DEAL",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    isExpanded = isFinancialDealExpanded,
                    onToggle = { isFinancialDealExpanded = !isFinancialDealExpanded }
                )

                if (isFinancialDealExpanded) {
                    FinancialDealRow(
                        icon = Icons.Default.Paid,
                        title = "Contribution",
                        primaryValue = state.contributionFormatted,
                        secondaryValue = "Per cycle"
                    )
                    FinancialDealRow(
                        icon = Icons.Default.CalendarToday,
                        title = "Frequency",
                        primaryValue = state.frequencyFormatted,
                        secondaryValue = state.cycleScheduleText
                    )
                    FinancialDealRow(
                        icon = Icons.Default.CardGiftcard,
                        title = "You receive",
                        primaryValue = state.potPayoutAmountOnly,
                        secondaryValue = "Lump Sum",
                        isPrimaryGreen = true
                    )
                    FinancialDealRow(
                        icon = Icons.Default.Autorenew,
                        title = "Current round",
                        primaryValue = state.currentRoundFormatted
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── Section 2: Rotation Roster ─────────────────────────────────
                CollapsibleSectionHeader(
                    number = "2",
                    title = "ROTATION ROSTER",
                    icon = Icons.Default.Groups,
                    isExpanded = isRotationRosterExpanded,
                    onToggle = { isRotationRosterExpanded = !isRotationRosterExpanded }
                )

                if (isRotationRosterExpanded) {
                    state.rosterSlots.forEach { slot ->
                        val (badgeBg, textColor, statusColor, isBold) = when {
                            slot.isOpenSlot -> {
                                Quad(
                                    Color(0xFF00A884),
                                    Color(0xFF25D366),
                                    Color(0xFF25D366),
                                    true
                                )
                            }
                            slot.isCurrentRecipient -> {
                                Quad(
                                    Color(0xFF8A5314),
                                    Color.White,
                                    Color(0xFFFFA726),
                                    true
                                )
                            }
                            else -> {
                                Quad(
                                    Color(0xFF233138),
                                    Color.White,
                                    Color(0xFF8696A0),
                                    false
                                )
                            }
                        }

                        RotationRosterRow(
                            position = slot.position,
                            name = slot.memberName ?: "Open",
                            statusText = slot.statusText,
                            badgeBgColor = badgeBg,
                            textColor = textColor,
                            statusColor = statusColor,
                            isBold = isBold
                        )
                    }

                    // Emergency swap footnote
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF8696A0),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Turn swapping is allowed after joining if an emergency arises.",
                            color = Color(0xFF8696A0),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── Section 3: Social Trust ────────────────────────────────────
                CollapsibleSectionHeader(
                    number = "3",
                    title = "SOCIAL TRUST",
                    icon = Icons.Default.Groups,
                    isExpanded = isSocialTrustExpanded,
                    onToggle = { isSocialTrustExpanded = !isSocialTrustExpanded }
                )

                if (isSocialTrustExpanded) {
                    if (state.mutualContacts.isNotEmpty()) {
                        Text(
                            text = "Select a mutual contact to vouch for your entry into this circle:",
                            color = Color(0xFF8696A0),
                            fontSize = 12.5.sp,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 8.dp)
                        )

                        state.mutualContacts.forEach { contact ->
                            val isSelected = selectedGuarantorId == contact.userId
                            SocialTrustContactRow(
                                contact = contact,
                                isSelected = isSelected,
                                onSelect = { selectedGuarantorId = contact.userId }
                            )
                        }
                    } else {
                        Text(
                            text = "No mutual contacts required for entry into this circle.",
                            color = Color(0xFF8696A0),
                            fontSize = 12.5.sp,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── Section 4: Rules & Obligations ────────────────────────────
                CollapsibleSectionHeader(
                    number = "4",
                    title = "RULES & OBLIGATIONS",
                    icon = Icons.Default.Shield,
                    isExpanded = isRulesExpanded,
                    onToggle = { isRulesExpanded = !isRulesExpanded }
                )

                if (isRulesExpanded) {
                    RuleObligationRow(
                        icon = Icons.Default.Description,
                        title = "KYC Required",
                        value = state.requiredKycText
                    )
                    RuleObligationRow(
                        icon = Icons.Default.HourglassTop,
                        title = "Late Payment Penalty",
                        value = state.latePenaltyText
                    )
                    RuleObligationRow(
                        icon = Icons.Default.Handshake,
                        title = "Endorsement Policy",
                        value = state.guarantorRequirementText
                    )
                    RuleObligationRow(
                        icon = Icons.Default.Lock,
                        title = "Arrears Policy",
                        value = state.arrearsPolicyText
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // ── Sticky Bottom Bar ──────────────────────────────────────────────
            Surface(
                color = MicrobondDarkBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Button(
                        onClick = {
                            onJoinGroupClick(selectedGuarantorId)
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color(0xFF0B141A)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "JOIN GROUP",
                            color = Color(0xFF0B141A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollapsibleSectionHeader(
    number: String,
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF163C31)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF25D366),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "$number. $title",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            letterSpacing = 0.5.sp,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = Color(0xFF8696A0),
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun FinancialDealRow(
    icon: ImageVector,
    title: String,
    primaryValue: String,
    secondaryValue: String? = null,
    isPrimaryGreen: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8696A0),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = Color(0xFFE9EDEF),
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                text = primaryValue,
                color = if (isPrimaryGreen) Color(0xFF25D366) else Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            if (!secondaryValue.isNullOrBlank()) {
                Text(
                    text = secondaryValue,
                    color = Color(0xFF8696A0),
                    fontSize = 11.5.sp
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF53646F),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun RotationRosterRow(
    position: Int,
    name: String,
    statusText: String,
    badgeBgColor: Color,
    textColor: Color = Color.White,
    statusColor: Color = Color(0xFF8696A0),
    isBold: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(badgeBgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$position",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = name,
            color = textColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = statusText,
            color = statusColor,
            fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF53646F),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SocialTrustContactRow(
    contact: MutualContact,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF25D366) else Color(0xFF8696A0),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(0xFF233138)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.profileName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.profileName,
                color = Color.White,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "In your phonebook • Payout Slot #${contact.payoutPosition}",
                color = Color(0xFF8696A0),
                fontSize = 11.5.sp
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF53646F),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun RuleObligationRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8696A0),
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = Color(0xFFE9EDEF),
            fontSize = 13.5.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = Color(0xFF8696A0),
            fontSize = 12.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF53646F),
            modifier = Modifier.size(18.dp)
        )
    }
}

private data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

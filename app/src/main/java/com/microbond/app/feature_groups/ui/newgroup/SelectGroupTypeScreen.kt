package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondBottomSheetDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondGroupIconBgDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondInfoBannerButtonContent
import com.microbond.app.core.ui.theme.MicrobondInfoBannerContainer
import com.microbond.app.core.ui.theme.MicrobondInfoBannerIconBg
import com.microbond.app.core.ui.theme.MicrobondLockBadgeBg
import com.microbond.app.core.ui.theme.MicrobondLockIconColor
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondWarningAmber
import com.microbond.app.core.components.inforwidgetbanner.InfoWidgetBanner
import com.microbond.app.feature_groups.data.GroupCreationManager
import com.microbond.app.feature_groups.ui.newgroup.widgets.GroupTypeRow

/**
 * Screen 1 of the new-group flow.
 *
 * Calls GET /api/v1/users/me/allowed-groups before displaying group options,
 * reflecting Microbond's progressive unlock matrix based on KYC Tier, Trust Score,
 * and communal health history.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectGroupTypeScreen(
    onBack: () -> Unit,
    onGroupTypeSelected: (SavingsGroupType) -> Unit,
    onVerifyKycClick: (() -> Unit)? = null
) {
    val isLoading by GroupCreationManager.isLoadingAllowedGroups.collectAsState()
    val allowedGroups by GroupCreationManager.allowedGroups.collectAsState()
    val groupReasons by GroupCreationManager.groupReasons.collectAsState()

    val hasFetchedAllowed by GroupCreationManager.hasFetchedAllowed.collectAsState()

    val (allowedEntries, lockedEntries) = remember(allowedGroups, hasFetchedAllowed) {
        groupTypeEntries.partition { GroupCreationManager.isGroupTypeAllowed(it.type) }
    }

    var lockedDialogInfo by remember { mutableStateOf<Pair<SavingsGroupType, String>?>(null) }
    var isBannerDismissed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showVerifyScreen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        GroupCreationManager.fetchAllowedGroups()
    }

    if (showVerifyScreen) {
        IdentityVerificationScreen(
            onBack = { showVerifyScreen = false },
            onVerificationSuccess = {
                showVerifyScreen = false
                coroutineScope.launch {
                    GroupCreationManager.fetchAllowedGroups(forceRefresh = true)
                }
            }
        )
        return
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            SelectGroupTypeTopBar(
                onBack = onBack,
                isLoading = isLoading
            )
        }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Allowed Group Archetypes (Active Options) ──
            if (allowedEntries.isNotEmpty()) {
                items(allowedEntries, key = { it.type.name }) { entry ->
                    GroupTypeRow(
                        icon = entry.icon,
                        title = entry.type.uiLabel,
                        subtitle = entry.subtitle,
                        isLocked = false,
                        onClick = { onGroupTypeSelected(entry.type) }
                    )
                }
            } else if (!isLoading && !isBannerDismissed) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        InfoWidgetBanner(
                            title = "Identity verification required",
                            description = "Complete KYC Tier 2 verification to unlock group archetypes and start pooling funds.",
                            linkText = "Learn more",
                            buttonText = "Verify",
                            isButtonOnSide = true,
                            leadingIcon = Icons.Default.Lock,
                            onButtonClick = {
                                if (onVerifyKycClick != null) {
                                    onVerifyKycClick()
                                } else {
                                    showVerifyScreen = true
                                }
                            },
                            onLinkClick = {
                                lockedDialogInfo = (SavingsGroupType.GOAL_SAVINGS to "Microbond uses progressive KYC tiers to protect pooled community funds. Tier 2 requires a verified National ID.")
                            },
                            onDismiss = {
                                isBannerDismissed = true
                            }
                        )
                    }
                }
            }

            // ── Locked / Unavailable Groups (Compact Name List) ──
            if (lockedEntries.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "UNAVAILABLE",
                        color = MicrobondInactiveGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                items(lockedEntries, key = { it.type.name }) { entry ->
                    val reason = GroupCreationManager.getDisallowedReason(entry.type)
                    UnavailableGroupRow(
                        icon = entry.icon,
                        title = entry.type.uiLabel,
                        onClick = {
                            lockedDialogInfo = entry.type to reason
                        }
                    )
                }
            }
        }

        // Informative Unlock Requirements Dialog when tapping a locked group
        lockedDialogInfo?.let { (type, reason) ->
            AlertDialog(
                onDismissRequest = { lockedDialogInfo = null },
                containerColor = MicrobondBottomSheetDark,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Archetype",
                        tint = MicrobondWarningAmber,
                        modifier = Modifier.size(28.dp)
                    )
                },
                title = {
                    Text(
                        text = "${type.uiLabel} Locked",
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = reason,
                            color = MicrobondInactiveGray,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Complete your identity verification (KYC) or maintain high communal health in active circles to unlock this group archetype.",
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { lockedDialogInfo = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = MicrobondActiveGreen)
                    ) {
                        Text("Understood", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    }
}

// ── Compact Unavailable Group Row ───────────────────────────────────────────
@Composable
private fun UnavailableGroupRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = MicrobondLockBadgeBg,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MicrobondLockIconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                color = MicrobondInactiveGray,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = MicrobondLockIconColor,
                modifier = Modifier.size(16.dp)
            )
        }
        if (showDivider) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 72.dp)
                    .height(0.5.dp)
                    .background(MicrobondDividerDark)
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────
@Composable
private fun SelectGroupTypeTopBar(
    onBack: () -> Unit,
    isLoading: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Select group type",
                    color = MicrobondTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MicrobondActiveGreen,
                trackColor = MicrobondDarkBackground
            )
        } else {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MicrobondDividerDark)
            )
        }
    }
}

// ── Group type metadata ───────────────────────────────────────────────────────
private data class GroupTypeEntry(
    val type: SavingsGroupType,
    val icon: ImageVector,
    val subtitle: String
)

private val groupTypeEntries = listOf(
    GroupTypeEntry(SavingsGroupType.MERRY_GO_ROUND,   Icons.Default.Diversity3,        "Rotating savings with scheduled payouts"),
    GroupTypeEntry(SavingsGroupType.GOAL_SAVINGS,      Icons.Default.Savings,           "Save together toward a shared goal"),
    GroupTypeEntry(SavingsGroupType.WELFARE_FUND,      Icons.Default.VolunteerActivism, "Emergency and welfare support fund"),
    GroupTypeEntry(SavingsGroupType.EVENT_COMMITTEE,   Icons.Default.CardGiftcard,      "Pool funds for events and celebrations"),
    GroupTypeEntry(SavingsGroupType.SHARED_WALLET,     Icons.Default.Handshake,         "Shared spending wallet for a household"),
    GroupTypeEntry(SavingsGroupType.BILL_SPLIT,        Icons.Default.LocalAtm,          "Split household and utility bills fairly"),
    GroupTypeEntry(SavingsGroupType.INVESTMENT_CLUB,   Icons.Default.AccountBalance,    "Invest together and share returns"),
    GroupTypeEntry(SavingsGroupType.FUNDRAISER,        Icons.Default.Campaign,          "Run a public fundraising campaign"),
    GroupTypeEntry(SavingsGroupType.ASSOCIATION_DUES,  Icons.Default.Groups,            "Collect membership dues and manage arrears")
)

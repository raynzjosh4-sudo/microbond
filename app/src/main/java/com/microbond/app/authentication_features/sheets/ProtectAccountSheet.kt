package com.microbond.app.authentication_features.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SyncLock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondAttachmentSheetDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondDragHandleDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondCheckMarkBlue

/**
 * Account Protection Sheet presented after profile picture and name setup.
 *
 * In the backend, this PIN is known as the "App PIN" (bcrypt-hashed step-up auth).
 * In the frontend, it is presented as a "Security PIN" to make its purpose intuitive:
 * it specifically guards money movements, payout destination changes, device recovery,
 * and high-risk operations, distinct from any local screen/device lock.
 *
 * Shows essential hints on the primary view and provides an interactive "Learn more"
 * section explaining in-depth how the Security PIN shields the user's funds.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectAccountSheet(
    onDismissRequest: () -> Unit,
    onCreatePinClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSkipClick: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLearnMore by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MicrobondAttachmentSheetDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MicrobondDragHandleDark)
                )
            }
        },
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = showLearnMore,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "SheetContentTransition"
        ) { isLearnMore ->
            if (isLearnMore) {
                SecurityPinLearnMoreContent(
                    onBackClick = { showLearnMore = false },
                    onCreatePinClick = onCreatePinClick
                )
            } else {
                SecurityPinOverviewContent(
                    onLearnMoreClick = { showLearnMore = true },
                    onCreatePinClick = onCreatePinClick,
                    onSkipClick = onSkipClick
                )
            }
        }
    }
}

/**
 * Primary overview view showing key protection hints and the "Create PIN" CTA.
 */
@Composable
private fun SecurityPinOverviewContent(
    onLearnMoreClick: () -> Unit,
    onCreatePinClick: () -> Unit,
    onSkipClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Shield Icon Header with glowing accent ring
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(MicrobondActiveGreen.copy(alpha = 0.12f))
                .border(1.5.dp, MicrobondActiveGreen.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = "Account Protection",
                tint = MicrobondActiveGreen,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Title
        Text(
            text = "Protect your account",
            style = TextStyle(
                color = MicrobondTextPrimary,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle explaining financial nature of the app
        Text(
            text = "Because Microbond holds your money, savings groups, and wallet balances, set up a Security PIN to keep your funds safe from unauthorized access.",
            style = TextStyle(
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Hint Cards: Highlights key use-cases without overwhelming the user
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MicrobondDarkBackground)
                .padding(vertical = 12.dp, horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PinUsageHintRow(
                icon = Icons.Outlined.Payment,
                title = "Authorizes withdrawals & payouts",
                description = "Required to confirm money transfers and link payout phone numbers."
            )

            HorizontalDivider(color = MicrobondDividerDark, thickness = 0.8.dp)

            PinUsageHintRow(
                icon = Icons.Outlined.PhoneAndroid,
                title = "Defends against device theft",
                description = "Even if your phone is unlocked, money cannot be moved without your PIN."
            )

            HorizontalDivider(color = MicrobondDividerDark, thickness = 0.8.dp)

            PinUsageHintRow(
                icon = Icons.Outlined.SyncLock,
                title = "Fast account recovery",
                description = "Allows instant verification when moving your account to a new phone."
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Learn more" banner / action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onLearnMoreClick
                )
                .padding(vertical = 8.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = microBondCheckMarkBlue,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = "Learn how your Security PIN is used",
                style = TextStyle(
                    color = microBondCheckMarkBlue,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = microBondCheckMarkBlue,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Primary Action: "Create PIN"
        ReusableMicroButtonBig(
            text = "Create PIN",
            onClick = onCreatePinClick,
            modifier = Modifier.fillMaxWidth()
        )

        // Optional secondary dismiss/skip
        if (onSkipClick != null) {
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(
                onClick = onSkipClick,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Text(
                    text = "Set up later",
                    style = TextStyle(
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        } else {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

/**
 * Detailed view shown when the user taps "Learn more".
 * Lays out the comprehensive architecture of the Security PIN.
 */
@Composable
private fun SecurityPinLearnMoreContent(
    onBackClick: () -> Unit,
    onCreatePinClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp)
    ) {
        // Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to summary",
                    tint = MicrobondTextPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "About your Security PIN",
                style = TextStyle(
                    color = MicrobondTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Microbond uses a multi-layered security model. Your Security PIN acts as your digital signature for financial and account-altering transactions.",
                style = TextStyle(
                    color = MicrobondInactiveGray,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            )

            DetailedInfoCard(
                icon = Icons.Outlined.AccountBalanceWallet,
                title = "Wallet Withdrawals & Transfers",
                description = "Every time you cash out your savings or initiate a withdrawal, your PIN is verified directly by the secure backend to ensure you authorized the transaction."
            )

            DetailedInfoCard(
                icon = Icons.Outlined.Payment,
                title = "Payout Destinations & Payment Methods",
                description = "Adding a mobile money destination or binding a payout phone number requires PIN authorization. This prevents cycle disbursements from being redirected."
            )

            DetailedInfoCard(
                icon = Icons.Outlined.Shield,
                title = "Protection Against Physical Device Theft",
                description = "Browsing and viewing your groups is quick and seamless. But if an unlocked phone falls into the wrong hands, funds cannot be stolen without this secret PIN."
            )

            DetailedInfoCard(
                icon = Icons.Outlined.SyncLock,
                title = "Zero-Cost Account Recovery",
                description = "When switching to a new device, your PIN acts as cryptographic proof of ownership, restoring your identity instantly without expensive SMS OTP charges."
            )

            DetailedInfoCard(
                icon = Icons.Outlined.Lock,
                title = "Screen Lock vs. Security PIN",
                description = "Your device fingerprint or screen lock controls access to the phone screen. Your Security PIN is your private authorization for real financial commitments."
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create PIN CTA on Learn More view
        ReusableMicroButtonBig(
            text = "Create PIN",
            onClick = onCreatePinClick,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        )
    }
}

/**
 * Single hint row inside the overview card.
 */
@Composable
private fun PinUsageHintRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MicrobondAttachmentSheetDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MicrobondActiveGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    color = MicrobondTextPrimary,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = TextStyle(
                    color = MicrobondInactiveGray,
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                )
            )
        }
    }
}

/**
 * Single card inside the detailed "Learn more" view.
 */
@Composable
private fun DetailedInfoCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MicrobondDarkBackground)
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MicrobondActiveGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MicrobondActiveGreen,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    color = MicrobondTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = TextStyle(
                    color = MicrobondInactiveGray,
                    fontSize = 12.5.sp,
                    lineHeight = 17.5.sp
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun ProtectAccountSheetOverviewPreview() {
    ProtectAccountSheet(
        onDismissRequest = {},
        onCreatePinClick = {},
        onSkipClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun SecurityPinLearnMoreContentPreview() {
    SecurityPinLearnMoreContent(
        onBackClick = {},
        onCreatePinClick = {}
    )
}

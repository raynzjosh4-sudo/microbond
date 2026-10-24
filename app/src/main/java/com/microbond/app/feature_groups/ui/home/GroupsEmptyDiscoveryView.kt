package com.microbond.app.feature_groups.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.contactgroupdiscovery.InstagramStyleContactGroupItem
import com.microbond.app.core.components.inforwidgetbanner.InfoWidgetBanner
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondInfoBannerBorder
import com.microbond.app.core.ui.theme.MicrobondInfoBannerContainer
import com.microbond.app.core.ui.theme.MicrobondInfoBannerIconBg
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondTextPrimaryLight

/**
 * Empty discovery view presented when a user has no active joined groups.
 *
 * Screen-Level UI:
 * - If there are pending join requests: Pins "PENDING REQUESTS (n)" at the top,
 *   with the remaining discovered groups below under "MORE GROUPS WITH YOUR CONTACTS".
 * - If no pending join requests: Shows the promotional Welcome Banner, headline,
 *   and social proof discovery carousel.
 *
 * Item-Level UI:
 * - Each card tracks its own independent join state (IDLE, LOADING, PENDING_APPROVAL,
 *   PENDING_GUARANTOR, JOINED) without freezing or locking the rest of the feed.
 */
@Composable
fun GroupsEmptyDiscoveryView(
    discoveredContactGroups: List<DiscoverableContactGroup>,
    groupJoinStates: Map<String, JoinState> = emptyMap(),
    isLoading: Boolean = false,
    showFab: Boolean = false,
    onGroupClick: (DiscoverableContactGroup) -> Unit,
    onCancelPendingRequest: (DiscoverableContactGroup) -> Unit = {},
    onStatusBadgeClick: (DiscoverableContactGroup, JoinState) -> Unit = { _, _ -> },
    onExploreAllClick: () -> Unit = {},
    onCreateGroupClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showWelcomeBanner by remember { mutableStateOf(true) }

    // Separate groups that are actively pending approval or guarantor vouching
    val pendingGroups = discoveredContactGroups.filter { group ->
        val state = groupJoinStates[group.id]
        state is JoinState.PENDING_APPROVAL || state is JoinState.PENDING_GUARANTOR
    }

    val remainingDiscoverableGroups = if (pendingGroups.isNotEmpty()) {
        discoveredContactGroups.filter { group ->
            val state = groupJoinStates[group.id]
            state !is JoinState.PENDING_APPROVAL && state !is JoinState.PENDING_GUARANTOR
        }
    } else {
        discoveredContactGroups
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // ── Top Section: PENDING REQUESTS (If any request is pending) ──
        if (pendingGroups.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PENDING REQUESTS (${pendingGroups.size})",
                        color = Color(0xFFFBBF24),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                pendingGroups.forEach { group ->
                    val joinState = groupJoinStates[group.id] ?: JoinState.PENDING_APPROVAL
                    PendingGroupRequestCard(
                        group = group,
                        joinState = joinState,
                        onCancel = { onCancelPendingRequest(group) },
                        onClick = { onStatusBadgeClick(group, joinState) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Promotional InfoWidgetBanner (When no pending requests and enabled) ──
        if (pendingGroups.isEmpty() && showWelcomeBanner) {
            InfoWidgetBanner(
                title = "Welcome to Microbond",
                description = "Save with verified contacts in trusted rotating circles.",
                leadingIcon = Icons.Default.Group,
                buttonText = "Learn more",
                onButtonClick = onExploreAllClick,
                showDismissButton = true,
                onDismiss = { showWelcomeBanner = false },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Headline & Subtitle ──
        val headerTitle = if (pendingGroups.isNotEmpty()) {
            "MORE GROUPS WITH YOUR CONTACTS"
        } else {
            "Start saving together"
        }

        val isPendingHeader = pendingGroups.isNotEmpty()

        Text(
            text = headerTitle,
            color = if (isPendingHeader) MicrobondActiveGreen else MicrobondTextPrimary,
            fontSize = if (isPendingHeader) 13.sp else 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = if (isPendingHeader) 1.sp else 0.sp,
            textAlign = if (isPendingHeader) TextAlign.Start else TextAlign.Center,
            modifier = if (isPendingHeader) {
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            } else {
                Modifier
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!isPendingHeader) {
            val subtitle = if (discoveredContactGroups.isNotEmpty()) {
                "Join circles where your contacts are members, or start your own savings pool."
            } else {
                "Save in trusted rotating circles with friends and family."
            }

            Text(
                text = subtitle,
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 36.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))
        }

        // ── 1. Horizontal Carousel (Instagram Style Circles) ──
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MicrobondActiveGreen,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        } else if (remainingDiscoverableGroups.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = remainingDiscoverableGroups,
                    key = { it.id.ifBlank { it.name } }
                ) { group ->
                    InstagramStyleContactGroupItem(
                        group = group,
                        onClick = onGroupClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Outlined Pill Button ("Explore all groups") ──
        OutlinedButton(
            onClick = onExploreAllClick,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MicrobondInfoBannerBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MicrobondActiveGreen
            ),
            modifier = Modifier.height(42.dp)
        ) {
            Text(
                text = "Explore all groups",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (showFab) {
            Spacer(modifier = Modifier.height(24.dp))
            ExtendedFloatingActionButton(
                onClick = onCreateGroupClick,
                containerColor = MicrobondActiveGreen,
                contentColor = microBondTextPrimaryLight,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New group", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


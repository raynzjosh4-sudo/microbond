package com.microbond.app.core.components.groupintrocard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.groupIconImage.widgets.MicrobondGroupImageAvatar
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondAmberNotice
import com.microbond.app.core.ui.theme.MicrobondCardOutlineDark
import com.microbond.app.core.ui.theme.MicrobondCardSurfaceDark
import com.microbond.app.core.ui.theme.MicrobondGroupIconBgDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Clean, compact Microbond Group Introduction Card.
 *
 * Supports two distinct operational states:
 * 1. Non-member: Displays a single primary [Join Group] action button.
 * 2. Member: Displays [+ Add members] (Admin) and [Invite via link or QR code] actions,
 *    along with editable description handling.
 */
@Composable
fun GroupIntroCard(
    name: String,
    memberCountText: String,
    createdDateText: String? = null,
    description: String? = null,
    iconUri: String? = null,
    isMember: Boolean = false,
    canAddMembers: Boolean = false,
    canInviteViaLink: Boolean = false,
    canEditDescription: Boolean = false,
    canEditSettings: Boolean = false,
    modifier: Modifier = Modifier,
    joinButtonText: String = "Join Group",
    isJoinEnabled: Boolean = true,
    isJoinLoading: Boolean = false,
    onAvatarClick: () -> Unit = {},
    onJoinClick: () -> Unit = {},
    onAddMembersClick: () -> Unit = {},
    onInviteLinkClick: () -> Unit = {},
    onAddDescriptionClick: () -> Unit = {},
    groupSpecificContent: @Composable ColumnScope.() -> Unit = {}
) {
    Surface(
        color = MicrobondCardSurfaceDark,
        shape = RoundedCornerShape(16.dp),
        border = null,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 14.dp)
        ) {
            // Group Avatar with Camera Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clickable { onAvatarClick() }
            ) {
                if (!iconUri.isNullOrBlank()) {
                    MicrobondGroupImageAvatar(
                        avatarSize = 52.dp,
                        imageUri = iconUri
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MicrobondGroupIconBgDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Group Icon",
                            tint = Color(0xFF5E8B80),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Camera Badge Overlay (Bottom-Right) - only visible if member has permission to edit settings
                if (isMember && canEditSettings) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(MicrobondCardSurfaceDark)
                            .padding(1.5.dp)
                            .clip(CircleShape)
                            .background(MicrobondActiveGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            tint = Color(0xFF0B141A),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Group Name / Title
            Text(
                text = name,
                color = MicrobondTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Subtitle: Members / Count
            Text(
                text = memberCountText,
                color = MicrobondInactiveGray,
                fontSize = 12.5.sp,
                textAlign = TextAlign.Center
            )

            if (!createdDateText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = createdDateText,
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Description or Add description button
            Spacer(modifier = Modifier.height(6.dp))
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    color = MicrobondActiveGreen,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable(enabled = isMember) { onAddDescriptionClick() }
                )
            } else if (isMember) {
                Text(
                    text = "Add description...",
                    color = MicrobondActiveGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onAddDescriptionClick() }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            // Group-specific Content Slot (e.g. ROSCA details)
            groupSpecificContent()

            // ── Dynamic Action Buttons ──
            Spacer(modifier = Modifier.height(12.dp))

            if (!isMember) {
                // Non-member: Primary Join Group Button
                if (isJoinLoading) {
                    Surface(
                        color = MicrobondActiveGreen.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    ReusableMicroButtonBig(
                        text = joinButtonText,
                        onClick = onJoinClick,
                        enabled = isJoinEnabled
                    )
                }
            } else {
                // Active Member: Actions matching permissions
                if (canAddMembers || canInviteViaLink) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Button 1: [+ Add members] (Visible for ADMIN)
                        if (canAddMembers) {
                            Surface(
                                onClick = onAddMembersClick,
                                color = Color.Transparent,
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, MicrobondCardOutlineDark),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = "Add members",
                                        tint = MicrobondActiveGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Add members",
                                        color = MicrobondActiveGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Button 2: [🔗 Invite via link or QR code] (Visible if ADMIN or members_can_invite_via_link)
                        if (canInviteViaLink) {
                            Surface(
                                onClick = onInviteLinkClick,
                                color = Color.Transparent,
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, MicrobondCardOutlineDark),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = "Invite via link",
                                        tint = MicrobondActiveGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Invite via link or QR code",
                                        color = MicrobondActiveGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Microbond end-to-end encryption notice pill:
 * Non-clickable, centered text with lock icon in amber `#FED37E`, dark background.
 */
@Composable
fun GroupEncryptionNoticePill(
    modifier: Modifier = Modifier,
    noticeText: String = "Messages and calls in this chat are end-to-end encrypted. Nobody outside this circle, not even Microbond, can read or listen to them. Learn more"
) {
    Surface(
        color = MicrobondCardSurfaceDark,
        shape = RoundedCornerShape(8.dp),
        border = null,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Encrypted",
                tint = MicrobondAmberNotice,
                modifier = Modifier
                    .size(13.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = noticeText,
                color = MicrobondAmberNotice,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Centered Date Pill header (e.g. "Monday", "October 11, 2026").
 */
@Composable
fun GroupDatePill(
    dateText: String = "Today",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MicrobondCardSurfaceDark,
            shape = RoundedCornerShape(8.dp),
            border = null
        ) {
            Text(
                text = dateText,
                color = MicrobondInactiveGray,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

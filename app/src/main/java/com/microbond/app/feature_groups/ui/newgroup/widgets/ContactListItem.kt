package com.microbond.app.feature_groups.ui.newgroup.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_groups.ui.newgroup.ContactEntry

/**
 * A single contact row that supports:
 * - Selection tick (for contacts on Microbond AND off-app contacts for UI testing)
 * - "Invite" text button for contacts not on Microbond
 * - Subtle bottom divider
 */
@Composable
fun ContactListItem(
    contact: ContactEntry,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with optional green check overlay
            Box(contentAlignment = Alignment.BottomEnd) {
                MicroBondAvatar(
                    painter = null,
                    name = contact.name,
                    modifier = Modifier.size(48.dp),
                    backgroundColor = contact.avatarColor
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(MicrobondActiveGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                if (contact.subtitle.isNotBlank()) {
                    Text(
                        text = contact.subtitle,
                        color = MicrobondInactiveGray,
                        fontSize = 13.sp
                    )
                }
            }

            // Invite button for non-Microbond contacts (selectable for UI testing)
            if (!contact.isOnMicrobond) {
                TextButton(onClick = onToggle) {
                    Text(
                        text = if (isSelected) "Invited ✓" else "Invite",
                        color = if (isSelected) MicrobondActiveGreen else MicrobondActiveGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        if (showDivider) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 78.dp)
                    .height(0.5.dp)
                    .background(MicrobondDividerDark)
            )
        }
    }
}

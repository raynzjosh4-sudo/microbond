package com.microbond.app.core.components.userlisttile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * WhatsApp-style contact / member list tile.
 *
 * Reuses the same [MicroBondAvatar] that is used in chat bubbles —
 * shows a circular photo or a coloured initial letter.
 *
 * Layout: [avatar] | [name + subtitle] | [trailing slot]
 *
 * @param name          Primary contact name (bold, white)
 * @param subtitle      Secondary line — phone number, last seen, role, etc.
 * @param painter       Optional real photo for the avatar
 * @param avatarColor   Fallback avatar background colour (used when no [painter])
 * @param avatarSize    Size of the circle avatar (default 50.dp — WhatsApp list size)
 * @param trailingContent Optional composable placed at the far-right (checkbox, tick, etc.)
 * @param onClick       Optional click handler for the whole row
 * @param showDivider   Draw a hairline divider below the tile (default false)
 */
@Composable
fun MicrobondUserListTile(
    name: String,
    subtitle: String? = null,
    painter: Painter? = null,
    avatarColor: Color = Color(0xFF247A70),
    avatarSize: Dp = 50.dp,
    avatarFallbackContent: (@Composable () -> Unit)? = null,
    avatarBadge: (@Composable () -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Avatar ────────────────────────────────────────────────────────────
        Box(modifier = Modifier.size(avatarSize)) {
            MicroBondAvatar(
                painter = painter,
                name = name,
                modifier = Modifier.fillMaxSize(),
                backgroundColor = avatarColor,
                fallbackContent = avatarFallbackContent
            )
            avatarBadge?.let { badge ->
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    badge()
                }
            }
        }

        Spacer(Modifier.width(14.dp))

        // ── Name + subtitle ───────────────────────────────────────────────────
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = name,
                color = MicrobondTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MicrobondInactiveGray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ── Trailing slot ─────────────────────────────────────────────────────
        if (trailingContent != null) {
            Spacer(Modifier.width(8.dp))
            trailingContent()
        }
    }
}

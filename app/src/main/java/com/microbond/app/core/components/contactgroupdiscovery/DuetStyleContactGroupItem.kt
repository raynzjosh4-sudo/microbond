package com.microbond.app.core.components.contactgroupdiscovery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.ContactMember
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondChipNonSelectedColor
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondInfoBannerIconBg
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Pattern 3: Interlocking Dual-Circle (50/50 Duet Style).
 *
 * Visual Hierarchy:
 * - Two interlocking 46dp circles side-by-side (76dp width total).
 * - Left: Friend avatar.
 * - Right: Group avatar overlapping with cutout border.
 * - "$contactName in" subtitle and bold group title underneath.
 */
@Composable
fun DuetStyleContactGroupItem(
    group: DiscoverableContactGroup,
    onClick: (DiscoverableContactGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    val topContact = group.contactMembers.firstOrNull()
    val contactName = topContact?.profileName ?: "Contact"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(96.dp)
            .clickable { onClick(group) }
            .padding(horizontal = 4.dp)
    ) {
        // Interlocking 50/50 Avatars
        Box(
            modifier = Modifier
                .width(76.dp)
                .height(48.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Left: Friend Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MicrobondChipNonSelectedColor),
                contentAlignment = Alignment.Center
            ) {
                AsyncAvatarImage(
                    imageUrl = topContact?.profilePictureUrl,
                    contentDescription = contactName,
                    fallbackIcon = Icons.Default.Person,
                    fallbackTint = MicrobondInactiveGray,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Right: Group Avatar overlapping with 2dp border cutout
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .align(Alignment.CenterEnd)
                    .clip(CircleShape)
                    .background(MicrobondInfoBannerIconBg)
                    .border(2.dp, MicrobondDarkBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncAvatarImage(
                    imageUrl = group.groupImageUrl,
                    contentDescription = group.name,
                    fallbackIcon = Icons.Default.Group,
                    fallbackTint = MicrobondActiveGreen,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "$contactName in",
            color = MicrobondInactiveGray,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = group.name,
            color = MicrobondTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun DuetStyleContactGroupItemPreview() {
    DuetStyleContactGroupItem(
        group = DiscoverableContactGroup(
            id = "3",
            name = "Tech Savers",
            contactMembers = listOf(ContactMember(profileName = "Simon"))
        ),
        onClick = {}
    )
}

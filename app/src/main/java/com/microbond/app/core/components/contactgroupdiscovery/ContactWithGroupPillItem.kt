package com.microbond.app.core.components.contactgroupdiscovery

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondInfoBannerBorder
import com.microbond.app.core.ui.theme.MicrobondInfoBannerIconBg
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Pattern 2: Contact Avatar + Mini Group Pill Row Underneath.
 *
 * Visual Hierarchy:
 * - 100% full-sized contact avatar (56dp).
 * - Contact friend name.
 * - Compact pill tag containing [👥 Group Name] with subtle emerald border.
 */
@Composable
fun ContactWithGroupPillItem(
    group: DiscoverableContactGroup,
    onClick: (DiscoverableContactGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    val topContact = group.contactMembers.firstOrNull()
    val contactName = topContact?.profileName ?: "Contact"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(92.dp)
            .clickable { onClick(group) }
            .padding(horizontal = 4.dp)
    ) {
        // Standard WhatsApp Circular Contact Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
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

        Spacer(modifier = Modifier.height(6.dp))

        // Contact Name
        Text(
            text = contactName,
            color = MicrobondTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(3.dp))

        // Small Group Pill Row: [👥 Group Name]
        Surface(
            color = MicrobondInfoBannerIconBg,
            shape = CircleShape,
            border = BorderStroke(0.5.dp, MicrobondInfoBannerBorder)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = MicrobondActiveGreen,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = group.name,
                    color = MicrobondActiveGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun ContactWithGroupPillItemPreview() {
    ContactWithGroupPillItem(
        group = DiscoverableContactGroup(
            id = "2",
            name = "Tech Savers",
            contactMembers = listOf(ContactMember(profileName = "Simon H."))
        ),
        onClick = {}
    )
}

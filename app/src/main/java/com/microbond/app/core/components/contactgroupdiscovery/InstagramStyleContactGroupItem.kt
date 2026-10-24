package com.microbond.app.core.components.contactgroupdiscovery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.ContactMember
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

private data class GroupDiscoveryTheme(
    val backgroundColor: Color,
    val iconColor: Color,
    val icon: ImageVector
)

private val memberAvatarColors = listOf(
    Color(0xFFE07A5F),
    Color(0xFF3D5A80),
    Color(0xFF2A9D8F),
    Color(0xFFE76F51),
    Color(0xFF8338EC),
    Color(0xFF3A86FF),
    Color(0xFF06D6A0),
    Color(0xFFF4A261)
)

private fun getGroupDiscoveryTheme(group: DiscoverableContactGroup): GroupDiscoveryTheme {
    val nameLower = group.name.lowercase()
    val typeLower = group.type.lowercase()

    return when {
        nameLower.contains("creator") || typeLower.contains("rotating") || typeLower.contains("rosca") -> GroupDiscoveryTheme(
            backgroundColor = Color(0xFF1B382B),
            iconColor = Color(0xFF00C896),
            icon = Icons.Filled.Groups
        )
        nameLower.contains("builder") || nameLower.contains("business") || typeLower.contains("investment") -> GroupDiscoveryTheme(
            backgroundColor = Color(0xFF2C2244),
            iconColor = Color(0xFFA78BFA),
            icon = Icons.Filled.TrendingUp
        )
        nameLower.contains("tech") || nameLower.contains("code") || typeLower.contains("target") -> GroupDiscoveryTheme(
            backgroundColor = Color(0xFF18363D),
            iconColor = Color(0xFF2DD4BF),
            icon = Icons.Filled.Code
        )
        nameLower.contains("connect") || nameLower.contains("weekend") || typeLower.contains("welfare") || typeLower.contains("emergency") -> GroupDiscoveryTheme(
            backgroundColor = Color(0xFF3B2A1C),
            iconColor = Color(0xFFFBBF24),
            icon = Icons.Filled.Landscape
        )
        else -> {
            val palette = listOf(
                GroupDiscoveryTheme(Color(0xFF1B382B), Color(0xFF00C896), Icons.Filled.Groups),
                GroupDiscoveryTheme(Color(0xFF2C2244), Color(0xFFA78BFA), Icons.Filled.TrendingUp),
                GroupDiscoveryTheme(Color(0xFF18363D), Color(0xFF2DD4BF), Icons.Filled.Code),
                GroupDiscoveryTheme(Color(0xFF3B2A1C), Color(0xFFFBBF24), Icons.Filled.Landscape)
            )
            val hash = kotlin.math.abs(group.name.hashCode())
            palette[hash % palette.size]
        }
    }
}

/**
 * Groups Your Contacts Are In item:
 * 1. Prominent themed circular illustration / icon banner (76dp).
 * 2. Horizontally stacked overlapping circular face avatars of member contacts across the bottom.
 * 3. Bold group title.
 * 4. "Members: ContactName1, ContactName2..." subtitle.
 */
@Composable
fun InstagramStyleContactGroupItem(
    group: DiscoverableContactGroup,
    onClick: (DiscoverableContactGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = getGroupDiscoveryTheme(group)
    val displayMembers = group.contactMembers.take(3)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(106.dp)
            .clickable { onClick(group) }
            .padding(horizontal = 3.dp)
    ) {
        // ── 1. Illustration Circle with Overlapping Member Avatars ──
        Box(
            modifier = Modifier.size(width = 86.dp, height = 94.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Main Themed Circle (76dp)
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(theme.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                if (!group.groupImageUrl.isNullOrBlank()) {
                    AsyncAvatarImage(
                        imageUrl = group.groupImageUrl,
                        contentDescription = group.name,
                        fallbackIcon = theme.icon,
                        fallbackTint = theme.iconColor,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = theme.icon,
                        contentDescription = group.name,
                        tint = theme.iconColor,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            // Overlapping Member Avatars Row overlapping the bottom of the circle
            if (displayMembers.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(28.dp),
                    horizontalArrangement = Arrangement.spacedBy((-8).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                displayMembers.forEachIndexed { index, member ->
                    val colorIndex = kotlin.math.abs((member.phoneNumber ?: member.profileName).hashCode() + index * 31)
                    val bgColor = memberAvatarColors[colorIndex % memberAvatarColors.size]

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .border(2.dp, MicrobondDarkBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!member.profilePictureUrl.isNullOrBlank()) {
                            AsyncAvatarImage(
                                imageUrl = member.profilePictureUrl,
                                contentDescription = member.profileName,
                                fallbackIcon = Icons.Default.Person,
                                fallbackTint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            val initial = member.profileName.trim().take(1).uppercase()
                            if (initial.isNotBlank()) {
                                Text(
                                    text = initial,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

        // ── 2. Group Name ──
        Text(
            text = group.name,
            color = MicrobondTextPrimary,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 17.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── 3. Members Subtitle ──
        val contactNames = group.contactMembers.mapNotNull { it.profileName.ifBlank { null } }
        val membersText = when {
            contactNames.isNotEmpty() -> "Members:\n${contactNames.take(3).joinToString(", ")}"
            group.mutualContactsCount > 1 -> "Members:\n${group.mutualContactsCount} contacts"
            group.mutualContactsCount == 1 -> "Members:\n1 contact"
            else -> "${group.memberCount} members"
        }

        Text(
            text = membersText,
            color = MicrobondInactiveGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.5.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Semantic alias
@Composable
fun GroupsWithContactsItem(
    group: DiscoverableContactGroup,
    onClick: (DiscoverableContactGroup) -> Unit,
    modifier: Modifier = Modifier
) = InstagramStyleContactGroupItem(group, onClick, modifier)

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun InstagramStyleContactGroupItemPreview() {
    InstagramStyleContactGroupItem(
        group = DiscoverableContactGroup(
            id = "1",
            name = "Microbond Creators",
            mutualContactsCount = 3,
            contactMembers = listOf(
                ContactMember(id = "1", profileName = "Asha"),
                ContactMember(id = "2", profileName = "Brian"),
                ContactMember(id = "3", profileName = "Moses")
            )
        ),
        onClick = {}
    )
}

package com.microbond.app.feature_settings.groupsetttingscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.network.api.goupapis.GroupMemberItemDto
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.GroupMemberRole

/**
 * WhatsApp-style Group Members roster section with real member counts,
 * inline search filtering, "Add members" button, and admin badges.
 */
@Composable
fun GroupMembersSection(
    displayMemberCount: Int,
    isSearchingMembers: Boolean,
    onToggleSearch: () -> Unit,
    memberSearchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onAddMemberClick: () -> Unit,
    viewerRole: GroupMemberRole,
    membersList: List<GroupMemberItemDto>,
    canInviteViaLink: Boolean = false,
    onViewMemberChanges: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Header: "X members" and Search button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$displayMemberCount members",
                color = MicrobondInactiveGray,
                fontSize = 14.sp
            )
            IconButton(onClick = onToggleSearch) {
                Icon(
                    imageVector = if (isSearchingMembers) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "Search members",
                    tint = MicrobondInactiveGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Inline Search bar if active
        if (isSearchingMembers) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF182229))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MicrobondInactiveGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = memberSearchQuery,
                        onValueChange = onSearchQueryChange,
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        cursorBrush = SolidColor(MicrobondActiveGreen),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (memberSearchQuery.isEmpty()) {
                                Text(
                                    text = "Search members...",
                                    color = MicrobondInactiveGray,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (memberSearchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = onClearSearch,
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MicrobondInactiveGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        val currentUid = AuthSessionManager.currentUserId
        val myMember = membersList.firstOrNull {
            (!currentUid.isNullOrBlank() && it.userId.equals(currentUid, ignoreCase = true)) ||
            it.userId.equals("me", ignoreCase = true) ||
            it.fullName.equals("You", ignoreCase = true)
        }
        val isCurrentUserAdmin = when {
            myMember != null -> myMember.role.equals("ADMIN", ignoreCase = true)
            else -> viewerRole.isAdmin
        }
        val myInitial = myMember?.fullName?.take(1)?.uppercase() ?: "Y"

        // "Add members" / "Invite to group" Action Item (Only shown if admin or canInviteViaLink)
        if (isCurrentUserAdmin || canInviteViaLink) {
            val actionLabel = if (isCurrentUserAdmin) "Add members" else "Invite via link"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddMemberClick)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MicrobondActiveGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCurrentUserAdmin) Icons.Default.PersonAddAlt else Icons.Default.Link,
                        contentDescription = actionLabel,
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = actionLabel,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Current User ("You") Item
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B3B34)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = myInitial,
                    color = MicrobondActiveGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "You",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Add member tag",
                    color = MicrobondActiveGreen,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { /* Tag */ }
                )
            }
            // "Group Admin" Pill Badge (only if current user is actually an admin)
            if (isCurrentUserAdmin) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F3B2E))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Group Admin",
                        color = MicrobondActiveGreen,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Live / Real Group Members
        val filteredMembers = if (memberSearchQuery.isBlank()) {
            membersList
        } else {
            membersList.filter {
                it.fullName.contains(memberSearchQuery, ignoreCase = true) ||
                it.phoneNumber.contains(memberSearchQuery)
            }
        }

        if (filteredMembers.isNotEmpty()) {
            filteredMembers.forEach { member ->
                val isCurrentUser = (!currentUid.isNullOrBlank() && member.userId.equals(currentUid, ignoreCase = true)) ||
                        member.userId.equals("me", ignoreCase = true) ||
                        member.fullName.equals("You", ignoreCase = true)
                if (!isCurrentUser) {
                    MemberRosterItem(
                        name = member.fullName.ifBlank { member.phoneNumber.ifBlank { "Member" } },
                        subtitle = if (member.phoneNumber.isNotBlank() && member.phoneNumber != member.fullName)
                            member.phoneNumber
                        else
                            member.role,
                        avatarUrl = member.profilePictureUrl,
                        isAdmin = member.role.equals("ADMIN", ignoreCase = true)
                    )
                }
            }
        } else if (membersList.isEmpty()) {
            // Fallback member item matching WhatsApp display
            MemberRosterItem(
                name = "Reach out to us now",
                subtitle = "Director",
                avatarUrl = null,
                isAdmin = false
            )
        }

        // "View member changes" link
        Text(
            text = "View member changes",
            color = MicrobondActiveGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { onViewMemberChanges() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}

/**
 * Individual member roster tile rendering avatar, name, role/phone, and admin chip.
 */
@Composable
fun MemberRosterItem(
    name: String,
    subtitle: String,
    avatarUrl: String?,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MicroBondAvatar(
            name = name,
            avatarUrl = avatarUrl,
            modifier = Modifier.size(44.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (isAdmin) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0F3B2E))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Group Admin",
                    color = MicrobondActiveGreen,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

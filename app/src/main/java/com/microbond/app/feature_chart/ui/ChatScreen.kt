package com.microbond.app.feature_chart.ui

import com.microbond.app.core.components.groupIconImage.groupListTile.MicrobondGroupListTile
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.chips.MicrobondFilterChips
import com.microbond.app.core.components.menu.MicrobondMenuAction
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.dummydata.sampleStatuses
import com.microbond.app.core.data_classes_and_enums.ChartPreview
import com.microbond.app.feature_chart.ui.ChatSelectionController
import com.microbond.app.feature_chart.ui.ChatSelectionTopBar
import com.microbond.app.feature_chart.ui.rememberChatSelectionController

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    onChatSelected: (String) -> Unit = {},
    onAvatarClick: (String) -> Unit = {},
    onMenuAction: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("All") }
    val chatSelection = rememberChatSelectionController()
    val chatGroups = chatSelection.chats

    val menuActions = listOf(
        MicrobondMenuAction("New group", { onMenuAction("New group") }),
        MicrobondMenuAction("New community", { onMenuAction("New community") }),
        MicrobondMenuAction("Broadcast lists", { onMenuAction("Broadcast lists") }),
        MicrobondMenuAction("Linked devices", { onMenuAction("Linked devices") }),
        MicrobondMenuAction("Starred messages", { onMenuAction("Starred messages") }),
        MicrobondMenuAction("Mark all as read", {
            chatSelection.markAllAsRead()
            onMenuAction("Mark all as read")
        }),
        MicrobondMenuAction("Settings", { onMenuAction("Settings") })
    )

    val visibleGroups = chatGroups
        .sortedByDescending { chatSelection.isPinned(it.name) }
        .map { group ->
        group to ChartPreview(
            name = group.name,
            initials = group.initials,
            message = group.latestMessage,
            time = group.time,
            unreadCount = group.unreadCount,
            isOutgoingMessage = group.isOutgoingMessage,
            isFavorite = group.isFavorite,
            isGroup = group.isGroup,
            avatarColor = group.avatarColor,
            messageStatus = group.messageStatus,
            memberStatuses = group.memberStatuses.ifEmpty { sampleStatuses }
        )
        }
        .filter { (group, chart) ->
        val matchesSearch = searchQuery.isBlank() ||
            group.name.contains(searchQuery, ignoreCase = true) ||
            chart.message.contains(searchQuery, ignoreCase = true)
        val matchesChip = when (selectedChip) {
            "All" -> true
            "Unread" -> group.unreadCount > 0
            "Favorites" -> group.isFavorite
            "Groups" -> group.isGroup
            else -> false
        }
        matchesSearch && matchesChip
    }
    val visibleChatNames = visibleGroups.map { it.first.name }.toSet()
    val selectionMenuActions = chatSelection.selectionMenuActions(visibleChatNames, onMenuAction)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MicrobondDarkBackground)
    ) {
        if (chatSelection.hasSelection) {
            ChatSelectionTopBar(
                selectedCount = chatSelection.selectedCount,
                isPinned = chatSelection.areSelectedPinned,
                isMuted = chatSelection.areSelectedMuted,
                menuActions = selectionMenuActions,
                onClose = chatSelection::clearSelection,
                onPin = chatSelection::togglePinned,
                onDelete = chatSelection::deleteSelected,
                onMute = chatSelection::toggleMuted,
                onArchive = { chatSelection.archiveSelected(onMenuAction) }
            )
        } else {
            MicrobondTopAppBar(
                title = "Microbond",
                showSearchBarWigdet = false,
                showCameraIcon = false,
                showSearchIcon = true,
                showMoreIcon = true,
                onSearchIconClick = { },
                onMoreClick = { },
                menuActions = menuActions
            )
        }


        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item(key = "filters") {
                MicrobondFilterChips(
                    selectedChip = selectedChip,
                    onChipSelected = { selectedChip = it },
                    unreadCount = chatGroups.sumOf { it.unreadCount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            if (visibleGroups.isEmpty()) {
                item(key = "empty-state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "No chats found" else "No chats in this list",
                            color = MicrobondInactiveGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(visibleGroups, key = { it.first.name }) { (group, chart) ->
                    MicrobondGroupListTile(
                        chart = chart,
                        memberStatuses = chart.memberStatuses.ifEmpty { sampleStatuses },
                        groupType = group.groupType,
                        isSelected = chatSelection.isSelected(group.name),
                        onClick = {
                            if (chatSelection.hasSelection) {
                                chatSelection.toggleSelection(group.name)
                            } else {
                                onChatSelected(group.name)
                            }
                        },
                        onLongClick = { chatSelection.select(group.name) },
                        onAvatarClick = { onAvatarClick(group.name) }
                    )
                }
            }
        }
    }
}

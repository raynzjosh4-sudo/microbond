package com.microbond.app.feature_chart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.chips.MicrobondFilterChips
import com.microbond.app.core.components.contactgroupdiscovery.InstagramStyleContactGroupItem
import com.microbond.app.core.components.groupIconImage.groupListTile.MicrobondGroupListTile
import com.microbond.app.core.components.inforwidgetbanner.InfoWidgetBanner
import com.microbond.app.core.data_classes_and_enums.ChartPreview
import com.microbond.app.core.data_classes_and_enums.GroupAdminSettings
import com.microbond.app.core.data_classes_and_enums.GroupPrivateSettings
import com.microbond.app.core.data_classes_and_enums.GroupSavingsType
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.data_classes_and_enums.TargetSavingsGroupData
import com.microbond.app.core.dummydata.SampleGroup
import androidx.compose.ui.platform.LocalContext
import com.microbond.app.core.network.sync.ContactSyncEngine
import com.microbond.app.core.network.ContactSyncRequest
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.network.GroupJoinRequest
import com.microbond.app.core.network.GroupListSummaryResponse
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.network.DiscoveredGroupsRepository
import com.microbond.app.feature_groups.data.UserGroupsRepository
import com.microbond.app.feature_groups.data.isMemberOrAdmin
import com.microbond.app.feature_groups.ui.home.GroupsEmptyDiscoveryView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    onChatSelected: (String, SavingsGroupType, String) -> Unit = { _, _, _ -> },
    onAvatarClick: (String) -> Unit = {},
    onMenuAction: (String) -> Unit = {},
    onCreateGroupClick: () -> Unit = {},
    onExploreAllClick: () -> Unit = {},
    onOpenDiscoveredGroup: (DiscoverableContactGroup) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("All") }
    val userJoinedGroups by UserGroupsRepository.joinedGroups.collectAsState()
    val totalUnreadCount by UserGroupsRepository.totalUnreadCount.collectAsState()
    val discoveredContactGroups by DiscoveredGroupsRepository.groups.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val syncedContacts by ContactSyncEngine.onMicrobondContacts.collectAsState()
    val offAppContacts by ContactSyncEngine.offAppContacts.collectAsState()
    val isSyncing by ContactSyncEngine.isSyncing.collectAsState()

    // Strictly filter to circles where the current user is an active member or admin
    val memberOrAdminGroups = remember(userJoinedGroups) {
        userJoinedGroups.filter { it.isMemberOrAdmin() }
    }
    val hasJoinedGroups = memberOrAdminGroups.isNotEmpty()

    val chatSelection = rememberChatSelectionController(initialChats = memberOrAdminGroups)

    // Sync chatSelection state whenever memberOrAdminGroups changes
    LaunchedEffect(memberOrAdminGroups) {
        chatSelection.updateChats(memberOrAdminGroups)
    }

    // 1. Fetch real user joined groups from database / API and initiate contact sync
    LaunchedEffect(Unit) {
        ContactSyncEngine.syncContacts(context)
        UserGroupsRepository.syncWithBackend(context)
    }

    // 2. Discover real groups where device contacts are members
    LaunchedEffect(syncedContacts, offAppContacts) {
        val allPhones = (syncedContacts + offAppContacts)
            .map { it.phoneNumber }
            .filter { it.isNotBlank() }
            .distinct()
        if (allPhones.isNotEmpty()) {
            try {
                val response = NetworkClient.apiService.discoverGroupsByContacts(
                    ContactSyncRequest(phoneNumbers = allPhones)
                )
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val groups = response.body()!!
                    DiscoveredGroupsRepository.update(groups)
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatScreen", "Failed to discover groups by contacts", e)
            }
        }
    }

    val menuActions = listOf(
        MicrobondMenuAction("New group", { onCreateGroupClick() }),
        MicrobondMenuAction("New community", { onMenuAction("New community") }),
        MicrobondMenuAction("Broadcast lists", { onMenuAction("Broadcast lists") }),
        MicrobondMenuAction("Linked devices", { onMenuAction("Linked devices") }),
        MicrobondMenuAction("Starred messages", { onMenuAction("Starred messages") }),
        MicrobondMenuAction("Mark all as read", {
            chatSelection.markAllAsRead()
            UserGroupsRepository.markAllAsRead(context)
            onMenuAction("Mark all as read")
        }),
        MicrobondMenuAction("Settings", { onMenuAction("Settings") })
    )

    val visibleGroups = memberOrAdminGroups
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
                memberStatuses = group.memberStatuses,
                actionMessages = group.actionMessages,
                groupIconUri = group.groupIconUri
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
                onDelete = {
                    UserGroupsRepository.deleteGroups(chatSelection.selectedChatNames, context)
                    chatSelection.deleteSelected()
                },
                onMute = chatSelection::toggleMuted,
                onArchive = { chatSelection.archiveSelected(onMenuAction) }
            )
        } else {
            MicrobondTopAppBar(
                title = "Microbond",
                showSearchBarWigdet = true,
                showCameraIcon = false,
                showSearchIcon = false,
                showMoreIcon = true,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
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
                    unreadCount = totalUnreadCount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // Zero Joined Groups State: Show Discovery Widget directly when user is not member or admin of any circle
            if (!hasJoinedGroups) {
                item(key = "empty-discovery-view") {
                    if (searchQuery.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No circles found matching \"$searchQuery\"",
                                color = MicrobondInactiveGray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        // Empty state: Show contacts' groups discovery carousel & banner
                        GroupsEmptyDiscoveryView(
                            discoveredContactGroups = discoveredContactGroups,
                            isLoading = isSyncing,
                            showFab = false,
                            onGroupClick = onOpenDiscoveredGroup,
                            onExploreAllClick = onExploreAllClick,
                            onCreateGroupClick = onCreateGroupClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // User has joined groups: Show only circles where he is a member or admin
                if (visibleGroups.isEmpty()) {
                    item(key = "empty-search-or-filter") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "No circles found matching \"$searchQuery\"" else "No $selectedChip circles",
                                color = MicrobondInactiveGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(visibleGroups, key = { it.first.name }) { (group, chart) ->
                        MicrobondGroupListTile(
                            chart = chart,
                            memberStatuses = group.memberStatuses,
                            groupType = group.groupType,
                            isSelected = chatSelection.isSelected(group.name),
                            onClick = {
                                if (chatSelection.hasSelection) {
                                    chatSelection.toggleSelection(group.name)
                                } else {
                                    UserGroupsRepository.markGroupAsRead(group.name, group.id, context)
                                    onChatSelected(group.name, group.type, group.id)
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
}

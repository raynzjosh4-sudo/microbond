package com.microbond.app.feature_groups.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.network.sync.ContactSyncEngine
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_groups.viewmodel.GroupsHomeViewModel

/**
 * Main Groups Home screen that checks if the user has active joined groups:
 * - If joinedGroups is empty: renders GroupsEmptyDiscoveryView with horizontal
 *   InstagramStyleContactGroupItem carousel routing directly to inside group chat.
 * - If joinedGroups is present: renders ActiveGroupsListView.
 */
@Composable
fun GroupsHomeScreen(
    viewModel: GroupsHomeViewModel = viewModel(),
    onNavigateToCreateGroup: () -> Unit,
    onNavigateToExploreAll: () -> Unit = {},
    onGroupSelected: (String) -> Unit = {},
    onOpenDiscoveredGroup: (DiscoverableContactGroup) -> Unit = {},
    activeGroupsContent: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val syncedContacts by ContactSyncEngine.onMicrobondContacts.collectAsState()

    // Trigger discovery using synced phone numbers when available
    LaunchedEffect(syncedContacts) {
        val phoneNumbers = syncedContacts.mapNotNull { it.phoneNumber }
        viewModel.loadUserGroupsAndDiscover(phoneNumbers)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MicrobondDarkBackground)
    ) {
        when {
            state.isCheckingGroups -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MicrobondActiveGreen
                    )
                }
            }
            state.joinedGroups.isEmpty() -> {
                GroupsEmptyDiscoveryView(
                    discoveredContactGroups = state.discoveredContactGroups,
                    isLoading = state.isDiscoveringContactGroups,
                    onGroupClick = onOpenDiscoveredGroup,
                    onExploreAllClick = onNavigateToExploreAll,
                    onCreateGroupClick = onNavigateToCreateGroup
                )
            }
            else -> {
                activeGroupsContent()
            }
        }
    }
}

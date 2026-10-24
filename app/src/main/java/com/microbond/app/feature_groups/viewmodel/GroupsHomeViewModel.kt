package com.microbond.app.feature_groups.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microbond.app.core.network.ContactMember
import com.microbond.app.core.network.ContactSyncRequest
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.network.GroupJoinRequest
import com.microbond.app.core.network.GroupListSummaryResponse
import com.microbond.app.core.network.MicrobondApiService
import com.microbond.app.core.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GroupsHomeScreenState(
    val joinedGroups: List<GroupListSummaryResponse> = emptyList(),
    val discoveredContactGroups: List<DiscoverableContactGroup> = emptyList(),
    val isCheckingGroups: Boolean = false,
    val isDiscoveringContactGroups: Boolean = false,
    val isJoiningGroup: Boolean = false,
    val selectedGroupToJoin: DiscoverableContactGroup? = null
)

class GroupsHomeViewModel(
    private val apiService: MicrobondApiService = NetworkClient.apiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsHomeScreenState())
    val uiState: StateFlow<GroupsHomeScreenState> = _uiState.asStateFlow()

    fun loadUserGroupsAndDiscover(localPhoneNumbers: List<String> = emptyList()) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingGroups = true)

            // 1. Fetch user's existing joined groups from database / API
            val userGroups = fetchUserJoinedGroups()
            _uiState.value = _uiState.value.copy(
                joinedGroups = userGroups,
                isCheckingGroups = false
            )

            // 2. Discover groups where phone contacts are members
            discoverGroups(localPhoneNumbers)
        }
    }

    fun discoverGroups(phoneNumbers: List<String>) {
        viewModelScope.launch {
            if (phoneNumbers.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    discoveredContactGroups = emptyList(),
                    isDiscoveringContactGroups = false
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isDiscoveringContactGroups = true)
            try {
                val response = apiService.discoverGroupsByContacts(
                    ContactSyncRequest(phoneNumbers = phoneNumbers)
                )
                if (response.isSuccessful) {
                    val groups = response.body().orEmpty()
                    com.microbond.app.core.network.DiscoveredGroupsRepository.update(groups)
                    _uiState.value = _uiState.value.copy(
                        discoveredContactGroups = groups,
                        isDiscoveringContactGroups = false
                    )
                    return@launch
                }
            } catch (_: Exception) {
                // Network or database error
            }

            _uiState.value = _uiState.value.copy(
                discoveredContactGroups = emptyList(),
                isDiscoveringContactGroups = false
            )
        }
    }

    fun selectGroupToJoin(group: DiscoverableContactGroup?) {
        _uiState.value = _uiState.value.copy(selectedGroupToJoin = group)
    }

    fun joinGroup(groupId: String, onJoinedSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isJoiningGroup = true)
            try {
                val response = apiService.joinGroup(GroupJoinRequest(groupId = groupId))
                if (response.isSuccessful) {
                    // Refresh user groups from database
                    val refreshedGroups = fetchUserJoinedGroups()
                    _uiState.value = _uiState.value.copy(
                        joinedGroups = refreshedGroups,
                        isJoiningGroup = false,
                        selectedGroupToJoin = null
                    )
                    onJoinedSuccess()
                    return@launch
                }
            } catch (_: Exception) {}

            _uiState.value = _uiState.value.copy(
                isJoiningGroup = false,
                selectedGroupToJoin = null
            )
            onJoinedSuccess()
        }
    }

    private suspend fun fetchUserJoinedGroups(): List<GroupListSummaryResponse> {
        return try {
            val response = apiService.getGroupDashboardSummaries()
            if (response.isSuccessful) response.body().orEmpty() else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}

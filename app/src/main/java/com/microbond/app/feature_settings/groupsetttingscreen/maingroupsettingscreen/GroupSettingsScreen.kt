package com.microbond.app.feature_settings.groupsetttingscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.GroupMediaSummaryResponse
import com.microbond.app.core.network.api.goupapis.GroupMemberItemDto
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_groups.data.UserGroupsRepository
import com.microbond.app.feature_groups.ui.addmember.AddGroupMemberScreen
import com.microbond.app.feature_groups.ui.newgroup.GroupPermissionsScreen
import com.microbond.app.feature_settings.groupsetttingscreen.settingswidgets.EditGroupFieldScreen
import com.microbond.app.feature_settings.groupsetttingscreen.settingswidgets.GroupIconViewerScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.GroupSubSettingsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.MemberVisibilityScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.GroupMemberRole
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.rememberGroupSettingsAccessState
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.visibleDestinations
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.SettingDetailRouter
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.GroupSettingsHero
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.GroupSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WhatsAppGroupAvatarOverlay
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WhatsAppGroupTabs
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.rememberAvatarAlpha
import kotlinx.coroutines.launch

/**
 * WhatsApp-accurate group settings page with collapsing moving avatar, sticky
 * Members/Settings tabs, smooth section scrolling, and live group members roster.
 * Orchestrates modular sections: [GroupMembersSection], [GroupSettingsPreferencesSection],
 * and [GroupMediaPreviewCard].
 */
@Composable
fun GroupSettingsScreen(
    groupName: String,
    onBack: () -> Unit,
    memberCount: Int = 2,
    modifier: Modifier = Modifier,
    groupType: SavingsGroupType = SavingsGroupType.GOAL_SAVINGS,
    viewerRole: GroupMemberRole = GroupMemberRole.MEMBER,
    groupId: String = "",
    initialDescription: String? = null,
    initialGroupIconUri: String? = null,
    onProfileUpdated: ((newName: String?, newDescription: String?, newIconUri: String?) -> Unit)? = null
) {
    val existingGroup = remember(groupName, groupId) {
        (if (groupId.isNotBlank()) UserGroupsRepository.findById(groupId) else null)
            ?: UserGroupsRepository.findByName(groupName)
    }
    val effectiveGroupId = remember(groupId, existingGroup) {
        groupId.ifBlank { existingGroup?.id ?: "" }
    }

    // ── Navigation state ───────────────────────────────────────────────────────
    var route by remember { mutableStateOf<GroupSettingsRoute>(GroupSettingsRoute.Main) }
    val accessState = rememberGroupSettingsAccessState(groupName, groupType)
    val backToMain = { route = GroupSettingsRoute.Main }

    // ── Editable state ─────────────────────────────────────────────────────────
    var editedGroupName    by remember(groupName, existingGroup) { mutableStateOf(existingGroup?.name ?: groupName) }
    var groupDescription   by remember(existingGroup) { mutableStateOf(existingGroup?.description ?: (initialDescription ?: "The future of this world")) }
    var groupIconUri       by remember(existingGroup) { mutableStateOf(existingGroup?.groupIconUri ?: initialGroupIconUri) }
    var notificationsMuted by remember { mutableStateOf(false) }
    var mediaVisibility    by remember { mutableStateOf(false) }
    var chatLocked         by remember { mutableStateOf(false) }

    // ── Live Permissions state ─────────────────────────────────────────────────
    var membersCanEditSettings by remember { mutableStateOf(false) }
    var membersCanInviteViaLink by remember { mutableStateOf(false) }
    var membersCanSendMessages by remember { mutableStateOf(true) }
    var adminsMustApproveNewMembers by remember { mutableStateOf(false) }

    // ── Live Members roster state ──────────────────────────────────────────────
    var membersList by remember { mutableStateOf<List<GroupMemberItemDto>>(emptyList()) }
    var isMembersLoading by remember { mutableStateOf(false) }
    var memberSearchQuery by remember { mutableStateOf("") }
    var isSearchingMembers by remember { mutableStateOf(false) }

    // ── Media, docs & links summary state ──────────────────────────────────────
    var mediaSummary by remember { mutableStateOf<GroupMediaSummaryResponse?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    fun refreshMembers() {
        if (effectiveGroupId.isNotBlank() && !effectiveGroupId.startsWith("group_custom")) {
            coroutineScope.launch {
                isMembersLoading = true
                try {
                    val response = NetworkClient.apiService.getGroupMembers(effectiveGroupId)
                    if (response.isSuccessful && response.body() != null) {
                        membersList = response.body()!!
                    }
                } catch (e: Exception) {
                    android.util.Log.w("GroupSettingsScreen", "Failed to fetch group members: ${e.message}")
                } finally {
                    isMembersLoading = false
                }
            }
        }
    }

    fun refreshMediaSummary() {
        if (effectiveGroupId.isNotBlank() && !effectiveGroupId.startsWith("group_custom")) {
            coroutineScope.launch {
                try {
                    val response = NetworkClient.apiService.getGroupMediaSummary(effectiveGroupId)
                    if (response.isSuccessful && response.body() != null) {
                        mediaSummary = response.body()
                    }
                } catch (e: Exception) {
                    android.util.Log.w("GroupSettingsScreen", "Failed to fetch group media summary: ${e.message}")
                }
            }
        }
    }

    fun refreshPermissions() {
        if (effectiveGroupId.isNotBlank() && !effectiveGroupId.startsWith("group_custom")) {
            coroutineScope.launch {
                try {
                    val response = NetworkClient.apiService.getGroupPermissions(effectiveGroupId)
                    if (response.isSuccessful && response.body()?.data != null) {
                        val d = response.body()!!.data!!
                        membersCanEditSettings = d.membersCanEditSettings
                        membersCanInviteViaLink = d.membersCanInviteViaLink
                        membersCanSendMessages = d.membersCanSendMessages
                        adminsMustApproveNewMembers = d.adminsMustApproveNewMembers
                    }
                } catch (e: Exception) {
                    android.util.Log.w("GroupSettingsScreen", "Failed to fetch permissions: ${e.message}")
                }
            }
        }
    }

    LaunchedEffect(effectiveGroupId) {
        refreshMembers()
        refreshMediaSummary()
        refreshPermissions()
    }

    // ── Viewer role & permissions state ───────────────────────────────────────
    val currentUid = com.microbond.app.authentication_features.data.AuthSessionManager.currentUserId
    val myMemberInList = membersList.firstOrNull {
        (!currentUid.isNullOrBlank() && it.userId.equals(currentUid, ignoreCase = true)) ||
        it.userId.equals("me", ignoreCase = true) ||
        it.fullName.equals("You", ignoreCase = true)
    }
    val effectiveViewerRole = when {
        myMemberInList != null -> if (myMemberInList.role.equals("ADMIN", ignoreCase = true)) GroupMemberRole.ADMIN else GroupMemberRole.MEMBER
        else -> viewerRole
    }

    val canEditSettings = effectiveViewerRole.isAdmin || membersCanEditSettings
    val canInviteViaLink = effectiveViewerRole.isAdmin || membersCanInviteViaLink

    // ── Sub-screens ────────────────────────────────────────────────────────────
    when (val current = route) {
        GroupSettingsRoute.Main -> Unit

        GroupSettingsRoute.AddMember -> {
            BackHandler(onBack = backToMain)
            AddGroupMemberScreen(
                groupId = effectiveGroupId,
                groupName = editedGroupName,
                canInviteViaLink = canInviteViaLink,
                onBack = backToMain,
                onMembersAdded = {
                    refreshMembers()
                    backToMain()
                }
            )
            return
        }

        GroupSettingsRoute.EditName -> {
            if (!canEditSettings) {
                backToMain()
                return
            }
            BackHandler(onBack = backToMain)
            EditGroupFieldScreen(
                title = "Enter group name",
                initialValue = editedGroupName,
                groupId = effectiveGroupId.ifBlank { editedGroupName },
                isNameField = true,
                onBack = backToMain,
                onSavedSuccessfully = { newName ->
                    editedGroupName = newName
                    onProfileUpdated?.invoke(newName, null, null)
                    backToMain()
                }
            )
            return
        }

        GroupSettingsRoute.EditDescription -> {
            if (!canEditSettings) {
                backToMain()
                return
            }
            BackHandler(onBack = backToMain)
            EditGroupFieldScreen(
                title = "Group description",
                initialValue = groupDescription,
                groupId = effectiveGroupId.ifBlank { editedGroupName },
                isNameField = false,
                helperText = "The group description is visible to members of this group and people invited to this group.",
                onBack = backToMain,
                onSavedSuccessfully = { newDesc ->
                    groupDescription = newDesc
                    onProfileUpdated?.invoke(null, newDesc, null)
                    backToMain()
                }
            )
            return
        }

        GroupSettingsRoute.GroupIcon -> {
            BackHandler(onBack = backToMain)
            GroupIconViewerScreen(
                groupName = editedGroupName,
                iconUri = groupIconUri,
                groupId = effectiveGroupId.ifBlank { editedGroupName },
                canEditIcon = canEditSettings,
                canShare = canInviteViaLink,
                onBack = backToMain,
                onIconUpdated = { newUri ->
                    groupIconUri = newUri
                    onProfileUpdated?.invoke(null, null, newUri)
                }
            )
            return
        }

        GroupSettingsRoute.Permissions -> {
            BackHandler(onBack = { refreshPermissions(); backToMain() })
            GroupPermissionsScreen(
                groupType = groupType,
                groupName = editedGroupName,
                groupId = effectiveGroupId,
                isAdmin = effectiveViewerRole.isAdmin,
                onBack = { refreshPermissions(); backToMain() },
                modifier = modifier
            )
            return
        }

        GroupSettingsRoute.MemberVisibility -> {
            MemberVisibilityScreen(
                groupName = editedGroupName,
                groupType = groupType,
                accessState = accessState,
                onBack = backToMain,
                modifier = modifier
            )
            return
        }

        is GroupSettingsRoute.Page -> {
            GroupSubSettingsScreen(
                groupName = editedGroupName,
                memberCount = memberCount,
                groupType = groupType,
                page = current.page,
                viewerRole = effectiveViewerRole,
                accessState = accessState,
                onBack = backToMain,
                onNavigateToSetting = { settingTitle -> route = GroupSettingsRoute.SettingDetail(settingTitle, current) },
                modifier = modifier,
                groupId = effectiveGroupId
            )
            return
        }

        is GroupSettingsRoute.SettingDetail -> {
            val backFromDetail = { route = current.parentRoute ?: GroupSettingsRoute.Main }
            BackHandler(onBack = backFromDetail)
            SettingDetailRouter(
                settingTitle = current.settingTitle,
                groupName = editedGroupName,
                groupId = effectiveGroupId,
                isAdmin = effectiveViewerRole.isAdmin,
                onBack = backFromDetail
            )
            return
        }
    }

    val avatarAlpha = rememberAvatarAlpha(scrollState)
    val destinations = accessState.visibleDestinations(groupType, effectiveViewerRole)

    // Position coordinates for automatic tab switching and section smooth scrolling
    var membersSectionY by remember { mutableFloatStateOf(0f) }
    var settingsSectionY by remember { mutableFloatStateOf(0f) }
    var inlineTabsY by remember { mutableFloatStateOf(0f) }

    val currentScrollPx = scrollState.value.toFloat()

    // Auto-update active tab from scroll position
    val selectedTab = if (settingsSectionY > 0 && currentScrollPx >= (settingsSectionY - with(density) { 140.dp.toPx() })) {
        "Settings"
    } else {
        "Members"
    }

    val displayMemberCount = if (membersList.isNotEmpty()) {
        membersList.size
    } else {
        existingGroup?.memberCount ?: memberCount
    }.coerceAtLeast(2)

    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MicrobondDarkBackground)
    ) {
        Scaffold(
            containerColor = MicrobondDarkBackground,
            topBar = {
                GroupSettingsTopBar(
                    groupName = editedGroupName,
                    avatarAlpha = avatarAlpha,
                    canInviteViaLink = canInviteViaLink,
                    onBack = onBack
                )
            }
        ) { insets ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(insets)
                    .verticalScroll(scrollState)
            ) {
                // ── WhatsApp Hero Block ──────────────────────────────────────────
                GroupSettingsHero(
                    groupName = editedGroupName,
                    memberCount = displayMemberCount,
                    groupType = groupType,
                    description = groupDescription,
                    canEditSettings = canEditSettings,
                    canInviteViaLink = canInviteViaLink,
                    onEditName = { route = GroupSettingsRoute.EditName },
                    onAddDescription = { route = GroupSettingsRoute.EditDescription },
                    onSearchClick = {
                        isSearchingMembers = true
                        coroutineScope.launch {
                            scrollState.animateScrollTo(maxOf(0, (membersSectionY - with(density) { 104.dp.toPx() }).toInt()))
                        }
                    }
                )

                // ── WhatsApp Media, links, and docs Preview Row ──────────────────
                GroupMediaPreviewCard(
                    mediaSummary = mediaSummary,
                    onClick = { /* Open full categorized media gallery */ }
                )

                WaSettingsDivider()

                // ── In-line WhatsApp Tabs: Members | Settings ────────────────────
                WhatsAppGroupTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        if (tab == "Settings") {
                            coroutineScope.launch {
                                val target = maxOf(0, (settingsSectionY - with(density) { (56.dp + 46.dp).toPx() }).toInt())
                                scrollState.animateScrollTo(target)
                            }
                        } else {
                            coroutineScope.launch {
                                val target = maxOf(0, (membersSectionY - with(density) { (56.dp + 46.dp).toPx() }).toInt())
                                scrollState.animateScrollTo(target)
                            }
                        }
                    },
                    modifier = Modifier.onGloballyPositioned { coords ->
                        inlineTabsY = coords.positionInParent().y
                    }
                )

                // ── Members Section ──────────────────────────────────────────────
                GroupMembersSection(
                    displayMemberCount = displayMemberCount,
                    isSearchingMembers = isSearchingMembers,
                    onToggleSearch = { isSearchingMembers = !isSearchingMembers },
                    memberSearchQuery = memberSearchQuery,
                    onSearchQueryChange = { memberSearchQuery = it },
                    onClearSearch = { memberSearchQuery = "" },
                    onAddMemberClick = { route = GroupSettingsRoute.AddMember },
                    viewerRole = effectiveViewerRole,
                    membersList = membersList,
                    canInviteViaLink = canInviteViaLink,
                    onViewMemberChanges = { /* View changes */ },
                    modifier = Modifier.onGloballyPositioned { coords ->
                        membersSectionY = coords.positionInParent().y
                    }
                )

                WaSettingsDivider()

                // ── Settings Section ─────────────────────────────────────────────
                GroupSettingsPreferencesSection(
                    viewerRole = effectiveViewerRole,
                    groupType = groupType,
                    destinations = destinations,
                    notificationsMuted = notificationsMuted,
                    mediaVisibility = mediaVisibility,
                    onMediaVisibilityChange = { mediaVisibility = it },
                    chatLocked = chatLocked,
                    onChatLockedChange = { chatLocked = it },
                    onPermissionsClick = { route = GroupSettingsRoute.Permissions },
                    onNotificationsClick = { route = GroupSettingsRoute.SettingDetail("Notifications") },
                    onEncryptionClick = { route = GroupSettingsRoute.SettingDetail("Encryption") },
                    onAdvancedPrivacyClick = { route = GroupSettingsRoute.SettingDetail("Advanced group privacy") },
                    onOpenPage = { route = GroupSettingsRoute.Page(it) },
                    onOpenMemberVisibility = { route = GroupSettingsRoute.MemberVisibility },
                    onPaymentMethodClick = { route = GroupSettingsRoute.SettingDetail("My payment method") },
                    onExitGroupClick = { route = GroupSettingsRoute.SettingDetail("Exit group") },
                    onReportGroupClick = { route = GroupSettingsRoute.SettingDetail("Report group") },
                    modifier = Modifier.onGloballyPositioned { coords ->
                        settingsSectionY = coords.positionInParent().y
                    }
                )
            }
        }

        // ── Sticky Tabs Bar (sticks below 56dp TopBar when scrolled past hero) ──
        val tabsCollapseThresholdPx = with(density) { 260.dp.toPx() }
        if (currentScrollPx >= tabsCollapseThresholdPx) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp)
            ) {
                WhatsAppGroupTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        if (tab == "Settings") {
                            coroutineScope.launch {
                                val target = maxOf(0, (settingsSectionY - with(density) { (56.dp + 46.dp).toPx() }).toInt())
                                scrollState.animateScrollTo(target)
                            }
                        } else {
                            coroutineScope.launch {
                                val target = maxOf(0, (membersSectionY - with(density) { (56.dp + 46.dp).toPx() }).toInt())
                                scrollState.animateScrollTo(target)
                            }
                        }
                    }
                )
            }
        }

        // ── Smooth Moving WhatsApp Avatar Overlay ──────────────────────────────
        WhatsAppGroupAvatarOverlay(
            groupIconUri = groupIconUri,
            groupName = editedGroupName,
            scrollState = scrollState,
            onClick = { route = GroupSettingsRoute.GroupIcon }
        )
    }
}

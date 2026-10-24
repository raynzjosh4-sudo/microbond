package com.microbond.app.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberPlatformOverscrollFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.microbond.app.core.components.floatingbutton.MicrobondFloatingActionButton
import com.microbond.app.core.data_classes_and_enums.MessagesFabFlow
import com.microbond.app.core.dummydata.members
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_chart.ui.ChatScreen
import com.microbond.app.feature_groups.data.TrustConnectionRepository
import com.microbond.app.feature_groups.data.UserGroupsRepository
import com.microbond.app.feature_groups.ui.InviteMemberScreen
import com.microbond.app.feature_groups.ui.newgroup.NewGroupFlow
import com.microbond.app.feature_notifications.ui.NotificationsScreen
import com.microbond.app.feature_services.ui.ServicesScreen
import com.microbond.app.feature_updates.ui.UpdateGroupScreen
import com.microbond.app.feature_you.ui.WallpaperSelectionScreen
import com.microbond.app.feature_you.ui.YouScreen
import com.microbond.app.feature_you.ui.account.AccountScreen
import com.microbond.app.feature_you.ui.account.SecurityNotificationsScreen
import com.microbond.app.feature_you.ui.account.PhoneNumberScreen
import com.microbond.app.feature_you.ui.account.ChangeNumberScreen
import com.microbond.app.feature_you.ui.account.ChangePinScreen
import com.microbond.app.feature_you.ui.account.BackupEmailScreen
import com.microbond.app.feature_you.ui.account.DeleteAccountScreen
import com.microbond.app.feature_you.ui.privacy.PrivacyScreen
import com.microbond.app.feature_you.ui.privacy.AppLockScreen
import com.microbond.app.feature_you.ui.privacy.subscreens.ProfilePhotoScreen
import com.microbond.app.feature_you.ui.privacy.subscreens.AboutPrivacyScreen
import com.microbond.app.feature_you.ui.privacy.subscreens.GroupsPrivacyScreen
import com.microbond.app.feature_you.ui.linkedaccounts.LinkedAccountsScreen
import com.microbond.app.feature_you.ui.linkedaccounts.AddLinkedAccountScreen
import com.microbond.app.feature_you.ui.notifications.NotificationsSettingsScreen
import com.microbond.app.feature_you.ui.storage.StorageSettingsScreen
import com.microbond.app.feature_you.ui.storage.subscreens.ManageStorageScreen
import com.microbond.app.feature_you.ui.storage.subscreens.NetworkUsageScreen
import com.microbond.app.authentication_features.screens.ForgotPinRecoveryScreen
import com.microbond.app.feature_you.ui.help.HelpScreen
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.main.widgets.MicroBondBottomBar
import com.microbond.app.screens.threadscreen.ThreadScreen
import kotlinx.coroutines.launch


@PreviewScreenSizes
@Composable
fun MicrobondApp() {
    // 5 Pages for our 5 tabs
    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    var selectedThreadName by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var selectedPreviewName by remember { mutableStateOf<String?>(null) }
    var selectedGroupType by remember { mutableStateOf<SavingsGroupType?>(null) }
    var selectedDiscoveredGroup by remember { mutableStateOf<DiscoverableContactGroup?>(null) }
    var activeMessagesFabFlow by remember { mutableStateOf<MessagesFabFlow?>(null) }
    var activeSettingScreen by remember { mutableStateOf<String?>(null) }
    var trustConnections by remember { mutableStateOf(TrustConnectionRepository.load(context))}
    val totalUnreadCount by UserGroupsRepository.totalUnreadCount.collectAsState()
    val overscrollFactory = rememberPlatformOverscrollFactory(
        glowColor = MicrobondActiveGreen
    )

    BackHandler(enabled = selectedThreadName != null || selectedPreviewName != null || activeMessagesFabFlow != null || activeSettingScreen != null) {
        when {
            activeSettingScreen != null -> activeSettingScreen = null
            activeMessagesFabFlow != null -> activeMessagesFabFlow = null
            selectedThreadName != null -> {
                selectedThreadName = null
                selectedGroupId = null
                selectedGroupType = null
            }
            selectedPreviewName != null -> selectedPreviewName = null
        }
    }

    CompositionLocalProvider(LocalOverscrollFactory provides overscrollFactory) {
        Scaffold(
            
            containerColor = MicrobondDarkBackground,

            bottomBar = {
                if (selectedThreadName == null && activeMessagesFabFlow == null && activeSettingScreen == null) {
                    MicroBondBottomBar(
                        selectedTabIndex = pagerState.currentPage,
                        unreadChartCount = totalUnreadCount,
                        onTabSelected = { index ->
                            if (index != pagerState.currentPage) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (selectedThreadName == null && activeMessagesFabFlow == null && activeSettingScreen == null && pagerState.currentPage == 0) {
                    MicrobondFloatingActionButton(
                        icon = Icons.Filled.GroupAdd,
                        contentDescription = "Create a new group",
                        secondaryIcon = Icons.Filled.PersonAdd,
                        secondarycontentDescription = "Create a new group",
                        onclick = { activeMessagesFabFlow = MessagesFabFlow.CREATE_GROUP },
                        onsecondaryClick = { activeMessagesFabFlow = MessagesFabFlow.INVITE_A_FREIND },
                    )
                }
            }

        ) { innerPadding ->
            // HorizontalPager allows smooth left-to-right swiping between tabs
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = selectedThreadName == null && activeMessagesFabFlow == null && activeSettingScreen == null,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .fillMaxSize()
                    .background(MicrobondDarkBackground)
            ) { page ->
                // Load the correct screen based on current swipe page
                when (page) {
                    0 -> {
                        when (activeMessagesFabFlow) {
                            MessagesFabFlow.INVITE_A_FREIND -> InviteMemberScreen(
                                onBack = { activeMessagesFabFlow = null },
                                isFromYouPage = false
                            )
                            MessagesFabFlow.CREATE_GROUP -> NewGroupFlow(
                                trustedConnections = trustConnections,
                                onBack = { activeMessagesFabFlow = null },
                                onGroupCreated = { setup ->
                                    activeMessagesFabFlow = null
                                    selectedThreadName = setup.name
                                    selectedGroupId = setup.groupId
                                    selectedGroupType = setup.groupType
                                    UserGroupsRepository.addGroupFromSetup(setup, context)
                                }
                            )
                            null -> {
                                if (selectedPreviewName != null) {
                                    com.microbond.app.feature_chart.PreviewPage(
                                        onBack = { selectedPreviewName = null }
                                    )
                                } else if (selectedThreadName == null) {
                                    ChatScreen(
                                        onChatSelected = { threadName, groupType, groupId ->
                                            selectedThreadName = threadName
                                            selectedGroupId = groupId
                                            selectedGroupType = groupType
                                            selectedDiscoveredGroup = null
                                        },
                                        onAvatarClick = { selectedPreviewName = it },
                                        onCreateGroupClick = { activeMessagesFabFlow = MessagesFabFlow.CREATE_GROUP },
                                        onExploreAllClick = { activeMessagesFabFlow = MessagesFabFlow.CREATE_GROUP },
                                        onOpenDiscoveredGroup = { discoveredGroup ->
                                            selectedThreadName = discoveredGroup.name
                                            selectedGroupId = discoveredGroup.id.ifBlank { discoveredGroup.groupId }
                                            selectedDiscoveredGroup = discoveredGroup
                                            selectedGroupType = when (discoveredGroup.type?.uppercase()) {
                                                "ROTATING", "MERRY_GO_ROUND" -> SavingsGroupType.MERRY_GO_ROUND
                                                "WELFARE_EMERGENCY", "WELFARE_FUND" -> SavingsGroupType.WELFARE_FUND
                                                "INVESTMENT_CLUB" -> SavingsGroupType.INVESTMENT_CLUB
                                                "TARGET_SAVINGS", "GOAL_SAVINGS" -> SavingsGroupType.GOAL_SAVINGS
                                                "BILL_SPLIT" -> SavingsGroupType.BILL_SPLIT
                                                "EVENT_COMMITTEE" -> SavingsGroupType.EVENT_COMMITTEE
                                                "SHARED_WALLET" -> SavingsGroupType.SHARED_WALLET
                                                "ASSOCIATION_DUES" -> SavingsGroupType.ASSOCIATION_DUES
                                                "FUNDRAISER" -> SavingsGroupType.FUNDRAISER
                                                else -> SavingsGroupType.MERRY_GO_ROUND
                                            }
                                        }
                                    )
                                } else {
                                    val activeGroupId = selectedGroupId
                                        ?: selectedDiscoveredGroup?.id
                                        ?: UserGroupsRepository.findByName(selectedThreadName!!)?.id
                                    ThreadScreen(
                                        entityName = selectedThreadName!!,
                                        groupId = activeGroupId,
                                        groupType = selectedGroupType,
                                        discoveredGroup = selectedDiscoveredGroup,
                                        onNavigateBack = {
                                            selectedThreadName = null
                                            selectedGroupId = null
                                            selectedGroupType = null
                                            selectedDiscoveredGroup = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                    1 -> UpdateGroupScreen()
                    2 -> ServicesScreen()
                    3 -> NotificationsScreen()
                    4 -> {
                        when (activeSettingScreen) {
                            "Threads" -> WallpaperSelectionScreen(onBack = { activeSettingScreen = null })
                            "Account" -> AccountScreen(
                                onNavigateToSetting = { activeSettingScreen = it },
                                onBack = { activeSettingScreen = null },
                                onLogout = { activeSettingScreen = null }
                            )
                            "Security notifications" -> SecurityNotificationsScreen(onBack = { activeSettingScreen = "Account" })
                            "Change PIN" -> ChangePinScreen(
                                onBack = { activeSettingScreen = "Account" },
                                onForgotPinClick = { activeSettingScreen = "Forgot PIN" }
                            )
                            "Forgot PIN" -> ForgotPinRecoveryScreen(
                                onNavigateBack = { activeSettingScreen = "Change PIN" },
                                onPinResetReady = {
                                    activeSettingScreen = "Change PIN"
                                }
                            )
                            "Backup email" -> BackupEmailScreen(onBack = { activeSettingScreen = "Account" })
                            "Phone number" -> PhoneNumberScreen(onBack = { activeSettingScreen = "Account" })
                            "Change number" -> ChangeNumberScreen(onBack = { activeSettingScreen = "Account" })
                            "Delete account" -> DeleteAccountScreen(
                                onBack = { activeSettingScreen = "Account" },
                                onAccountDeleted = { activeSettingScreen = null }
                            )
                            "Privacy" -> PrivacyScreen(
                                onNavigateToSetting = { activeSettingScreen = it },
                                onBack = { activeSettingScreen = null }
                            )
                            "Profile photo" -> ProfilePhotoScreen(onBack = { activeSettingScreen = "Privacy" })
                            "About privacy" -> AboutPrivacyScreen(onBack = { activeSettingScreen = "Privacy" })
                            "Groups privacy" -> GroupsPrivacyScreen(onBack = { activeSettingScreen = "Privacy" })
                            "App lock" -> AppLockScreen(onBack = { activeSettingScreen = "Privacy" })
                            "Linked Accounts" -> LinkedAccountsScreen(
                                onNavigateToSetting = { activeSettingScreen = it },
                                onBack = { activeSettingScreen = null }
                            )

                            
                            "Add Linked Account" -> AddLinkedAccountScreen(onBack = { activeSettingScreen = "Linked Accounts" })
                            "Notifications" -> NotificationsSettingsScreen(onBack = { activeSettingScreen = null })
                            "Storage" -> StorageSettingsScreen(
                                onNavigateToSetting = { activeSettingScreen = it },
                                onBack = { activeSettingScreen = null }
                            )
                            "Manage storage" -> ManageStorageScreen(onBack = { activeSettingScreen = "Storage" })
                            "Network usage" -> NetworkUsageScreen(onBack = { activeSettingScreen = "Storage" })
                            "Help" -> HelpScreen(onBack = { activeSettingScreen = null })
                            "Invite" -> InviteMemberScreen(
                                onBack = { activeSettingScreen = null },
                                isFromYouPage = true
                            )
                            else -> YouScreen(
                                onNavigateToSetting = { activeSettingScreen = it },
                                onNavigateToProfile = { activeSettingScreen = "Account" }
                            )
                        }
                    }
                }
            }
        }
    }
}


package com.microbond.app.main

import android.graphics.drawable.Icon
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberPlatformOverscrollFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.microbond.app.core.components.floatingbutton.MicrobondFloatingActionButton
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_chart.ui.ChatScreen
import com.microbond.app.feature_groups.ui.CreateCircleScreen
import com.microbond.app.feature_groups.ui.InviteMemberScreen
import com.microbond.app.feature_groups.data.TrustConnectionRepository
import com.microbond.app.feature_notifications.ui.NotificationsScreen
import com.microbond.app.feature_services.ui.ServicesScreen
import com.microbond.app.feature_updates.ui.UpdateScreenV2
import com.microbond.app.feature_you.ui.WallpaperSelectionScreen
import com.microbond.app.feature_you.ui.YouScreen
import com.microbond.app.main.widgets.MicroBondBottomBar
import com.microbond.app.screens.thread.ThreadScreen
import kotlinx.coroutines.launch

private enum class MessagesFabFlow {
    CREATE_CIRCLE,
    INVITE_MEMBER
}

@PreviewScreenSizes
@Composable
fun MicrobondApp() {
    // 5 Pages for our 5 tabs
    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    var selectedThreadName by remember { mutableStateOf<String?>(null) }
    var selectedPreviewName by remember { mutableStateOf<String?>(null) }
    var activeMessagesFabFlow by remember { mutableStateOf<MessagesFabFlow?>(null) }
    var activeSettingScreen by remember { mutableStateOf<String?>(null) }
    var trustConnections by remember { mutableStateOf(TrustConnectionRepository.load(context)) }
    val overscrollFactory = rememberPlatformOverscrollFactory(
        glowColor = MicrobondActiveGreen
    )

    BackHandler(enabled = selectedThreadName != null || selectedPreviewName != null || activeMessagesFabFlow != null || activeSettingScreen != null) {
        when {
            activeSettingScreen != null -> activeSettingScreen = null
            activeMessagesFabFlow != null -> activeMessagesFabFlow = null
            selectedThreadName != null -> selectedThreadName = null
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
                        contentDescription = "Create a new vault or circle",
                        secondaryIcon = Icons.Filled.PersonAdd,
                        secondarycontentDescription = "Invite a new member",
                        onclick = { activeMessagesFabFlow = MessagesFabFlow.CREATE_CIRCLE },
                        onsecondaryClick = { activeMessagesFabFlow = MessagesFabFlow.INVITE_MEMBER },
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
                            MessagesFabFlow.CREATE_CIRCLE -> CreateCircleScreen(
                                onBack = { activeMessagesFabFlow = null },
                                onCreateCircle = { activeMessagesFabFlow = null },
                                trustedConnections = trustConnections
                            )
                            MessagesFabFlow.INVITE_MEMBER -> InviteMemberScreen(
                                onBack = { activeMessagesFabFlow = null },
                                trustedConnections = trustConnections,
                                onSaveConnection = { connection ->
                                    trustConnections = TrustConnectionRepository.add(context, connection)
                                }
                            )
                            null -> {
                                val threadName = selectedThreadName
                                if (selectedPreviewName != null) {
                                    com.microbond.app.feature_chart.PreviewPage(
                                        onBack = { selectedPreviewName = null }
                                    )
                                } else if (threadName == null) {
                                    ChatScreen(
                                        onChatSelected = { selectedThreadName = it },
                                        onAvatarClick = { selectedPreviewName = it }
                                    )
                                } else {
                                    ThreadScreen(
                                        entityName = threadName,
                                        onNavigateBack = { selectedThreadName = null }
                                    )
                                }
                            }
                        }
                    }
                    1 -> UpdateScreenV2()
                    2 -> ServicesScreen()
                    3 -> NotificationsScreen()
                    4 -> {
                        if (activeSettingScreen == "Threads") {
                            WallpaperSelectionScreen(onBack = { activeSettingScreen = null })
                        } else {
                            YouScreen(onNavigateToSetting = { activeSettingScreen = it })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, color = MicrobondTextPrimary)
    }
}

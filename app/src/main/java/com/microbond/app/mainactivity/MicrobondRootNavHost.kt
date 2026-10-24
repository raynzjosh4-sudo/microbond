package com.microbond.app.mainactivity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.authentication_features.lifecycle.AppLockLifecycleObserver
import com.microbond.app.authentication_features.sheets.BiometricPromptSheet
import com.microbond.app.authentication_features.sheets.ProtectAccountSheet
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.sync.ContactSyncEngine
import com.microbond.app.main.MicrobondApp

/**
 * Root navigation host that orchestrates authentication, app lifecycle locking,
 * session revocation, and seamless transition to MicrobondApp.
 */
@Composable
fun MicrobondRootNavHost(
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val sessionAuthed by AuthSessionManager.isAuthenticated.collectAsState()
    val userHasAppPin by AuthSessionManager.hasAppPin.collectAsState()
    val isProfileComplete by AuthSessionManager.isProfileComplete.collectAsState()
    val isBiometricsEnabled by AuthSessionManager.isBiometricsEnabled.collectAsState()
    var showMainApp by remember { mutableStateOf(false) }

    val initialScreen = remember {
        if (sessionAuthed && !isProfileComplete) {
            "PROFILE_INFO"
        } else if (sessionAuthed && !userHasAppPin) {
            "CREATE_PIN"
        } else if (sessionAuthed && userHasAppPin) {
            "SESSION_UNLOCK"
        } else {
            "WELCOME"
        }
    }
    val authState = rememberAuthFlowState(initialScreen)

    // 60-second idle background lock: overlays SessionUnlockPinScreen upon return
    DisposableEffect(lifecycleOwner) {
        val appLockObserver = AppLockLifecycleObserver(idleTimeoutMs = 60_000L) {
            if (AuthSessionManager.isAuthenticated.value && AuthSessionManager.hasAppPin.value) {
                authState.authErrorMessage = null
                authState.authBackStack.clear()
                authState.authBackStack.add("SESSION_UNLOCK")
                showMainApp = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(appLockObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(appLockObserver)
        }
    }

    // Global HTTP 401 interceptor: handles token revocation/expiration by wiping session
    DisposableEffect(Unit) {
        NetworkClient.onUnauthorizedCallback = {
            if (authState.currentAuthScreen != "SESSION_UNLOCK") {
                AuthSessionManager.clearSession(context)
                authState.authErrorMessage = "Session expired. Please log in again."
                authState.resetToWelcome()
                showMainApp = false
            }
        }
        onDispose {
            NetworkClient.onUnauthorizedCallback = null
        }
    }

    // Sync device contacts in background to identify Microbond users and mutual groups
    LaunchedEffect(showMainApp) {
        if (showMainApp) {
            ContactSyncEngine.syncContacts(context)
        }
    }

    // Alert user immediately if currently locked out upon entering SESSION_UNLOCK
    LaunchedEffect(authState.currentAuthScreen) {
        if (authState.currentAuthScreen == "SESSION_UNLOCK") {
            val remaining = AuthSessionManager.getRemainingLockoutSeconds(context)
            if (remaining > 0) {
                authState.authErrorMessage = "Too many failed attempts. Try again in $remaining seconds."
            }
        }
    }

    fun proceedToMainApp() {
        if (!AuthSessionManager.isProfileComplete.value && !authState.isPinResetting) {
            if (!authState.authBackStack.contains("PROFILE_INFO")) {
                authState.navigateTo("PROFILE_INFO")
            }
            showMainApp = false
            return
        }
        if (!AuthSessionManager.hasAppPin.value) {
            if (!authState.authBackStack.contains("CREATE_PIN")) {
                authState.navigateTo("CREATE_PIN")
            }
            showMainApp = false
            return
        }
        val hasContactsPerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasContactsPerm) {
            authState.allowAccessNextScreen = "MAIN_APP"
            authState.navigateTo("ALLOW_ACCESS")
        } else {
            ContactSyncEngine.syncContacts(context)
            showMainApp = true
        }
    }

    LaunchedEffect(sessionAuthed, userHasAppPin, isProfileComplete, authState.isPinResetting) {
        if (sessionAuthed && !isProfileComplete && !authState.isPinResetting) {
            authState.showProtectAccountSheet = false
            if (!authState.authBackStack.contains("PROFILE_INFO")) {
                authState.navigateTo("PROFILE_INFO")
            }
            showMainApp = false
        } else if (sessionAuthed && !userHasAppPin) {
            authState.showProtectAccountSheet = false
            if (!authState.authBackStack.contains("CREATE_PIN")) {
                authState.navigateTo("CREATE_PIN")
            }
            showMainApp = false
        } else if (!sessionAuthed) {
            showMainApp = false
            if (!authState.authBackStack.contains("WELCOME")) {
                authState.resetToWelcome()
            }
        }
    }

    // Intercept Android hardware/system back button during authentication flow
    BackHandler(enabled = !showMainApp && authState.authBackStack.size > 1) {
        authState.handleAuthBack(onFinish)
    }

    AnimatedContent(
        //animated container socket
        targetState = showMainApp,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "AuthToMainAppTransition"
    ) { isAuthed ->
        if (!isAuthed) {
            AnimatedContent(
                targetState = authState.currentAuthScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                },
                label = "AuthScreensFlow"
            ) { screen ->
                AuthScreenRouter(
                    screen = screen,
                    state = authState,
                    context = context,
                    coroutineScope = coroutineScope,
                    isBiometricsEnabled = isBiometricsEnabled,
                    onProceedToMainApp = ::proceedToMainApp,
                    onFinish = onFinish
                )
            }

            if (authState.showBiometricPromptSheet) {
                BiometricPromptSheet(
                    onDismissRequest = {
                        authState.showBiometricPromptSheet = false
                        AuthSessionManager.setBiometricsPrompted(context, true)
                        proceedToMainApp()
                    },
                    onEnableBiometrics = {
                        AuthSessionManager.setBiometricsEnabled(context, true)
                        authState.showBiometricPromptSheet = false
                        proceedToMainApp()
                    },
                    onMaybeLater = {
                        AuthSessionManager.setBiometricsEnabled(context, false)
                        authState.showBiometricPromptSheet = false
                        proceedToMainApp()
                    }
                )
            }

            if (authState.showProtectAccountSheet) {
                ProtectAccountSheet(
                    onDismissRequest = {
                        if (userHasAppPin) {
                            authState.showProtectAccountSheet = false
                        } else {
                            authState.showProtectAccountSheet = false
                            authState.navigateTo("CREATE_PIN")
                        }
                    },
                    onCreatePinClick = {
                        authState.showProtectAccountSheet = false
                        authState.navigateTo("CREATE_PIN")
                    },
                    onSkipClick = null
                )
            }
        } else {
            MicrobondApp()
        }
    }
}

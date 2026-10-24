package com.microbond.app.mainactivity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.microbond.app.core.components.countrypicker.Country

/**
 * Encapsulates the UI state, navigation backstack, and recovery tokens
 * for the entire Microbond authentication flow.
 */
class AuthFlowState(
    initialScreen: String
) {
    val authBackStack: SnapshotStateList<String> = mutableStateListOf(initialScreen)
    val currentAuthScreen: String get() = authBackStack.lastOrNull() ?: "WELCOME"

    var pendingPhoneNumber by mutableStateOf("")
    var savedCountry by mutableStateOf<Country?>(null)
    var savedPhoneNumberInput by mutableStateOf("")
    var savedProfileName by mutableStateOf("")
    var savedAvatarUrl by mutableStateOf<String?>(null)
    var savedAvatarColor by mutableStateOf<String?>(null)
    var savedAboutStatus by mutableStateOf("")
    var allowAccessNextScreen by mutableStateOf("VERIFY_OTP")

    var isRequestingOtp by mutableStateOf(false)
    var isVerifyingOtp by mutableStateOf(false)
    var isVerifyingPin by mutableStateOf(false)
    var isUpdatingProfile by mutableStateOf(false)
    var isSettingPin by mutableStateOf(false)
    var isPinResetting by mutableStateOf(false)

    var emailRecoveryTempToken by mutableStateOf<String?>(null)
    var emailRecoveryFinalToken by mutableStateOf<String?>(null)
    var authErrorMessage by mutableStateOf<String?>(null)

    var showProtectAccountSheet by mutableStateOf(false)
    var showBiometricPromptSheet by mutableStateOf(false)

    fun navigateTo(screen: String) {
        authErrorMessage = null
        authBackStack.add(screen)
    }

    fun handleAuthBack(onFinish: () -> Unit) {
        authErrorMessage = null
        if (authBackStack.size > 1) {
            authBackStack.removeAt(authBackStack.lastIndex)
        } else {
            onFinish()
        }
    }

    fun resetToWelcome() {
        authErrorMessage = null
        authBackStack.clear()
        authBackStack.add("WELCOME")
    }
}

@Composable
fun rememberAuthFlowState(initialScreen: String): AuthFlowState {
    return remember { AuthFlowState(initialScreen) }
}

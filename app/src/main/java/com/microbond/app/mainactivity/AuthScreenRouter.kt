package com.microbond.app.mainactivity

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.authentication_features.screens.AllowAccessScreen
import com.microbond.app.authentication_features.screens.BackupEmailRecoveryScreen
import com.microbond.app.authentication_features.screens.CreateSecurityPinScreen
import com.microbond.app.authentication_features.screens.EnterPhoneNumberScreen
import com.microbond.app.authentication_features.screens.ForgotPinRecoveryScreen
import com.microbond.app.authentication_features.screens.LostPhoneRecoveryScreen
import com.microbond.app.authentication_features.screens.OtpVerificationScreen
import com.microbond.app.authentication_features.screens.ProfileInfoScreen
import com.microbond.app.authentication_features.screens.SessionUnlockPinScreen
import com.microbond.app.authentication_features.screens.WelcomeOnboardingScreen
import kotlinx.coroutines.CoroutineScope

/**
 * Maps each authentication screen route identifier to its respective Composable screen,
 * binding screen events to AuthActions and AuthFlowState.
 */
@Composable
fun AuthScreenRouter(
    screen: String,
    state: AuthFlowState,
    context: Context,
    coroutineScope: CoroutineScope,
    isBiometricsEnabled: Boolean,
    onProceedToMainApp: () -> Unit,
    onFinish: () -> Unit
) {
    when (screen) {
        "WELCOME" -> {
            WelcomeOnboardingScreen(
                onGetStarted = {
                    state.navigateTo("ENTER_PHONE")
                }
            )
        }
        "ENTER_PHONE" -> {
            EnterPhoneNumberScreen(
                initialCountry = state.savedCountry,
                initialPhoneNumber = state.savedPhoneNumberInput,
                isLoading = state.isRequestingOtp,
                errorMessage = state.authErrorMessage,
                onPhoneNumberChange = { country, phone ->
                    state.savedCountry = country
                    state.savedPhoneNumberInput = phone
                },
                onNavigateBack = {
                    state.handleAuthBack(onFinish)
                },
                onNextClick = { fullPhone ->
                    state.pendingPhoneNumber = fullPhone
                    state.allowAccessNextScreen = "VERIFY_OTP"
                    state.navigateTo("ALLOW_ACCESS")
                },
                onLostPhoneRecoveryClick = {
                    state.navigateTo("LOST_PHONE_RECOVERY")
                },
                onLostAccessToNumberClick = {
                    state.authErrorMessage = null
                    state.navigateTo("BACKUP_EMAIL_RECOVERY")
                }
            )
        }
        "ALLOW_ACCESS" -> {
            AllowAccessScreen(
                secondaryButtonText = if (state.allowAccessNextScreen == "MAIN_APP") "Not now" else "Verify another way",
                onContinue = {
                    if (state.allowAccessNextScreen == "MAIN_APP") {
                        onProceedToMainApp()
                    } else {
                        state.navigateTo("VERIFY_OTP")
                        AuthActions.requestOtp(state.pendingPhoneNumber, coroutineScope, state)
                    }
                },
                onVerifyAnotherWay = {
                    if (state.allowAccessNextScreen == "MAIN_APP") {
                        onProceedToMainApp()
                    } else {
                        state.navigateTo("VERIFY_OTP")
                        AuthActions.requestOtp(state.pendingPhoneNumber, coroutineScope, state)
                    }
                },
                onNavigateBack = {
                    state.handleAuthBack(onFinish)
                }
            )
        }
        "VERIFY_OTP" -> {
            OtpVerificationScreen(
                phoneNumber = state.pendingPhoneNumber,
                isRequestingSms = state.isRequestingOtp,
                isVerifyingCode = state.isVerifyingOtp,
                errorMessage = state.authErrorMessage,
                onClearError = {
                    state.authErrorMessage = null
                },
                onVerifyOtp = { code ->
                    AuthActions.verifyOtpCode(code, context, coroutineScope, state)
                },
                onResendOtp = {
                    AuthActions.resendOtp(coroutineScope, state)
                },
                onWrongNumberClick = {
                    state.authErrorMessage = null
                    while (state.authBackStack.size > 1 && state.authBackStack.last() != "ENTER_PHONE") {
                        state.authBackStack.removeAt(state.authBackStack.lastIndex)
                    }
                    if (state.authBackStack.isEmpty()) {
                        state.authBackStack.add("ENTER_PHONE")
                    }
                },
                onNavigateBack = {
                    state.handleAuthBack(onFinish)
                }
            )
        }
        "PROFILE_INFO" -> {
            val currentDisplayName = AuthSessionManager.displayName.collectAsState().value
            val currentAvatarUrl = AuthSessionManager.avatarUrl.collectAsState().value
            val currentAvatarColor = AuthSessionManager.avatarColor.collectAsState().value
            val currentAboutStatus = AuthSessionManager.aboutStatus.collectAsState().value

            ProfileInfoScreen(
                initialName = state.savedProfileName.ifBlank { currentDisplayName.orEmpty() },
                initialAvatarUrl = state.savedAvatarUrl ?: currentAvatarUrl,
                initialAboutStatus = state.savedAboutStatus.ifBlank { currentAboutStatus ?: "Hey there! I am using Microbond" },
                initialAvatarColor = state.savedAvatarColor ?: currentAvatarColor,
                isLoading = state.isUpdatingProfile,
                errorMessage = state.authErrorMessage,
                onNextClick = { name, imageUri, about, avatarColor ->
                    AuthActions.updateProfile(
                        name = name,
                        imageUri = imageUri,
                        about = about,
                        avatarColor = avatarColor,
                        context = context,
                        coroutineScope = coroutineScope,
                        state = state,
                        onProceedToMainApp = onProceedToMainApp
                    )
                },
                onNavigateBack = {
                    state.handleAuthBack(onFinish)
                }
            )
        }
        "CREATE_PIN" -> {
            CreateSecurityPinScreen(
                isLoading = state.isSettingPin,
                errorMessage = state.authErrorMessage,
                isResetFlow = state.isPinResetting,
                onPinCreated = { pin ->
                    AuthActions.createOrResetPin(
                        pin = pin,
                        context = context,
                        coroutineScope = coroutineScope,
                        state = state,
                        onProceedToMainApp = onProceedToMainApp
                    )
                },
                onNavigateBack = {
                    if (state.isPinResetting) {
                        state.isPinResetting = false
                        state.resetToWelcome()
                    } else {
                        state.handleAuthBack(onFinish)
                    }
                },
                onForgotPinClick = {
                    state.authErrorMessage = null
                    state.navigateTo("FORGOT_PIN_RECOVERY")
                },
                onNavigateToUnlock = {
                    state.authErrorMessage = null
                    onProceedToMainApp()
                }
            )
        }
        "FORGOT_PIN_RECOVERY" -> {
            ForgotPinRecoveryScreen(
                initialPhoneNumber = state.pendingPhoneNumber.ifBlank {
                    AuthSessionManager.phoneNumber.collectAsState().value.orEmpty()
                },
                onNavigateBack = {
                    state.handleAuthBack(onFinish)
                },
                onPinResetReady = { recoveryToken ->
                    if (!recoveryToken.isNullOrBlank()) {
                        AuthSessionManager.updateAccessToken(context, recoveryToken)
                    }
                    state.authErrorMessage = null
                    state.isPinResetting = true
                    AuthSessionManager.setProfileComplete(context, true)
                    state.authBackStack.clear()
                    state.authBackStack.add("CREATE_PIN")
                }
            )
        }
        "SESSION_UNLOCK" -> {
            SessionUnlockPinScreen(
                isLoading = state.isVerifyingPin,
                errorMessage = state.authErrorMessage,
                isBiometricsEnabled = isBiometricsEnabled,
                onBiometricClick = {
                    state.authErrorMessage = null
                    onProceedToMainApp()
                },
                onPinEntered = { pin ->
                    AuthActions.verifyUnlockPin(
                        pin = pin,
                        context = context,
                        coroutineScope = coroutineScope,
                        state = state,
                        onProceedToMainApp = onProceedToMainApp
                    )
                },
                onForgotPinClick = {
                    state.authErrorMessage = null
                    state.navigateTo("FORGOT_PIN_RECOVERY")
                },
                onNavigateBack = if (state.authBackStack.size > 1) { { state.handleAuthBack(onFinish) } } else null
            )
        }
        "LOST_PHONE_RECOVERY" -> {
            LostPhoneRecoveryScreen(
                onNavigateBack = {
                    state.handleAuthBack(onFinish)
                },
                onRecoverySuccess = { newPhone, tempRecoveryToken ->
                    AuthSessionManager.saveSession(
                        context = context,
                        token = tempRecoveryToken,
                        userId = null,
                        phoneNumber = newPhone,
                        hasAppPin = true
                    )
                    state.authErrorMessage = null
                    onProceedToMainApp()
                },
                onBackupEmailRecoveryClick = {
                    state.authErrorMessage = null
                    state.navigateTo("BACKUP_EMAIL_RECOVERY")
                }
            )
        }
        "BACKUP_EMAIL_RECOVERY" -> {
            BackupEmailRecoveryScreen(
                onNavigateBack = {
                    state.handleAuthBack(onFinish)
                },
                onRecoveryComplete = { newPhone, tempToken ->
                    state.pendingPhoneNumber = newPhone
                    state.emailRecoveryTempToken = tempToken
                    state.isPinResetting = true
                    state.authErrorMessage = null
                    state.navigateTo("VERIFY_OTP")
                }
            )
        }
    }
}

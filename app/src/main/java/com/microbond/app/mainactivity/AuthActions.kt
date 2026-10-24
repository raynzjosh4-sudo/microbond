package com.microbond.app.mainactivity

import android.content.Context
import android.net.Uri
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.network.AvatarUploadHelper
import com.microbond.app.core.network.ConfirmPhoneRecoveryRequest
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.OtpRequestPayload
import com.microbond.app.core.network.OtpVerifyPayload
import com.microbond.app.core.network.RequestPhoneRecoveryOtpRequest
import com.microbond.app.core.network.SetAppPinPayload
import com.microbond.app.core.network.UpdateUserProfileRequest
import com.microbond.app.core.network.VerifyAppPinRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Encapsulates the network actions, OTP calls, profile updates, and PIN verification
 * executed during authentication and recovery.
 */
object AuthActions {

    fun requestOtp(
        phone: String,
        coroutineScope: CoroutineScope,
        state: AuthFlowState
    ) {
        coroutineScope.launch {
            state.isRequestingOtp = true
            try {
                val sanitizedPhone = phone.trim().replace(" ", "").replace("-", "")
                val response = NetworkClient.apiService.requestOtp(
                    OtpRequestPayload(phoneNumber = sanitizedPhone)
                )
                if (!response.isSuccessful) {
                    val code = response.code()
                    val err = response.errorBody()?.string()
                    state.authErrorMessage = when (code) {
                        502, 503, 504 -> "Server is waking up. Please wait a moment and try again."
                        in 500..599 -> "Server error ($code). Please try again shortly."
                        else -> parseApiError(err, "Failed to send SMS code.")
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                state.authErrorMessage = "Server wake-up took too long. Please try again."
            } catch (e: java.io.IOException) {
                state.authErrorMessage = "Unable to connect to server. Please check your internet connection."
            } catch (e: Exception) {
                state.authErrorMessage = "Connection error: ${e.localizedMessage ?: "Unable to connect"}"
            } finally {
                state.isRequestingOtp = false
            }
        }
    }

    fun verifyOtpCode(
        code: String,
        context: Context,
        coroutineScope: CoroutineScope,
        state: AuthFlowState
    ) {
        val cleanCode = code.trim()
        if (state.isVerifyingOtp || cleanCode.length != 6) return
        state.isVerifyingOtp = true
        state.authErrorMessage = null
        coroutineScope.launch {
            try {
                val sanitizedPhone = state.pendingPhoneNumber.trim().replace(" ", "").replace("-", "")
                if (!state.emailRecoveryTempToken.isNullOrBlank()) {
                    val response = NetworkClient.apiService.confirmPhoneRecovery(
                        authHeader = "Bearer ${state.emailRecoveryTempToken}",
                        payload = ConfirmPhoneRecoveryRequest(
                            new_phone_number = sanitizedPhone,
                            code = cleanCode
                        )
                    )
                    if (response.isSuccessful && response.body() != null) {
                        state.emailRecoveryFinalToken = response.body()!!.recovery_token
                        state.emailRecoveryTempToken = null
                        state.isPinResetting = true
                        state.authErrorMessage = null
                        state.navigateTo("CREATE_PIN")
                    } else {
                        val code = response.code()
                        val err = response.errorBody()?.string()
                        state.authErrorMessage = when (code) {
                            502, 503, 504 -> "Server is waking up. Please wait a moment and try again."
                            in 500..599 -> "Server error ($code). Please try again shortly."
                            else -> parseApiError(err, "Wrong SMS code. Try again.")
                        }
                    }
                } else {
                    val response = NetworkClient.apiService.verifyOtp(
                        OtpVerifyPayload(
                            phoneNumber = sanitizedPhone,
                            code = cleanCode
                        )
                    )
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        var isProfileDone = body.isProfileComplete && !body.isNewUser
                        var resolvedName = body.fullName.orEmpty()
                        var resolvedAvatar = body.avatarUrl ?: body.profilePictureUrl
                        var resolvedAbout = body.aboutStatus.orEmpty()
                        var resolvedAvatarColor = body.avatarColor

                        AuthSessionManager.saveSession(
                            context = context,
                            token = body.accessToken,
                            userId = body.userId,
                            phoneNumber = body.phoneNumber,
                            hasAppPin = body.hasAppPin,
                            isProfileComplete = isProfileDone,
                            displayName = resolvedName,
                            avatarUrl = resolvedAvatar,
                            avatarColor = resolvedAvatarColor
                        )

                        // Attempt to fetch full profile from backend to populate any existing name, avatar, bio, color
                        try {
                            val profileResp = NetworkClient.apiService.getCurrentUserProfile()
                            if (profileResp.isSuccessful && profileResp.body()?.data != null) {
                                val userProfile = profileResp.body()!!.data!!
                                if (!userProfile.fullName.isNullOrBlank()) {
                                    resolvedName = userProfile.fullName
                                }
                                val pic = userProfile.profilePictureUrl ?: userProfile.avatarUrl
                                if (!pic.isNullOrBlank()) {
                                    resolvedAvatar = pic
                                }
                                if (!userProfile.aboutStatus.isNullOrBlank()) {
                                    resolvedAbout = userProfile.aboutStatus
                                }
                                if (!userProfile.avatarColor.isNullOrBlank()) {
                                    resolvedAvatarColor = userProfile.avatarColor
                                }
                                if (userProfile.isProfileComplete) {
                                    isProfileDone = true
                                }
                            }
                        } catch (_: Exception) {}

                        state.savedProfileName = resolvedName
                        state.savedAvatarUrl = resolvedAvatar
                        state.savedAboutStatus = resolvedAbout
                        state.savedAvatarColor = resolvedAvatarColor

                        AuthSessionManager.updateProfileDetails(
                            context = context,
                            name = resolvedName,
                            avatarUrl = resolvedAvatar,
                            about = resolvedAbout,
                            avatarColor = resolvedAvatarColor
                        )
                        if (isProfileDone) {
                            AuthSessionManager.setProfileComplete(
                                context = context,
                                value = true,
                                displayName = resolvedName,
                                avatarUrl = resolvedAvatar,
                                avatarColor = resolvedAvatarColor
                            )
                        }
                        if (resolvedAbout.isNotBlank()) {
                            AuthSessionManager.setAboutStatus(context, resolvedAbout)
                        }

                        state.authErrorMessage = null
                        if (state.isPinResetting) {
                            state.navigateTo("CREATE_PIN")
                        } else {
                            state.navigateTo("PROFILE_INFO")
                        }
                    } else {
                        val statusCode = response.code()
                        val err = response.errorBody()?.string()
                        state.authErrorMessage = when (statusCode) {
                            502, 503, 504 -> "Server is waking up. Please wait a moment and try again."
                            in 500..599 -> "Server error ($statusCode). Please try again shortly."
                            429 -> "Too many attempts. Please wait before trying again."
                            else -> parseApiError(err, "Invalid or expired OTP code.")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                state.authErrorMessage = "Server took too long to respond. Please try again."
            } catch (e: java.io.IOException) {
                state.authErrorMessage = "Unable to connect to server. Please check your internet connection."
            } catch (e: Exception) {
                state.authErrorMessage = "Error: ${e.localizedMessage ?: "Unable to verify code"}"
            } finally {
                state.isVerifyingOtp = false
            }
        }
    }

    fun resendOtp(
        coroutineScope: CoroutineScope,
        state: AuthFlowState
    ) {
        if (state.isRequestingOtp) return
        state.isRequestingOtp = true
        state.authErrorMessage = null
        coroutineScope.launch {
            try {
                val sanitizedPhone = state.pendingPhoneNumber.trim().replace(" ", "").replace("-", "")
                if (!state.emailRecoveryTempToken.isNullOrBlank()) {
                    val response = NetworkClient.apiService.requestPhoneRecoveryOtp(
                        authHeader = "Bearer ${state.emailRecoveryTempToken}",
                        payload = RequestPhoneRecoveryOtpRequest(new_phone_number = sanitizedPhone)
                    )
                    if (!response.isSuccessful) {
                        val code = response.code()
                        val err = response.errorBody()?.string()
                        state.authErrorMessage = when (code) {
                            502, 503, 504 -> "Server is waking up. Please wait a moment and try again."
                            in 500..599 -> "Server error ($code). Please try again shortly."
                            else -> parseApiError(err, "Failed to resend SMS.")
                        }
                    }
                } else {
                    val response = NetworkClient.apiService.requestOtp(
                        OtpRequestPayload(phoneNumber = sanitizedPhone)
                    )
                    if (!response.isSuccessful) {
                        val code = response.code()
                        val err = response.errorBody()?.string()
                        state.authErrorMessage = when (code) {
                            502, 503, 504 -> "Server is waking up. Please wait a moment and try again."
                            in 500..599 -> "Server error ($code). Please try again shortly."
                            else -> parseApiError(err, "Failed to send SMS code.")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                state.authErrorMessage = "Resend request timed out. Please try again."
            } catch (e: java.io.IOException) {
                state.authErrorMessage = "Network error. Please check your internet connection."
            } catch (e: Exception) {
                state.authErrorMessage = "Failed to resend: ${e.localizedMessage}"
            } finally {
                state.isRequestingOtp = false
            }
        }
    }

    fun updateProfile(
        name: String,
        imageUri: Uri?,
        about: String,
        avatarColor: String? = null,
        context: Context,
        coroutineScope: CoroutineScope,
        state: AuthFlowState,
        onProceedToMainApp: () -> Unit
    ) {
        state.savedProfileName = name
        state.savedAboutStatus = about
        state.savedAvatarColor = avatarColor
        coroutineScope.launch {
            state.isUpdatingProfile = true
            state.authErrorMessage = null
            try {
                // 1. Update user profile name, about status, and avatar color
                runCatching {
                    NetworkClient.apiService.updateProfile(
                        UpdateUserProfileRequest(
                            fullName = name,
                            aboutStatus = about,
                            avatarColor = avatarColor
                        )
                    )
                }

                // 2. Upload avatar to Cloudflare R2 if selected
                var uploadedAvatarUrl: String? = null
                if (imageUri != null) {
                    try {
                        val part = AvatarUploadHelper.createAvatarMultipartPart(context, imageUri)
                        if (part != null) {
                            val avatarResp = NetworkClient.apiService.uploadAvatar(part)
                            if (avatarResp.isSuccessful) {
                                uploadedAvatarUrl = avatarResp.body()?.get("avatar_url") as? String
                            }
                        }
                    } catch (_: Exception) {
                        // Fail soft if avatar fails
                    }
                }

                val finalAvatarUrl = uploadedAvatarUrl ?: state.savedAvatarUrl ?: AuthSessionManager.avatarUrl.value
                state.savedAvatarUrl = finalAvatarUrl

                // Mark profile complete in session storage
                AuthSessionManager.setProfileComplete(
                    context = context,
                    value = true,
                    displayName = name,
                    avatarUrl = finalAvatarUrl,
                    avatarColor = avatarColor
                )
                AuthSessionManager.setAboutStatus(context, about)
                AuthSessionManager.updateProfileDetails(
                    context = context,
                    name = name,
                    avatarUrl = finalAvatarUrl,
                    about = about,
                    avatarColor = avatarColor
                )

                if (!AuthSessionManager.hasAppPin.value) {
                    state.navigateTo("CREATE_PIN")
                } else if (AuthSessionManager.getSavedPin(context) == null) {
                    state.navigateTo("SESSION_UNLOCK")
                } else {
                    onProceedToMainApp()
                }
            } catch (e: Exception) {
                val finalAvatarUrl = state.savedAvatarUrl ?: AuthSessionManager.avatarUrl.value
                AuthSessionManager.setProfileComplete(
                    context = context,
                    value = true,
                    displayName = name,
                    avatarUrl = finalAvatarUrl,
                    avatarColor = avatarColor
                )
                AuthSessionManager.setAboutStatus(context, about)
                AuthSessionManager.updateProfileDetails(
                    context = context,
                    name = name,
                    avatarUrl = finalAvatarUrl,
                    about = about,
                    avatarColor = avatarColor
                )

                if (!AuthSessionManager.hasAppPin.value) {
                    state.navigateTo("CREATE_PIN")
                } else if (AuthSessionManager.getSavedPin(context) == null) {
                    state.navigateTo("SESSION_UNLOCK")
                } else {
                    onProceedToMainApp()
                }
            } finally {
                state.isUpdatingProfile = false
            }
        }
    }

    fun createOrResetPin(
        pin: String,
        context: Context,
        coroutineScope: CoroutineScope,
        state: AuthFlowState,
        onProceedToMainApp: () -> Unit
    ) {
        coroutineScope.launch {
            state.isSettingPin = true
            state.authErrorMessage = null
            try {
                val authHeader = if (!state.emailRecoveryFinalToken.isNullOrBlank()) "Bearer ${state.emailRecoveryFinalToken}" else null
                val response = NetworkClient.apiService.setPin(
                    authHeader = authHeader,
                    payload = SetAppPinPayload(pin = pin)
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    val newAccessToken = body?.accessToken
                    if (!newAccessToken.isNullOrBlank()) {
                        AuthSessionManager.updateAccessToken(context, newAccessToken)
                    }
                    state.emailRecoveryFinalToken = null
                    AuthSessionManager.savePin(context, pin)
                    AuthSessionManager.setHasAppPin(context, true)
                    if (state.isPinResetting) {
                        AuthSessionManager.setProfileComplete(context, true)
                    }
                    state.isPinResetting = false
                    if (!AuthSessionManager.isBiometricsPrompted(context)) {
                        state.showBiometricPromptSheet = true
                    } else {
                        onProceedToMainApp()
                    }
                } else {
                    val err = response.errorBody()?.string()
                    val parsed = parseApiError(err, "Failed to set PIN.")
                    if (parsed.contains("already have a PIN", ignoreCase = true)) {
                        AuthSessionManager.savePin(context, pin)
                        AuthSessionManager.setHasAppPin(context, true)
                        if (state.isPinResetting) {
                            AuthSessionManager.setProfileComplete(context, true)
                        }
                        state.isPinResetting = false
                        if (!AuthSessionManager.isBiometricsPrompted(context)) {
                            state.showBiometricPromptSheet = true
                        } else {
                            onProceedToMainApp()
                        }
                    } else {
                        state.authErrorMessage = parsed
                    }
                }
            } catch (e: Exception) {
                AuthSessionManager.savePin(context, pin)
                AuthSessionManager.setHasAppPin(context, true)
                if (state.isPinResetting) {
                    AuthSessionManager.setProfileComplete(context, true)
                }
                state.isPinResetting = false
                if (!AuthSessionManager.isBiometricsPrompted(context)) {
                    state.showBiometricPromptSheet = true
                } else {
                    onProceedToMainApp()
                }
            } finally {
                state.isSettingPin = false
            }
        }
    }

    fun verifyUnlockPin(
        pin: String,
        context: Context,
        coroutineScope: CoroutineScope,
        state: AuthFlowState,
        onProceedToMainApp: () -> Unit
    ) {
        // 1. Check if user is currently locked out
        val remainingLockout = AuthSessionManager.getRemainingLockoutSeconds(context)
        if (remainingLockout > 0) {
            state.authErrorMessage = "Too many failed attempts. Try again in $remainingLockout seconds."
            return
        }

        // 2. 0ms Fast Local Hardware/Keystore Match
        val savedLocalPin = AuthSessionManager.getSavedPin(context)
        if (savedLocalPin != null) {
            if (savedLocalPin == pin) {
                AuthSessionManager.recordSuccessfulPin(context)
                state.authErrorMessage = null
                onProceedToMainApp()
                return
            } else {
                // Local PIN mismatch: record failed attempt & enforce policy without wiping session
                val attempts = AuthSessionManager.recordFailedAttempt(context)
                val remaining = AuthSessionManager.getRemainingLockoutSeconds(context)
                if (remaining > 0) {
                    state.authErrorMessage = "Too many failed attempts. Locked out for $remaining seconds."
                } else {
                    val remainingTries = maxOf(1, 5 - attempts)
                    state.authErrorMessage = "Incorrect PIN. $remainingTries attempts remaining."
                }
                return
            }
        }

        // 3. Fallback to backend verification if local PIN not yet cached
        coroutineScope.launch {
            state.isVerifyingPin = true
            state.authErrorMessage = null
            try {
                val response = NetworkClient.apiService.verifyPin(VerifyAppPinRequest(pin = pin))
                if (response.isSuccessful && response.body()?.valid == true) {
                    AuthSessionManager.savePin(context, pin)
                    AuthSessionManager.recordSuccessfulPin(context)
                    state.authErrorMessage = null
                    onProceedToMainApp()
                } else {
                    val err = response.errorBody()?.string()
                    val attempts = AuthSessionManager.recordFailedAttempt(context)
                    val remaining = AuthSessionManager.getRemainingLockoutSeconds(context)
                    if (remaining > 0) {
                        state.authErrorMessage = "Too many failed attempts. Locked out for $remaining seconds."
                    } else {
                        val remainingTries = maxOf(1, 5 - attempts)
                        state.authErrorMessage = parseApiError(err, "Incorrect PIN. $remainingTries attempts remaining.")
                    }
                }
            } catch (e: Exception) {
                state.authErrorMessage = "Connection error: ${e.localizedMessage ?: "Unable to verify PIN"}"
            } finally {
                state.isVerifyingPin = false
            }
        }
    }
}

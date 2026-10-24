package com.microbond.app.core.network.api.authenticationapi

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

// ── Auth & Session Models ───────────────────────────────────────────────────

data class OtpRequestPayload(
    @SerializedName("phone_number") val phoneNumber: String
)

data class OtpRequestResponse(
    val status: String,
    val message: String,
    @SerializedName("expires_in_seconds") val expiresInSeconds: Int = 300
)

data class OtpVerifyPayload(
    @SerializedName("phone_number") val phoneNumber: String,
    val code: String,
    @SerializedName("device_id") val deviceId: String? = null
)

data class OtpVerifyResponse(
    val status: String,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String = "bearer",
    @SerializedName("user_id") val userId: String?,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("is_new_user") val isNewUser: Boolean = false,
    @SerializedName("is_profile_complete") val isProfileComplete: Boolean = false,
    @SerializedName("has_app_pin") val hasAppPin: Boolean = false,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerializedName("avatar_color") val avatarColor: String? = null,
    @SerializedName("about_status") val aboutStatus: String? = null
)

// ── PIN Management Models ───────────────────────────────────────────────────

data class SetAppPinPayload(
    @SerializedName("pin") val pin: String
)

data class SetAppPinResponse(
    val status: String,
    val message: String? = null,
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("token_type") val tokenType: String? = null
)

data class VerifyAppPinRequest(
    val pin: String
)

data class VerifyAppPinResponse(
    val status: String,
    val message: String? = null,
    val valid: Boolean = false
)

data class ChangeAppPinRequest(
    @SerializedName("old_pin") val oldPin: String,
    @SerializedName("new_pin") val newPin: String
)

data class ChangeAppPinResponse(
    val status: String,
    val message: String? = null
)

// ── Forgot PIN Models ───────────────────────────────────────────────────────

data class RequestForgotPinOTPRequest(
    @SerializedName("phone_number") val phoneNumber: String
)

data class RequestForgotPinOTPResponse(
    val status: String,
    val message: String? = null,
    @SerializedName("expires_in_seconds") val expiresInSeconds: Int? = null
)

data class VerifyForgotPinOTPRequest(
    @SerializedName("phone_number") val phoneNumber: String,
    val code: String
)

data class VerifyForgotPinOTPResponse(
    val status: String,
    val message: String? = null,
    @SerializedName("recovery_token") val recoveryToken: String? = null
)

// ── Account Recovery Models (Lost Phone / SIM Swap) ─────────────────────────

data class RequestRecoveryOTPRequest(
    @SerializedName("old_phone_number") val oldPhoneNumber: String,
    @SerializedName("app_pin") val appPin: String,
    @SerializedName("new_phone_number") val newPhoneNumber: String
)

data class RequestRecoveryOTPResponse(
    val status: String,
    val message: String,
    @SerializedName("expires_in_seconds") val expiresInSeconds: Int = 300
)

data class VerifyRecoveryOTPRequest(
    @SerializedName("phone_number") val phoneNumber: String,
    val code: String
)

data class VerifyRecoveryOTPResponse(
    val status: String,
    val message: String,
    @SerializedName("temp_recovery_token") val tempRecoveryToken: String
)

// ── Authenticated Phone Change Models ───────────────────────────────────────

data class RequestChangePhoneOTPRequest(
    @SerializedName("new_phone_number") val newPhoneNumber: String
)

data class RequestChangePhoneOTPResponse(
    val status: String,
    val message: String,
    @SerializedName("expires_in_seconds") val expiresInSeconds: Int = 300
)

data class VerifyChangePhoneOTPRequest(
    @SerializedName("new_phone_number") val newPhoneNumber: String,
    val code: String
)

data class VerifyChangePhoneOTPResponse(
    val status: String,
    val message: String
)

// ── Backup Email Models ─────────────────────────────────────────────────────

data class LinkEmailRequest(
    val email: String
)

data class LinkEmailResponse(
    val status: String,
    val message: String
)

data class VerifyEmailRequest(
    val code: String,
    val email: String? = null
)

data class VerifyEmailResponse(
    val status: String,
    val message: String
)

data class BackupEmailData(
    val email: String? = null,
    @SerializedName("is_verified") val isVerified: Boolean = false
)

data class GetBackupEmailResponse(
    val status: String = "success",
    @SerializedName("has_backup_email") val hasBackupEmail: Boolean = false,
    val data: BackupEmailData? = null,
    val email: String? = null,
    @SerializedName("is_verified") val isVerified: Boolean = false
)

data class RequestEmailRecoveryCodeRequest(
    val email: String
)

data class RequestEmailRecoveryCodeResponse(
    val status: String,
    val message: String,
    val expires_in_seconds: Int? = null
)

data class VerifyEmailRecoveryCodeRequest(
    val email: String,
    val code: String
)

data class VerifyEmailRecoveryCodeResponse(
    val status: String,
    val message: String,
    val temp_recovery_token: String
)

data class RequestPhoneRecoveryOtpRequest(
    val new_phone_number: String
)

data class RequestPhoneRecoveryOtpResponse(
    val status: String,
    val message: String,
    val expires_in_seconds: Int? = null
)

data class ConfirmPhoneRecoveryRequest(
    val new_phone_number: String = "",
    val code: String
)

data class ConfirmPhoneRecoveryResponse(
    val status: String,
    val message: String,
    val recovery_token: String
)

// ── Session & KYC Models ────────────────────────────────────────────────────

data class LogoutRequest(
    @SerializedName("all_devices") val allDevices: Boolean = false
)

data class LogoutResponse(
    val status: String = "success",
    val message: String = "Logged out successfully"
)

data class NINSubmissionRequest(
    val nin: String,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("date_of_birth", alternate = ["dob"]) val dob: String? = null
)

data class NINVerificationResponse(
    val status: String = "success",
    val verified: Boolean = false,
    val message: String = "",
    val tier: Int = 1
)

// ── Auth API Service Interface ──────────────────────────────────────────────

interface AuthApiService {

    // Africa's Talking OTP Request
    @POST("api/v1/auth/at-otp/request-otp")
    suspend fun requestOtp(@Body payload: OtpRequestPayload): Response<OtpRequestResponse>

    // Africa's Talking OTP Verify
    @POST("api/v1/auth/at-otp/verify-otp")
    suspend fun verifyOtp(@Body payload: OtpVerifyPayload): Response<OtpVerifyResponse>

    // Set Initial App / Security PIN
    @POST("pin/set")
    suspend fun setPin(
        @Header("Authorization") authHeader: String? = null,
        @Body payload: SetAppPinPayload
    ): Response<SetAppPinResponse>

    // Verify User App PIN for Session Unlock
    @POST("pin/verify")
    suspend fun verifyPin(@Body payload: VerifyAppPinRequest): Response<VerifyAppPinResponse>

    // Change App PIN for an Authenticated User
    @PUT("pin/change")
    suspend fun changePin(@Body payload: ChangeAppPinRequest): Response<ChangeAppPinResponse>

    // Request OTP to reset forgotten PIN
    @POST("pin/forgot/request-otp")
    suspend fun requestForgotPinOtp(
        @Body payload: RequestForgotPinOTPRequest
    ): Response<RequestForgotPinOTPResponse>

    // Verify OTP to reset forgotten PIN
    @POST("pin/forgot/verify-otp")
    suspend fun verifyForgotPinOtp(
        @Body payload: VerifyForgotPinOTPRequest
    ): Response<VerifyForgotPinOTPResponse>

    // Account Recovery using PIN (Lost Phone / New Number)
    @POST("recovery/request-otp")
    suspend fun requestRecoveryOtp(
        @Body payload: RequestRecoveryOTPRequest
    ): Response<RequestRecoveryOTPResponse>

    // Verify OTP for Account Recovery
    @POST("recovery/verify-otp")
    suspend fun verifyRecoveryOtp(
        @Body payload: VerifyRecoveryOTPRequest
    ): Response<VerifyRecoveryOTPResponse>

    // Request OTP to Change Phone Number
    @POST("change-phone/request-otp")
    suspend fun requestChangePhoneOtp(
        @Body payload: RequestChangePhoneOTPRequest
    ): Response<RequestChangePhoneOTPResponse>

    // Confirm OTP & Update Phone Number
    @POST("change-phone/confirm-and-update")
    suspend fun verifyChangePhoneOtp(
        @Body payload: VerifyChangePhoneOTPRequest
    ): Response<VerifyChangePhoneOTPResponse>

    // Link Secondary Backup Email
    @POST("email/link")
    suspend fun linkBackupEmail(
        @Body payload: LinkEmailRequest
    ): Response<LinkEmailResponse>

    // Verify Secondary Backup Email
    @POST("email/verify")
    suspend fun verifyBackupEmail(
        @Body payload: VerifyEmailRequest
    ): Response<VerifyEmailResponse>

    // Retrieve Recovery Email for Authenticated User
    @GET("email/backup")
    suspend fun getBackupEmail(): Response<GetBackupEmailResponse>

    // Step 1: Request 6-digit recovery code to backup email
    @POST("recovery/email/request-code")
    suspend fun requestEmailRecoveryCode(
        @Body payload: RequestEmailRecoveryCodeRequest
    ): Response<RequestEmailRecoveryCodeResponse>

    // Step 2: Verify email recovery code and obtain temp_recovery_token
    @POST("recovery/email/verify-code")
    suspend fun verifyEmailRecoveryCode(
        @Body payload: VerifyEmailRecoveryCodeRequest
    ): Response<VerifyEmailRecoveryCodeResponse>

    // Step 3: Request SMS OTP to new phone number using temp_recovery_token
    @POST("recovery/email/request-phone-otp")
    suspend fun requestPhoneRecoveryOtp(
        @Header("Authorization") authHeader: String,
        @Body payload: RequestPhoneRecoveryOtpRequest
    ): Response<RequestPhoneRecoveryOtpResponse>

    // Step 4: Confirm SMS OTP and obtain recovery_token for PIN reset
    @POST("recovery/email/confirm-phone")
    suspend fun confirmPhoneRecovery(
        @Header("Authorization") authHeader: String,
        @Body payload: ConfirmPhoneRecoveryRequest
    ): Response<ConfirmPhoneRecoveryResponse>

    // Invalidate session and revoke tokens on Logout
    @POST("logout")
    suspend fun logout(
        @Body payload: LogoutRequest = LogoutRequest()
    ): Response<LogoutResponse>

    // Submit National ID (NIN) for KYC Tier 2 verification
    @POST("api/v1/kyc/nin/prembly/verify")
    suspend fun verifyNIN(
        @Body payload: NINSubmissionRequest
    ): Response<NINVerificationResponse>
}

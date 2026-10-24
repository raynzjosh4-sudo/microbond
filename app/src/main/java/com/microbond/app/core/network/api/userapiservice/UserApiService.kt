package com.microbond.app.core.network.api.userapiservice

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

// ── User Profile Models ─────────────────────────────────────────────────────

data class UserProfileData(
    val id: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("avatar_url", alternate = ["profile_picture_url"]) val avatarUrl: String? = null,
    @SerializedName("profile_picture_url", alternate = ["avatar_url"]) val profilePictureUrl: String? = null,
    @SerializedName("avatar_color") val avatarColor: String? = null,
    @SerializedName("about_status") val aboutStatus: String? = null,
    @SerializedName("is_profile_complete") val isProfileComplete: Boolean = false,
    @SerializedName("has_app_pin") val hasAppPin: Boolean = false,
    @SerializedName("has_verified_backup_email") val hasVerifiedBackupEmail: Boolean = false,
    @SerializedName("backup_email") val backupEmail: String? = null
)

data class UserProfileResponse(
    val status: String,
    val data: UserProfileData? = null
)

data class UpdateUserProfileRequest(
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerializedName("avatar_color") val avatarColor: String? = null,
    @SerializedName("about_status") val aboutStatus: String? = null
)

// ── Account Deletion Models ─────────────────────────────────────────────────

data class DeleteAccountRequest(
    val confirmation: String = "DELETE MY ACCOUNT",
    val reason: String? = null,
    @SerializedName("app_pin") val appPin: String? = null
)

data class DeleteAccountResponse(
    val status: String,
    val message: String
)

// ── Personal Privacy Settings Models ────────────────────────────────────────

data class UserPrivacySettings(
    val status: String = "success",
    @SerializedName("profile_photo_visibility") val profilePhotoVisibility: String = "EVERYONE",
    @SerializedName("about_visibility") val aboutVisibility: String = "EVERYONE",
    @SerializedName("group_add_permission") val groupAddPermission: String = "EVERYONE"
)

data class UpdatePrivacySettingsRequest(
    @SerializedName("profile_photo_visibility") val profilePhotoVisibility: String? = null,
    @SerializedName("about_visibility") val aboutVisibility: String? = null,
    @SerializedName("group_add_permission") val groupAddPermission: String? = null
)

// ── Linked Payout Accounts Models ───────────────────────────────────────────

data class LinkedAccountResponse(
    val id: String,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("account_name") val accountName: String?,
    @SerializedName("account_type") val accountType: String?,
    @SerializedName("verified_name") val verifiedName: String?,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("is_default") val isDefault: Boolean = false,
    @SerializedName("masked_number") val maskedNumber: String,
    @SerializedName("verification_type") val verificationType: String? = null
)

data class CreateLinkedAccountRequest(
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("account_type") val accountType: String? = null,
    @SerializedName("bank_code") val bankCode: String? = null,
    @SerializedName("is_default") val isDefault: Boolean = false,
    @SerializedName("app_pin") val appPin: String? = null
)

data class DeleteLinkedAccountResponse(
    val status: String,
    val message: String
)

// ── User API Service Interface ──────────────────────────────────────────────

interface UserApiService {

    // Get Full Authenticated User Profile (including phone number)
    @GET("api/v1/users/me")
    suspend fun getCurrentUserProfile(): Response<UserProfileResponse>

    // Update Current User Profile Details
    @PATCH("api/v1/users/me/profile")
    suspend fun updateProfile(@Body payload: UpdateUserProfileRequest): Response<Map<String, Any>>

    // Upload & Update User Avatar directly to Cloudflare R2
    @Multipart
    @POST("api/v1/users/me/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): Response<Map<String, Any>>

    // GDPR: Permanently Delete / Anonymize Authenticated User Account
    @HTTP(method = "DELETE", path = "api/v1/users/me", hasBody = true)
    suspend fun deleteAccount(
        @Body payload: DeleteAccountRequest
    ): Response<DeleteAccountResponse>

    // Retrieve Personal Privacy Settings
    @GET("api/v1/users/me/privacy")
    suspend fun getPrivacySettings(): Response<UserPrivacySettings>

    // Update Personal Privacy Settings
    @PATCH("api/v1/users/me/privacy")
    suspend fun updatePrivacySettings(
        @Body payload: UpdatePrivacySettingsRequest
    ): Response<UserPrivacySettings>

    // List Linked Payout Accounts
    @GET("api/v1/users/me/payout-destinations/")
    suspend fun getLinkedAccounts(): Response<List<LinkedAccountResponse>>

    // Add / Verify New Linked Payout Account
    @POST("api/v1/users/me/payout-destinations/")
    suspend fun addLinkedAccount(
        @Body payload: CreateLinkedAccountRequest
    ): Response<LinkedAccountResponse>

    // Unlink / Remove Payout Destination
    @DELETE("api/v1/users/me/payout-destinations/{destination_id}")
    suspend fun deleteLinkedAccount(
        @Path("destination_id") destinationId: String
    ): Response<DeleteLinkedAccountResponse>
}

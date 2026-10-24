package com.microbond.app.core.network.api

import com.google.gson.annotations.SerializedName
import com.microbond.app.core.network.DiscoverableContactGroup
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// ── Contact & Group Synchronization Models ──────────────────────────────────

data class ContactSyncRequest(
    @SerializedName("phone_numbers") val phoneNumbers: List<String>
)

data class SharedGroupInfo(
    @SerializedName("group_id") val groupId: String,
    @SerializedName("group_name") val groupName: String
)

data class SyncedContactUser(
    @SerializedName("user_id") val userId: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("is_on_microbond") val isOnMicrobond: Boolean = true,
    @SerializedName("shared_groups") val sharedGroups: List<SharedGroupInfo> = emptyList()
)

data class MatchedContact(
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("phone_number") val phoneNumber: String = "",
    @SerializedName("profile_name") val profileName: String = "",
    @SerializedName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerializedName("avatar_color") val avatarColor: String? = null,
    @SerializedName("about_status") val aboutStatus: String? = null
)

data class ContactSyncResponse(
    val status: String = "success",
    @SerializedName("contacts_on_microbond") val contactsOnMicrobond: List<SyncedContactUser> = emptyList(),
    @SerializedName("unregistered_phones") val unregisteredPhones: List<String> = emptyList()
)

// ── Contacts API Service Interface ──────────────────────────────────────────

interface ContactsApiService {

    // Sync Device Contacts to Identify Registered Users and Shared Groups
    @POST("api/v1/contacts/sync")
    suspend fun syncContacts(
        @Body payload: ContactSyncRequest
    ): Response<List<MatchedContact>>

    // Discover groups where phone contacts are members
    @POST("api/v1/contacts/discover-groups")
    suspend fun discoverGroupsByContacts(
        @Body payload: ContactSyncRequest
    ): Response<List<DiscoverableContactGroup>>
}

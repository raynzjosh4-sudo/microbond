package com.microbond.app.feature_you.ui.privacy

import android.content.Context
import androidx.core.content.edit
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.UpdatePrivacySettingsRequest
import com.microbond.app.core.network.UserPrivacySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages user personal privacy preferences with optimistic local caching and
 * backend synchronization via GET and PATCH /api/v1/users/me/privacy.
 */
object UserPrivacyManager {
    private const val PREFS_NAME = "microbond_privacy_prefs"
    private const val KEY_PROFILE_PHOTO = "profile_photo_visibility"
    private const val KEY_ABOUT = "about_visibility"
    private const val KEY_GROUP_ADD = "group_add_permission"

    const val OPTION_EVERYONE = "EVERYONE"
    const val OPTION_MY_CONTACTS = "MY_CONTACTS"
    const val OPTION_NOBODY = "NOBODY"

    private val _profilePhotoVisibility = MutableStateFlow(OPTION_EVERYONE)
    val profilePhotoVisibility: StateFlow<String> = _profilePhotoVisibility

    private val _aboutVisibility = MutableStateFlow(OPTION_EVERYONE)
    val aboutVisibility: StateFlow<String> = _aboutVisibility

    private val _groupAddPermission = MutableStateFlow(OPTION_EVERYONE)
    val groupAddPermission: StateFlow<String> = _groupAddPermission

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoaded = MutableStateFlow(false)

    fun toDisplayLabel(value: String): String {
        return when (value.trim().uppercase().replace(" ", "_")) {
            OPTION_EVERYONE -> "Everyone"
            OPTION_MY_CONTACTS -> "My contacts"
            OPTION_NOBODY -> "Nobody"
            else -> value
        }
    }

    fun toOptionValue(label: String): String {
        return when (label.trim()) {
            "Everyone" -> OPTION_EVERYONE
            "My contacts" -> OPTION_MY_CONTACTS
            "Nobody" -> OPTION_NOBODY
            else -> label.trim().uppercase().replace(" ", "_")
        }
    }

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _profilePhotoVisibility.value = prefs.getString(KEY_PROFILE_PHOTO, OPTION_EVERYONE) ?: OPTION_EVERYONE
        _aboutVisibility.value = prefs.getString(KEY_ABOUT, OPTION_EVERYONE) ?: OPTION_EVERYONE
        _groupAddPermission.value = prefs.getString(KEY_GROUP_ADD, OPTION_EVERYONE) ?: OPTION_EVERYONE
        _isLoaded.value = true
    }

    suspend fun refreshPrivacySettings(context: Context): Boolean {
        if (!_isLoaded.value) {
            load(context)
        }
        _isLoading.value = true
        return try {
            val response = NetworkClient.apiService.getPrivacySettings()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                saveLocal(context, data.profilePhotoVisibility, data.aboutVisibility, data.groupAddPermission)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun updateProfilePhoto(context: Context, newOption: String): Boolean {
        val previous = _profilePhotoVisibility.value
        val normalized = toOptionValue(newOption)
        _profilePhotoVisibility.value = normalized
        saveLocal(context, profilePhoto = normalized)

        return try {
            val response = NetworkClient.apiService.updatePrivacySettings(
                UpdatePrivacySettingsRequest(profilePhotoVisibility = normalized)
            )
            if (response.isSuccessful) {
                true
            } else {
                _profilePhotoVisibility.value = previous
                saveLocal(context, profilePhoto = previous)
                false
            }
        } catch (_: Exception) {
            _profilePhotoVisibility.value = previous
            saveLocal(context, profilePhoto = previous)
            false
        }
    }

    suspend fun updateAbout(context: Context, newOption: String): Boolean {
        val previous = _aboutVisibility.value
        val normalized = toOptionValue(newOption)
        _aboutVisibility.value = normalized
        saveLocal(context, about = normalized)

        return try {
            val response = NetworkClient.apiService.updatePrivacySettings(
                UpdatePrivacySettingsRequest(aboutVisibility = normalized)
            )
            if (response.isSuccessful) {
                true
            } else {
                _aboutVisibility.value = previous
                saveLocal(context, about = previous)
                false
            }
        } catch (_: Exception) {
            _aboutVisibility.value = previous
            saveLocal(context, about = previous)
            false
        }
    }

    suspend fun updateGroupAddPermission(context: Context, newOption: String): Boolean {
        val previous = _groupAddPermission.value
        val normalized = toOptionValue(newOption)
        _groupAddPermission.value = normalized
        saveLocal(context, groupAdd = normalized)

        return try {
            val response = NetworkClient.apiService.updatePrivacySettings(
                UpdatePrivacySettingsRequest(groupAddPermission = normalized)
            )
            if (response.isSuccessful) {
                true
            } else {
                _groupAddPermission.value = previous
                saveLocal(context, groupAdd = previous)
                false
            }
        } catch (_: Exception) {
            _groupAddPermission.value = previous
            saveLocal(context, groupAdd = previous)
            false
        }
    }

    private fun saveLocal(
        context: Context,
        profilePhoto: String? = null,
        about: String? = null,
        groupAdd: String? = null
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            if (profilePhoto != null) {
                putString(KEY_PROFILE_PHOTO, profilePhoto)
                _profilePhotoVisibility.value = profilePhoto
            }
            if (about != null) {
                putString(KEY_ABOUT, about)
                _aboutVisibility.value = about
            }
            if (groupAdd != null) {
                putString(KEY_GROUP_ADD, groupAdd)
                _groupAddPermission.value = groupAdd
            }
        }
    }
}

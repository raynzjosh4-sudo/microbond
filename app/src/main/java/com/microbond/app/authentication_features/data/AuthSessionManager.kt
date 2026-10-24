package com.microbond.app.authentication_features.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages user authentication state, stored JWT tokens, PIN setup state, and login sessions.
 */
object AuthSessionManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_PHONE_NUMBER = "phone_number"
    private const val KEY_IS_AUTHENTICATED = "is_authenticated"
    private const val KEY_HAS_APP_PIN = "has_app_pin"
    private const val KEY_SAVED_PIN = "saved_app_pin"
    private const val KEY_IS_PROFILE_COMPLETE = "is_profile_complete"
    private const val KEY_DISPLAY_NAME = "display_name"
    private const val KEY_AVATAR_URL = "avatar_url"
    private const val KEY_AVATAR_COLOR = "avatar_color"
    private const val KEY_ABOUT_STATUS = "about_status"
    private const val KEY_FAILED_ATTEMPTS = "failed_pin_attempts"
    private const val KEY_LOCKOUT_UNTIL = "lockout_until_timestamp"
    private const val KEY_BIOMETRICS_ENABLED = "biometrics_enabled"
    private const val KEY_BIOMETRICS_PROMPTED = "biometrics_prompted"

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

    private val _hasAppPin = MutableStateFlow(false)
    val hasAppPin: StateFlow<Boolean> = _hasAppPin

    private val _isProfileComplete = MutableStateFlow(false)
    val isProfileComplete: StateFlow<Boolean> = _isProfileComplete

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl

    private val _avatarColor = MutableStateFlow<String?>(null)
    val avatarColor: StateFlow<String?> = _avatarColor

    private val _aboutStatus = MutableStateFlow<String?>("Saving for the future - Available")
    val aboutStatus: StateFlow<String?> = _aboutStatus

    private val _phoneNumber = MutableStateFlow<String?>(null)
    val phoneNumber: StateFlow<String?> = _phoneNumber

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId
    val currentUserId: String? get() = _userId.value

    private val _savedPin = MutableStateFlow<String?>(null)
    val savedPin: StateFlow<String?> = _savedPin

    private val _isBiometricsEnabled = MutableStateFlow(false)
    val isBiometricsEnabled: StateFlow<Boolean> = _isBiometricsEnabled

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = prefs.getString(KEY_ACCESS_TOKEN, null)
        val authed = prefs.getBoolean(KEY_IS_AUTHENTICATED, false)
        val pinSet = prefs.getBoolean(KEY_HAS_APP_PIN, false)
        val profileDone = prefs.getBoolean(KEY_IS_PROFILE_COMPLETE, false)
        val name = prefs.getString(KEY_DISPLAY_NAME, null)
        val avatar = prefs.getString(KEY_AVATAR_URL, null)
        val color = prefs.getString(KEY_AVATAR_COLOR, null)
        val about = prefs.getString(KEY_ABOUT_STATUS, null)
        val phone = prefs.getString(KEY_PHONE_NUMBER, null)
        val pin = prefs.getString(KEY_SAVED_PIN, null)
        val bioEnabled = prefs.getBoolean(KEY_BIOMETRICS_ENABLED, false)
        val uId = prefs.getString(KEY_USER_ID, null)
        _userId.value = uId
        _accessToken.value = token
        _hasAppPin.value = pinSet
        _isProfileComplete.value = profileDone
        _displayName.value = name
        _avatarUrl.value = avatar
        _avatarColor.value = color
        _aboutStatus.value = if (!about.isNullOrBlank()) about else "Saving for the future - Available"
        _phoneNumber.value = phone
        _savedPin.value = pin
        _isBiometricsEnabled.value = bioEnabled
        // For production, check both flag and token
        _isAuthenticated.value = authed && !token.isNullOrBlank()
    }

    fun saveSession(
        context: Context,
        token: String,
        userId: String?,
        phoneNumber: String?,
        hasAppPin: Boolean = false,
        isProfileComplete: Boolean = false,
        displayName: String? = null,
        avatarUrl: String? = null,
        avatarColor: String? = null
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val resolvedProfileComplete = isProfileComplete || prefs.getBoolean(KEY_IS_PROFILE_COMPLETE, false)
        val resolvedDisplayName = if (!displayName.isNullOrBlank()) displayName else prefs.getString(KEY_DISPLAY_NAME, null)
        val resolvedAvatarUrl = if (!avatarUrl.isNullOrBlank()) avatarUrl else prefs.getString(KEY_AVATAR_URL, null)
        val resolvedAvatarColor = if (!avatarColor.isNullOrBlank()) avatarColor else prefs.getString(KEY_AVATAR_COLOR, null)
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, token)
            if (!userId.isNullOrBlank()) putString(KEY_USER_ID, userId)
            if (!phoneNumber.isNullOrBlank()) putString(KEY_PHONE_NUMBER, phoneNumber)
            putBoolean(KEY_IS_AUTHENTICATED, true)
            putBoolean(KEY_HAS_APP_PIN, hasAppPin)
            putBoolean(KEY_IS_PROFILE_COMPLETE, resolvedProfileComplete)
            if (!resolvedDisplayName.isNullOrBlank()) putString(KEY_DISPLAY_NAME, resolvedDisplayName)
            if (!resolvedAvatarUrl.isNullOrBlank()) putString(KEY_AVATAR_URL, resolvedAvatarUrl)
            if (!resolvedAvatarColor.isNullOrBlank()) putString(KEY_AVATAR_COLOR, resolvedAvatarColor)
        }
        _userId.value = userId
        _accessToken.value = token
        _hasAppPin.value = hasAppPin
        _isProfileComplete.value = resolvedProfileComplete
        _displayName.value = resolvedDisplayName
        _avatarUrl.value = resolvedAvatarUrl
        _avatarColor.value = resolvedAvatarColor
        if (!phoneNumber.isNullOrBlank()) _phoneNumber.value = phoneNumber
        _isAuthenticated.value = true
    }

    fun setProfileComplete(
        context: Context,
        value: Boolean,
        displayName: String? = null,
        avatarUrl: String? = null,
        avatarColor: String? = null
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putBoolean(KEY_IS_PROFILE_COMPLETE, value)
            if (!displayName.isNullOrBlank()) putString(KEY_DISPLAY_NAME, displayName)
            if (!avatarUrl.isNullOrBlank()) putString(KEY_AVATAR_URL, avatarUrl)
            if (!avatarColor.isNullOrBlank()) putString(KEY_AVATAR_COLOR, avatarColor)
        }
        _isProfileComplete.value = value
        if (!displayName.isNullOrBlank()) _displayName.value = displayName
        if (!avatarUrl.isNullOrBlank()) _avatarUrl.value = avatarUrl
        if (!avatarColor.isNullOrBlank()) _avatarColor.value = avatarColor
    }

    fun setPhoneNumber(context: Context, phone: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_PHONE_NUMBER, phone)
        }
        _phoneNumber.value = phone
    }

    fun setAboutStatus(context: Context, status: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_ABOUT_STATUS, status)
        }
        _aboutStatus.value = status
    }

    fun updateProfileDetails(
        context: Context,
        name: String? = null,
        avatarUrl: String? = null,
        about: String? = null,
        phone: String? = null,
        avatarColor: String? = null
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            if (!name.isNullOrBlank()) putString(KEY_DISPLAY_NAME, name)
            if (!avatarUrl.isNullOrBlank()) putString(KEY_AVATAR_URL, avatarUrl)
            if (!avatarColor.isNullOrBlank()) putString(KEY_AVATAR_COLOR, avatarColor)
            if (!about.isNullOrBlank()) putString(KEY_ABOUT_STATUS, about)
            if (!phone.isNullOrBlank()) putString(KEY_PHONE_NUMBER, phone)
        }
        if (!name.isNullOrBlank()) _displayName.value = name
        if (!avatarUrl.isNullOrBlank()) _avatarUrl.value = avatarUrl
        if (!avatarColor.isNullOrBlank()) _avatarColor.value = avatarColor
        if (!about.isNullOrBlank()) _aboutStatus.value = about
        if (!phone.isNullOrBlank()) _phoneNumber.value = phone
    }

    fun savePin(context: Context, pin: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_SAVED_PIN, pin)
            putBoolean(KEY_HAS_APP_PIN, true)
        }
        _savedPin.value = pin
        _hasAppPin.value = true
    }

    fun getSavedPin(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SAVED_PIN, null)
    }

    fun setHasAppPin(context: Context, value: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putBoolean(KEY_HAS_APP_PIN, value)
        }
        _hasAppPin.value = value
    }

    fun updateAccessToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, token)
            putBoolean(KEY_IS_AUTHENTICATED, true)
        }
        _accessToken.value = token
        _isAuthenticated.value = true
    }

    fun isBiometricsPrompted(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_BIOMETRICS_PROMPTED, false)
    }

    fun setBiometricsPrompted(context: Context, prompted: Boolean = true) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_BIOMETRICS_PROMPTED, prompted) }
    }

    fun setBiometricsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putBoolean(KEY_BIOMETRICS_ENABLED, enabled)
            putBoolean(KEY_BIOMETRICS_PROMPTED, true)
        }
        _isBiometricsEnabled.value = enabled
    }

    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { clear() }
        _userId.value = null
        _accessToken.value = null
        _hasAppPin.value = false
        _isProfileComplete.value = false
        _displayName.value = null
        _avatarUrl.value = null
        _savedPin.value = null
        _isBiometricsEnabled.value = false
        _isAuthenticated.value = false
    }

    fun setAuthenticated(value: Boolean) {
        _isAuthenticated.value = value
    }

    /**
     * Records a failed PIN entry attempt.
     * - At 3 failed attempts: activates a 30-second local lockout.
     * - At 5+ failed attempts: activates a 60-second local lockout.
     * Returns the current failed attempts count.
     */
    fun recordFailedAttempt(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentAttempts = prefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
        val editor = prefs.edit()

        editor.putInt(KEY_FAILED_ATTEMPTS, currentAttempts)
        if (currentAttempts >= 3) {
            val lockoutMs = if (currentAttempts >= 5) 60_000L else 30_000L
            val lockoutUntil = System.currentTimeMillis() + lockoutMs
            editor.putLong(KEY_LOCKOUT_UNTIL, lockoutUntil)
        }
        editor.apply()
        return currentAttempts
    }

    /** Resets the failed attempt counter and lockout on successful PIN unlock. */
    fun recordSuccessfulPin(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            remove(KEY_FAILED_ATTEMPTS)
            remove(KEY_LOCKOUT_UNTIL)
        }
    }

    /** Returns the remaining lockout seconds, or 0 if not locked out. */
    fun getRemainingLockoutSeconds(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        val now = System.currentTimeMillis()
        return if (lockoutUntil > now) {
            ((lockoutUntil - now) / 1000L).toInt() + 1
        } else {
            0
        }
    }
}

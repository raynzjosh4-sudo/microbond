package com.microbond.app.models.usermodel

data class AccountSettings(
    val securityNotificationsEnabled: Boolean = true,
    val pin: String = ""
)

data class PrivacySettings(
    val profilePhotoVisibility: String = "My contacts",
    val aboutVisibility: String = "Everyone",
    val groupsVisibility: String = "Everyone",
    val appLockEnabled: Boolean = false
)

data class LinkedAccountsSettings(
    val linkedAccountIds: List<String> = emptyList(),
    val defaultDepositAccountId: String? = null,
    val defaultPayoutAccountId: String? = null
)

data class ThreadsSettings(
    val theme: String = "System default",
    val wallpaperUrl: String? = null,
    val keepThreadHistory: Boolean = true
)

data class NotificationSettings(
    val conversationTones: Boolean = true,
    // Messages
    val messageNotificationTone: String = "Default (ringtone)",
    val messageVibrate: String = "Default",
    val messageReactionNotifications: Boolean = true,
    val messageHighPriorityNotifications: Boolean = true,
    // Groups
    val groupNotificationTone: String = "Default (ringtone)",
    val groupVibrate: String = "Default",
    val groupReactionNotifications: Boolean = true,
    val groupHighPriorityNotifications: Boolean = true,
    // Transactions & Cycles
    val depositReminders: Boolean = true,
    val payoutAlerts: Boolean = true
)

data class StorageSettings(
    val useLessDataSync: Boolean = false,
    val keepOfflineRecords: Boolean = true,
    val autoDownloadMobileData: String = "Photos",
    val autoDownloadWifi: String = "All media",
    val autoDownloadRoaming: String = "No media",
    val mediaUploadQuality: String = "Auto (recommended)"
)

data class UserSettingsModel(
    val accountSettings: AccountSettings = AccountSettings(),
    val privacySettings: PrivacySettings = PrivacySettings(),
    val linkedAccountsSettings: LinkedAccountsSettings = LinkedAccountsSettings(),
    val threadsSettings: ThreadsSettings = ThreadsSettings(),
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val storageSettings: StorageSettings = StorageSettings()
)

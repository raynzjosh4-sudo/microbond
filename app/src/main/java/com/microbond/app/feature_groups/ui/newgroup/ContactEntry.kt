package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.ui.graphics.Color
import com.microbond.app.core.ui.theme.MicrobondDefaultContactAvatar

/**
 * Shared data model for a contact entry displayed in the new-group flow.
 *
 * @param isOnMicrobond  true = user has Microbond; false = phone-only contact (shows Invite button)
 */
data class ContactEntry(
    val name: String,
    val subtitle: String = "",
    val avatarColor: Color = MicrobondDefaultContactAvatar,
    val isOnMicrobond: Boolean = true,
    val phoneNumber: String = "",
    val userId: String? = null
)

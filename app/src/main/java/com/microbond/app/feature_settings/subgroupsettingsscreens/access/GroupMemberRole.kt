package com.microbond.app.feature_settings.subgroupsettingsscreens.access

/** The viewer's role inside a group. */
enum class GroupMemberRole(val label: String) {
    MEMBER("Member"),
    ADMIN("Admin");

    val isAdmin: Boolean get() = this == ADMIN

    companion object {
        /** Maps a free-text role (e.g. `GroupMemberSummary.role`) to a [GroupMemberRole]. */
        fun fromLabel(label: String?): GroupMemberRole =
            values().firstOrNull { it.label.equals(label?.trim(), ignoreCase = true) } ?: MEMBER
    }
}

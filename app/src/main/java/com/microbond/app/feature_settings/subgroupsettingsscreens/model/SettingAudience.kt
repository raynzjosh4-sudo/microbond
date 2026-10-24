package com.microbond.app.feature_settings.subgroupsettingsscreens.model

/** Who is allowed to see a group setting. */
enum class SettingAudience {
    /** Every member sees and controls this setting (personal preferences). */
    EVERYONE,

    /** Only admins can see and change this setting. */
    ADMIN_ONLY,

    /** Admins control this setting and may choose to let members view it (read-only). */
    ADMIN_SHAREABLE
}

package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** Liquidity & reminders page — admin controlled, same for every group type. */
internal fun liquiditySettings(): List<GroupSetting> = listOf(
    GroupSetting("Contribution reminder schedule", value = "2 days before").adminShareable(),
    GroupSetting("Auto-debit consent", "Permit automatic collection only from members who opt in.", toggle = true).adminOnly(),
    GroupSetting("Emergency contact", "Set a private contact for urgent payment issues.").adminOnly(),
    GroupSetting("Liquidity buffer coverage", value = "10%").adminShareable(),
    GroupSetting("Late-fee escalation", toggle = true, initiallyEnabled = true).adminShareable(),
    GroupSetting("SMS / call reminders", toggle = true, initiallyEnabled = true).adminOnly()
)

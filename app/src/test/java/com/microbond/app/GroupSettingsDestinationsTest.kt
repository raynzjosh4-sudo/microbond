package com.microbond.app

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsPage
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.groupSettingsDestinations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GroupSettingsDestinationsTest {
    @Test
    fun everyGroupTypeHasNineSettingsDestinations() {
        val destinationsByType = SavingsGroupType.values().associateWith {
            groupSettingsDestinations(it)
        }

        assertEquals(SavingsGroupType.values().toSet(), destinationsByType.keys)
        assertTrue(destinationsByType.values.all { it.size == 9 })
        assertTrue(destinationsByType.values.flatten().size == 81)
        assertTrue(destinationsByType.values.all { destinations ->
            destinations.map { it.page }.toSet() == GroupSettingsPage.values().toSet()
        })
    }

    @Test
    fun typeSpecificSettingsTitlesReflectSelectedGroupType() {
        val merryGoRoundTitles = groupSettingsDestinations(SavingsGroupType.MERRY_GO_ROUND)
            .map { it.title }
        val utilityTitles = groupSettingsDestinations(SavingsGroupType.BILL_SPLIT)
            .map { it.title }

        assertTrue(merryGoRoundTitles.contains("Payout rules"))
        assertTrue(merryGoRoundTitles.contains("Transaction messages"))
        assertTrue(utilityTitles.contains("Bill administration"))
        assertTrue(utilityTitles.contains("Bill payment messages"))
        assertTrue(merryGoRoundTitles != utilityTitles)
    }
}

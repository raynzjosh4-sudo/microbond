package com.microbond.app.core.data_classes_and_enums

import com.microbond.app.feature_groups.ui.newgroup.ContactEntry

/**
 * File holding all the interfaces for the application.
 */

sealed interface NewGroupFlowStep {
    /** Step 0 – pick a group type and optionally pre-select contacts. */
    object SelectGroupType : NewGroupFlowStep
    /** Step 1 – search and select participants for [groupType]. */
    data class AddParticipants(val groupType: SavingsGroupType) : NewGroupFlowStep
    /** Step 2 – name the group, set icon and settings. */
    data class NewGroup(val participants: List<ContactEntry>, val groupType: SavingsGroupType, val groupIconUri: String? = null) : NewGroupFlowStep
    data class Permissions(val participants: List<ContactEntry>, val groupType: SavingsGroupType) : NewGroupFlowStep
    data class FinancialSettings(val participants: List<ContactEntry>, val groupType: SavingsGroupType) : NewGroupFlowStep
    data class GalleryPicker(val participants: List<ContactEntry>, val groupType: SavingsGroupType) : NewGroupFlowStep
    data class ImageCropper(val participants: List<ContactEntry>, val groupType: SavingsGroupType, val imageUri: String) : NewGroupFlowStep
}

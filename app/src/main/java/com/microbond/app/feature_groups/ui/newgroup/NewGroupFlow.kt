package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.microbond.app.core.data_classes_and_enums.NewGroupFlowStep
import com.microbond.app.core.network.sync.ContactSyncEngine
import com.microbond.app.core.ui.theme.microbondAvatarPalette
import com.microbond.app.feature_groups.data.TrustConnection

/**
 * Coordinator composable that owns the multi-step new-group flow:
 *
 *   0. [SelectGroupTypeScreen]  — pick a group type; browse & pre-select contacts
 *   1. [AddParticipantsScreen]  — search & confirm participants
 *   2. [NewGroupScreen]         — name, icon, settings
 *      ↳ [GroupPermissionsScreen]
 *
 * @param trustedConnections  Persisted trust network contacts
 * @param onBack              Exit the entire flow (back from step 0)
 * @param onGroupCreated      Called with the final [NewGroupSetup] on success
 */
@Composable
fun NewGroupFlow(
    trustedConnections: List<TrustConnection> = emptyList(),
    onBack: () -> Unit,
    onGroupCreated: (NewGroupSetup) -> Unit
) {
    // ── Contacts data (no sampleChats — replaced with richer dummy set) ────────
    val avatarColors = microbondAvatarPalette

    val context = LocalContext.current
    val syncedOnMicrobond by ContactSyncEngine.onMicrobondContacts.collectAsState()
    val syncedOffApp by ContactSyncEngine.offAppContacts.collectAsState()

    LaunchedEffect(Unit) {
        ContactSyncEngine.syncContacts(context)
    }

    val defaultOnMicrobond = remember {
        listOf(
            ContactEntry("Reach out to us now (You)", "Message yourself",            avatarColors[0], isOnMicrobond = true),
            ContactEntry("Doreen Nakato",              "Hey there! I am using Microbond", avatarColors[1], isOnMicrobond = true),
            ContactEntry("Patrick Ssemanda",           "+256 782 334 210",            avatarColors[2], isOnMicrobond = true),
            ContactEntry("Gloria Auma",                "+256 753 001 887",            avatarColors[3], isOnMicrobond = true),
            ContactEntry("Brian Okello",               "Available",                   avatarColors[4], isOnMicrobond = true),
            ContactEntry("Faith Namukasa",             "+256 703 445 601",            avatarColors[5], isOnMicrobond = true),
            ContactEntry("Samuel Opio",                "At work",                     avatarColors[6], isOnMicrobond = true),
            ContactEntry("Lydia Acen",                 "+256 772 889 003",            avatarColors[7], isOnMicrobond = true),
        )
    }

    val defaultOffApp = remember {
        listOf(
            ContactEntry("+256 787 222 445", "",                 avatarColors[8], isOnMicrobond = false),
            ContactEntry("+256 700 888 412", "",                 avatarColors[0], isOnMicrobond = false),
            ContactEntry("+256 756 128 503", "Mercy",           avatarColors[2], isOnMicrobond = false),
            ContactEntry("+256 756 783 500", "Uncle James",     avatarColors[5], isOnMicrobond = false),
            ContactEntry("+256 702 001 344", "Mum",             avatarColors[3], isOnMicrobond = false),
        )
    }

    /** Contacts who already have a Microbond account. */
    val onMicrobondContacts = remember(syncedOnMicrobond) {
        if (syncedOnMicrobond.isNotEmpty()) syncedOnMicrobond else defaultOnMicrobond
    }

    /** Phone-book contacts who do NOT have Microbond yet — show "Invite" button. */
    val offAppContacts = remember(syncedOffApp) {
        if (syncedOffApp.isNotEmpty()) syncedOffApp else defaultOffApp
    }

    // ── Flow step state ────────────────────────────────────────────────────────
    var step by remember { mutableStateOf<NewGroupFlowStep>(NewGroupFlowStep.SelectGroupType) }

    androidx.activity.compose.BackHandler(enabled = step != NewGroupFlowStep.SelectGroupType) {
        when (val current = step) {
            is NewGroupFlowStep.AddParticipants -> step = NewGroupFlowStep.SelectGroupType
            is NewGroupFlowStep.NewGroup -> step = NewGroupFlowStep.AddParticipants(current.groupType)
            is NewGroupFlowStep.Permissions -> step = NewGroupFlowStep.NewGroup(current.participants, current.groupType)
            is NewGroupFlowStep.FinancialSettings -> step = NewGroupFlowStep.NewGroup(current.participants, current.groupType)
            is NewGroupFlowStep.GalleryPicker -> step = NewGroupFlowStep.NewGroup(current.participants, current.groupType)
            is NewGroupFlowStep.ImageCropper -> step = NewGroupFlowStep.GalleryPicker(current.participants, current.groupType)
            else -> onBack()
        }
    }

    when (val current = step) {

        is NewGroupFlowStep.SelectGroupType -> {
            SelectGroupTypeScreen(
                onBack = onBack,
                onGroupTypeSelected = { groupType ->
                    step = NewGroupFlowStep.AddParticipants(groupType)
                }
            )
        }

        is NewGroupFlowStep.AddParticipants -> {
            AddParticipantsScreen(
                contacts = onMicrobondContacts + offAppContacts,
                groupType = current.groupType,
                onBack = { step = NewGroupFlowStep.SelectGroupType },
                onNext = { selected ->
                    step = NewGroupFlowStep.NewGroup(selected, current.groupType)
                }
            )
        }

        is NewGroupFlowStep.NewGroup -> {
            NewGroupScreen(
                participants = current.participants,
                groupIconUri = current.groupIconUri,
                onBack = { step = NewGroupFlowStep.AddParticipants(current.groupType) },
                onGroupCreated = onGroupCreated,
                onFinancialSettings = {
                    step = NewGroupFlowStep.FinancialSettings(current.participants, current.groupType)
                },
                onGroupPermissions = {
                    step = NewGroupFlowStep.Permissions(current.participants, current.groupType)
                },
                onOpenGallery = {
                    step = NewGroupFlowStep.GalleryPicker(current.participants, current.groupType)
                },
                groupType = current.groupType
            )
        }

        is NewGroupFlowStep.Permissions -> {
            GroupPermissionsScreen(
                groupType = current.groupType,
                onBack = {
                    // Return to NewGroup
                    step = NewGroupFlowStep.NewGroup(current.participants, current.groupType)
                }
            )
        }

        is NewGroupFlowStep.FinancialSettings -> {
            GroupFinancialSettingsScreen(
                groupType = current.groupType,
                onBack = {
                    step = NewGroupFlowStep.NewGroup(current.participants, current.groupType)
                }
            )
        }

        is NewGroupFlowStep.GalleryPicker -> {
            GalleryPickerScreen(
                onBack = { step = NewGroupFlowStep.NewGroup(current.participants, current.groupType) },
                onImageSelected = { uri ->
                    step = NewGroupFlowStep.ImageCropper(current.participants, current.groupType, uri)
                }
            )
        }

        is NewGroupFlowStep.ImageCropper -> {
            ImageCropperScreen(
                imageUri = current.imageUri,
                onCancel = { step = NewGroupFlowStep.GalleryPicker(current.participants, current.groupType) },
                onDone = { croppedUri ->
                    step = NewGroupFlowStep.NewGroup(current.participants, current.groupType, groupIconUri = croppedUri)
                }
            )
        }
    }
}

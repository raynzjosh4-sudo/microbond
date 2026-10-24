package com.microbond.app.screens.threadscreen.threadscreenwidgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.microbond.app.core.components.groupintrocard.rotating.RotatingGroupDetailsSheet
import com.microbond.app.core.components.groupintrocard.rotating.RotatingGroupPreviewUiState
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.components.threadbubbles.bubblesheets.DepositReceiptSheet.DepositReceiptBottomSheet
import com.microbond.app.core.components.threadbubbles.weekly_report.WeeklyReportNumberDetailSheet
import com.microbond.app.core.components.usersheet.UserProfileSheet
import com.microbond.app.core.components.usersheet.UserProfileSheetData
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent

import com.microbond.app.core.components.threadbubbles.bubblesheets.depositwidget.DepositMemberDrillDownSheet
import com.microbond.app.core.network.api.deposits.DepositStatusFilter

import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Consolidated manager for all modal bottom sheets rendered within ThreadScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadBottomSheets(
    entityName: String,
    groupId: String? = null,
    selectedDepositEvent: ThreadEvent?,
    selectedUserSheet: UserProfileSheetData?,
    weeklyTapLabel: String?,
    weeklyTapValue: Int,
    groupType: SavingsGroupType? = null,
    discoveredGroup: DiscoverableContactGroup? = null,
    rotatingGroupState: RotatingGroupPreviewUiState? = null,
    showRotatingGroupDetailsSheet: Boolean = false,
    selectedDepositFilter: DepositStatusFilter? = null,
    onDepositDismiss: () -> Unit,
    onUserSheetDismiss: () -> Unit,
    onWeeklyTapDismiss: () -> Unit,
    onRotatingSheetDismiss: () -> Unit = {},
    onDepositFilterDismiss: () -> Unit = {},
    onClaimSlotConfirmed: (selectedGuarantorId: String?, slotNumber: Int, groupName: String) -> Unit = { _, _, _ -> }
) {
    selectedDepositEvent?.let { depositEvent ->
        DepositReceiptBottomSheet(
            event = depositEvent,
            groupName = entityName,
            onDismiss = onDepositDismiss
        )
    }

    selectedUserSheet?.let { user ->
        UserProfileSheet(
            user = user,
            onDismiss = onUserSheetDismiss
        )
    }

    weeklyTapLabel?.let { label ->
        WeeklyReportNumberDetailSheet(
            label = label,
            value = weeklyTapValue,
            onDismiss = onWeeklyTapDismiss
        )
    }

    // Rotating Group Details Sheet (Only for MERRY_GO_ROUND)
    if (showRotatingGroupDetailsSheet && groupType == SavingsGroupType.MERRY_GO_ROUND) {
        val rotatingState = rotatingGroupState ?: remember(entityName, discoveredGroup) {
            if (discoveredGroup != null) {
                RotatingGroupPreviewUiState.fromDiscoverableGroup(discoveredGroup)
            } else {
                RotatingGroupPreviewUiState.fromGroupNameOnly(entityName)
            }
        }
        RotatingGroupDetailsSheet(
            state = rotatingState,
            onDismissRequest = onRotatingSheetDismiss,
            onJoinGroupClick = { selectedGuarantorId ->
                onRotatingSheetDismiss()
                onClaimSlotConfirmed(selectedGuarantorId, rotatingState.nextOpenSlot, rotatingState.name)
            }
        )
    }

    // Daily Deposit Status Member Drill-Down Sheet
    selectedDepositFilter?.let { filter ->
        val targetGroupId = groupId?.ifBlank { null } ?: entityName
        DepositMemberDrillDownSheet(
            groupId = targetGroupId,
            initialFilter = filter,
            onDismiss = onDepositFilterDismiss
        )
    }
}


package com.microbond.app.core.components.threadbubbles.bubblesheets.depositwidget

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.bubblesheets.depositwidget.widgets.DepositFilterHeader
import com.microbond.app.core.components.threadbubbles.bubblesheets.depositwidget.widgets.DepositMemberListItem
import com.microbond.app.core.components.threadbubbles.bubblesheets.depositwidget.widgets.NudgeConfirmationDialog
import com.microbond.app.core.network.api.deposits.DailyDepositWidgetData
import com.microbond.app.core.network.api.deposits.DepositMemberItem
import com.microbond.app.core.network.api.deposits.DepositStatusFilter
import com.microbond.app.core.network.api.deposits.DepositWidgetRepository
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondBottomSheetDark
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondDragHandleGray
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondPureWhite
import kotlinx.coroutines.launch

/**
 * Modal bottom sheet presenting the deposit status breakdown and member drill-down list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositMemberDrillDownSheet(
    groupId: String,
    initialFilter: DepositStatusFilter = DepositStatusFilter.ALL,
    dateMs: Long? = null,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf(initialFilter) }
    var summaryData by remember { mutableStateOf<DailyDepositWidgetData?>(null) }
    var membersList by remember { mutableStateOf<List<DepositMemberItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var memberToNudge by remember { mutableStateOf<DepositMemberItem?>(null) }

    val nudgedUserIds by DepositWidgetRepository.nudgedUserIds.collectAsState()

    // Fetch summary counters once
    LaunchedEffect(groupId, dateMs) {
        val result = DepositWidgetRepository.fetchDailyWidget(groupId, dateMs)
        result.onSuccess { summaryData = it }
    }

    // Fetch members whenever filter changes
    LaunchedEffect(groupId, selectedFilter, dateMs) {
        isLoading = true
        val result = DepositWidgetRepository.fetchMembers(groupId, selectedFilter, dateMs)
        result.onSuccess { membersList = it }
        result.onFailure {
            membersList = emptyList()
        }
        isLoading = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MicrobondBottomSheetDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MicrobondDragHandleGray)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Deposit Status",
                        color = MicrobondPureWhite,
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val dateText = summaryData?.date ?: "Today"
                    Text(
                        text = "Member breakdown • $dateText",
                        color = MicrobondInactiveGray,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MicrobondInactiveGray
                    )
                }
            }

            // Filter header chips
            DepositFilterHeader(
                selectedFilter = selectedFilter,
                fullCount = summaryData?.summary?.full?.count ?: 0,
                halfCount = summaryData?.summary?.partial?.count ?: 0,
                notDepositedCount = summaryData?.summary?.notDeposited?.count ?: 0,
                totalCount = summaryData?.totalMembers ?: 0,
                onFilterSelected = { selectedFilter = it }
            )

            HorizontalDivider(
                color = MicrobondDividerDark,
                thickness = 1.dp
            )

            // Content list
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MicrobondActiveGreen,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            } else if (membersList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No members found in this category",
                        color = MicrobondInactiveGray,
                        fontSize = 13.5.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    items(
                        items = membersList,
                        key = { it.userId.ifBlank { it.membershipId } }
                    ) { member ->
                        val isNudged = nudgedUserIds.contains(member.userId)
                        DepositMemberListItem(
                            member = member,
                            isNudged = isNudged,
                            onNudgeClick = { memberToNudge = it }
                        )
                    }
                }
            }
        }
    }

    // Nudge Confirmation Dialog
    memberToNudge?.let { target ->
        NudgeConfirmationDialog(
            member = target,
            onDismiss = { memberToNudge = null },
            onConfirm = { member, message ->
                coroutineScope.launch {
                    val nudgeResult = DepositWidgetRepository.nudgeMember(
                        groupId = groupId,
                        targetUserId = member.userId,
                        customMessage = message
                    )
                    nudgeResult.onSuccess {
                        Toast.makeText(context, "Reminder sent to ${member.fullName}", Toast.LENGTH_SHORT).show()
                    }
                    nudgeResult.onFailure { error ->
                        Toast.makeText(context, "Could not send reminder: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                memberToNudge = null
            }
        )
    }
}

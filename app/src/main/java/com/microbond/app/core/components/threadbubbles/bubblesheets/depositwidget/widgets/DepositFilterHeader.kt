package com.microbond.app.core.components.threadbubbles.bubblesheets.depositwidget.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.api.deposits.DepositStatusFilter
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondBorderDark
import com.microbond.app.core.ui.theme.MicrobondChipNonSelectedColor
import com.microbond.app.core.ui.theme.MicrobondDepositFullGreen
import com.microbond.app.core.ui.theme.MicrobondDepositHalfYellow
import com.microbond.app.core.ui.theme.MicrobondDepositNotPaidRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondPillTextDark
import com.microbond.app.core.ui.theme.MicrobondPureWhite

/**
 * Filter chips row for switching between Full, Half, Not Deposited, and All members.
 */
@Composable
fun DepositFilterHeader(
    selectedFilter: DepositStatusFilter,
    fullCount: Int,
    halfCount: Int,
    notDepositedCount: Int,
    totalCount: Int,
    onFilterSelected: (DepositStatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChipItem(
            label = "All",
            count = totalCount,
            isSelected = selectedFilter == DepositStatusFilter.ALL,
            indicatorColor = MicrobondActiveGreen,
            onClick = { onFilterSelected(DepositStatusFilter.ALL) }
        )

        FilterChipItem(
            label = "Full Deposit",
            count = fullCount,
            isSelected = selectedFilter == DepositStatusFilter.FULL,
            indicatorColor = MicrobondDepositFullGreen,
            onClick = { onFilterSelected(DepositStatusFilter.FULL) }
        )

        FilterChipItem(
            label = "Half Deposit",
            count = halfCount,
            isSelected = selectedFilter == DepositStatusFilter.PARTIAL,
            indicatorColor = MicrobondDepositHalfYellow,
            onClick = { onFilterSelected(DepositStatusFilter.PARTIAL) }
        )

        FilterChipItem(
            label = "Not Deposited",
            count = notDepositedCount,
            isSelected = selectedFilter == DepositStatusFilter.NOT_DEPOSITED,
            indicatorColor = MicrobondDepositNotPaidRed,
            onClick = { onFilterSelected(DepositStatusFilter.NOT_DEPOSITED) }
        )
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    count: Int,
    isSelected: Boolean,
    indicatorColor: Color,
    onClick: () -> Unit
) {
    val bg = if (isSelected) MicrobondActiveGreen.copy(alpha = 0.15f) else MicrobondChipNonSelectedColor
    val borderCol = if (isSelected) MicrobondActiveGreen else MicrobondBorderDark

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(width = 1.dp, color = borderCol, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(indicatorColor)
        )

        Text(
            text = label,
            color = if (isSelected) MicrobondPureWhite else MicrobondInactiveGray,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(indicatorColor)
                .padding(horizontal = 5.dp, vertical = 1.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                color = MicrobondPillTextDark,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

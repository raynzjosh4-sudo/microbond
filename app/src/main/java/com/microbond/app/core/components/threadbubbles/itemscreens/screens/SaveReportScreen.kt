package com.microbond.app.core.components.threadbubbles.itemscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.switchbutton.MicrobondSwitch
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val RequestBackground = MicrobondDarkBackground
private val RequestSurface = MicrobondOverlayWidgetDark
private val RequestDivider = MicrobondDividerDark
private val RequestMuted = MicrobondInactiveGray

private data class ReportSection(
    val title: String,
    val detail: String,
    val icon: ImageVector,
    val color: Color,
    val initiallyIncluded: Boolean = true
)

@Composable
fun SaveReportScreen(
    onBack: () -> Unit,
    onCreateRequest: (includedSections: Set<String>, period: String, selectedDate: String) -> Unit
) {
    val sections = remember {
        mutableStateListOf(
            ReportSection("Week summary", "Totals, success rate, key highlights", Icons.Default.ShowChart, MicrobondActiveGreen),
            ReportSection("Daily records", "Full / partial / no save per day", Icons.Default.CalendarMonth, MicrobondActiveGreen),
            ReportSection("Member balances", "Savings, dues, loans and balances", Icons.Default.Savings, MicrobondActiveGreen),
            ReportSection("Members who saved", "List of members who made savings", Icons.Default.People, MicrobondActiveGreen),
            ReportSection("Members who missed", "List of members with no savings", Icons.Default.PersonOff, MicrobondInactiveGray),
            ReportSection("Payout rotation", "Completed and upcoming payouts", Icons.Default.Timelapse, MicrobondActiveGreen),
            ReportSection("Group activity", "Busiest days, average per day", Icons.Default.QueryStats, MicrobondActiveGreen, false),
            ReportSection("Contribution history", "All member contributions in date range", Icons.Default.History, MicrobondActiveGreen, false),
            ReportSection("All members", "Full member list with eligibility status", Icons.Default.Groups, MicrobondActiveGreen)
        )
    }
    val included = remember { mutableStateListOf<Boolean>().also { list -> sections.forEach { list.add(it.initiallyIncluded) } } }
    val ranges = listOf("Day", "Week", "Month", "5 Years", "Custom days")
    var selectedRange by remember { mutableStateOf("Day") }
    var selectedDay by remember { mutableStateOf(28) }
    var monthOffset by remember { mutableStateOf(0) }
    val displayedMonth = YearMonth.of(2026, 9).plusMonths(monthOffset.toLong())
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()) }

    BackHandler(onBack = onBack)

    Scaffold(
        containerColor = RequestBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(RequestSurface)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MicrobondTextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Request a view", color = MicrobondTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("Ugandan Bibina", color = RequestMuted, fontSize = 12.sp)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val selected = sections.indices
                        .filter { included[it] }
                        .mapTo(linkedSetOf()) { sections[it].title }
                    onCreateRequest(
                        selected,
                        selectedRange,
                        displayedMonth.atDay(selectedDay).format(monthFormatter)
                    )
                },
                containerColor = MicrobondActiveGreen,
                contentColor = MicrobondDarkBackground,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Create request")
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = RequestSurface,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp).background(MicrobondActivePillDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PieChart, null, tint = MicrobondActiveGreen, modifier = Modifier.size(22.dp))
                        }
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text("Include", color = MicrobondTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                            Text("Select the information to include in your report", color = RequestMuted, fontSize = 12.sp)
                        }
                    }
                    sections.forEachIndexed { index, section ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(section.icon, null, tint = section.color, modifier = Modifier.size(24.dp))
                            Column(
                                modifier = Modifier.weight(1f).padding(start = 12.dp)
                            ) {
                                Text(section.title, color = MicrobondTextPrimary, fontSize = 14.sp, lineHeight = 17.sp)
                                Text(section.detail, color = RequestMuted, fontSize = 11.sp, lineHeight = 14.sp)
                            }
                            MicrobondSwitch(
                                checked = included[index],
                                onCheckedChange = { included[index] = it }
                            )
                        }
                        if (index < sections.lastIndex) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp)
                                    .height(1.dp)
                                    .background(RequestDivider)
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = RequestSurface,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, null, tint = MicrobondActiveGreen, modifier = Modifier.size(24.dp))
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text("When", color = MicrobondTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("Choose the time period for your report", color = RequestMuted, fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().background(MicrobondActivePillDark, RoundedCornerShape(20.dp)).padding(3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ranges.forEach { range ->
                            val selected = selectedRange == range
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (selected) MicrobondActiveGreen else Color.Transparent, RoundedCornerShape(16.dp))
                                    .clickable { selectedRange = range }
                                    .padding(horizontal = 3.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    range,
                                    color = if (selected) MicrobondDarkBackground else MicrobondTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(9.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = RequestSurface,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Select date", color = RequestMuted, fontSize = 11.sp)
                                    Text(
                                        displayedMonth.atDay(selectedDay).format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())),
                                        color = MicrobondTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚡ Quick select", color = MicrobondTextPrimary, fontSize = 11.sp)
                                }
                            }
                            Spacer(Modifier.height(5.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(displayedMonth.format(monthFormatter), color = RequestMuted, fontSize = 12.sp)
                                Row {
                                    IconButton(
                                        onClick = {
                                            monthOffset--
                                            selectedDay = selectedDay.coerceAtMost(
                                                YearMonth.of(2026, 9).plusMonths(monthOffset.toLong()).lengthOfMonth()
                                            )
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            monthOffset++
                                            selectedDay = selectedDay.coerceAtMost(
                                                YearMonth.of(2026, 9).plusMonths(monthOffset.toLong()).lengthOfMonth()
                                            )
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                            val weekdays = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
                            Row(Modifier.fillMaxWidth()) {
                                weekdays.forEach { day ->
                                    Text(day, modifier = Modifier.weight(1f), color = RequestMuted, fontSize = 9.sp, textAlign = TextAlign.Center)
                                }
                            }
                            val leadingBlankDays = displayedMonth.atDay(1).dayOfWeek.value - 1
                            val dayCount = displayedMonth.lengthOfMonth()
                            val weekCount = (leadingBlankDays + dayCount + 6) / 7
                            (0 until weekCount).forEach { week ->
                                Row(Modifier.fillMaxWidth()) {
                                    (1..7).forEach { day ->
                                        val date = week * 7 + day - 1 - leadingBlankDays
                                        val selected = date == selectedDay
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(23.dp)
                                                .clickable { selectedDay = date },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (date in 1..dayCount) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(19.dp)
                                                        .background(
                                                            if (selected) MicrobondActiveGreen else Color.Transparent,
                                                            CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        date.toString(),
                                                        color = if (selected) MicrobondDarkBackground else MicrobondTextPrimary,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(7.dp))
                    QuickDateRow(
                        "Today",
                        YearMonth.of(2026, 9).atDay(28).format(monthFormatter),
                        selected = monthOffset == 0 && selectedDay == 28
                    ) {
                        monthOffset = 0
                        selectedDay = 28
                    }
                    QuickDateRow(
                        "Yesterday",
                        YearMonth.of(2026, 9).atDay(27).format(monthFormatter),
                        selected = monthOffset == 0 && selectedDay == 27
                    ) {
                        monthOffset = 0
                        selectedDay = 27
                    }
                    QuickDateRow("Choose date", "Pick from calendar", selected = false) { selectedRange = "Custom days" }
                }
            }
            Spacer(Modifier.height(68.dp))
        }
    }
}

@Composable
private fun QuickDateRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .background(MicrobondActivePillDark, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (selected) Icons.Default.Check else Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = if (selected) MicrobondActiveGreen else RequestMuted,
            modifier = Modifier.size(15.dp)
        )
        Column(modifier = Modifier.padding(start = 7.dp)) {
            Text(title, color = MicrobondTextPrimary, fontSize = 11.sp)
            Text(subtitle, color = RequestMuted, fontSize = 9.sp)
        }
    }
}

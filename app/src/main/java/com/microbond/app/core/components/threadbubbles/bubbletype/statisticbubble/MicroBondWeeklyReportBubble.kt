package com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.WeeklyReportData

private val GreenNum    = Color(0xFF4ADE80)
private val YellowNum   = Color(0xFFFFD166)
private val RedNum      = Color(0xFFFF6B6B)
private val MutedText   = Color(0xFF8696A0)
private val HeaderBg    = Color(0xFF0F2D26)
private val DividerClr  = Color.White.copy(alpha = 0.08f)
private val SectionIcon = Color(0xFF4ADE80)
private val TableBg     = Color(0xFF1A2520)
private val TableBorder = Color.White.copy(alpha = 0.10f)

// ─────────────────────────────────────────────────────────────
// Public composable
// ─────────────────────────────────────────────────────────────

@Composable
fun MicroBondWeeklyReportBubble(
    meta: MicroBondMeta,
    report: WeeklyReportData,
    onNumberTap: ((label: String, value: Int) -> Unit)? = null,
    onSummaryRowClick: ((section: String) -> Unit)? = null,
    onRequestPdf: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    _root_ide_package_.com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        authorType = _root_ide_package_.com.microbond.app.core.components.threadbubbles.BubbleAuthorType.DATA,
        showTimestamp = false,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── HEADER ──────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoGraph,
                        contentDescription = null,
                        tint = GreenNum,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = report.weekLabel,
                        color = GreenNum,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.3.sp
                    )
                }
                Text(
                    text = "${report.groupName}  ·  ${report.dateRange}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Daily target:  ${report.dailyTarget}  ·  ${report.memberCount} members",
                    color = MutedText,
                    fontSize = 11.sp
                )
                Text(
                    text = "AS OF ${report.asOf}",
                    color = MutedText,
                    fontSize = 10.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                fun showSection(name: String): Boolean =
                    !report.filterSections || name in report.visibleSections

                // ── MEMBERS ─────────────────────────────────────────
                if (showSection("MEMBERS")) ReportSection(icon = Icons.Default.Groups, title = "MEMBERS") {
                    TappableStatLine(
                        onNumberTap = onNumberTap,
                        segments = listOf(
                            StatSegment(
                                "${report.membersStart}",
                                GreenNum,
                                "Members start",
                                report.membersStart
                            ),
                            StatSegment(" start  ·  "),
                            StatSegment(
                                "${report.membersJoined}",
                                GreenNum,
                                "Members joined",
                                report.membersJoined
                            ),
                            StatSegment(" joined  ·  "),
                            StatSegment(
                                "${report.membersLeft}",
                                RedNum,
                                "Members left",
                                report.membersLeft
                            ),
                            StatSegment(" left  ·  "),
                            StatSegment(
                                "${report.membersNow}",
                                GreenNum,
                                "Members now",
                                report.membersNow
                            ),
                            StatSegment(" now  ·  "),
                            StatSegment(
                                "${report.eligibleSaveDays}",
                                YellowNum,
                                "Eligible save-days",
                                report.eligibleSaveDays
                            ),
                            StatSegment(" eligible save-days")
                        )
                    )
                }

                if (showSection("MEMBERS") && showSection("DAILY SAVING")) SectionDivider()

                // ── DAILY SAVING TABLE ───────────────────────────────
                if (showSection("DAILY SAVING")) ReportSection(icon = Icons.Default.CalendarMonth, title = "DAILY SAVING") {
                    Text(
                        text = "(full / partial / no save; eligible)",
                        color = MutedText,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DailySavingTable(
                        dailySavings = report.dailySavings,
                        memberCount = report.memberCount,
                        onNumberTap = onNumberTap
                    )
                }

                if (showSection("DAILY SAVING") && showSection("WEEK TOTALS")) SectionDivider()

                // ── WEEK TOTALS ──────────────────────────────────────
                if (showSection("WEEK TOTALS")) ReportSection(icon = Icons.Default.PieChart, title = "WEEK TOTALS") {
                    TappableStatLine(
                        onNumberTap = onNumberTap,
                        segments = listOf(
                            StatSegment(
                                "${report.totalFullDays}",
                                GreenNum,
                                "Full days",
                                report.totalFullDays
                            ),
                            StatSegment(" full days  ·  "),
                            StatSegment(
                                "${report.totalPartialDays}",
                                YellowNum,
                                "Partial days",
                                report.totalPartialDays
                            ),
                            StatSegment(" partial days  ·  "),
                            StatSegment(
                                "${report.totalNoSaveDays}",
                                RedNum,
                                "No-save days",
                                report.totalNoSaveDays
                            ),
                            StatSegment(" no-save days")
                        )
                    )
                    TappableStatLine(
                        onNumberTap = onNumberTap,
                        segments = listOf(
                            StatSegment(
                                "${report.membersSavedAtLeastOnce}",
                                GreenNum,
                                "Members saved ≥1x",
                                report.membersSavedAtLeastOnce
                            ),
                            StatSegment(" members saved at least once  ·  "),
                            StatSegment(
                                "${report.successfulSaveDays}",
                                GreenNum,
                                "Successful save-days",
                                report.successfulSaveDays
                            ),
                            StatSegment(" successful save-days")
                        )
                    )
                    TappableStatLine(
                        onNumberTap = onNumberTap,
                        segments = listOf(
                            StatSegment(
                                "${report.fullEveryEligibleDay}",
                                GreenNum,
                                "Full every eligible day",
                                report.fullEveryEligibleDay
                            ),
                            StatSegment(" full every eligible day  ·  "),
                            StatSegment(
                                "${report.partialNoMissedDays}",
                                YellowNum,
                                "Partial, no missed",
                                report.partialNoMissedDays
                            ),
                            StatSegment(" partial, no missed days  ·")
                        )
                    )
                    TappableStatLine(
                        onNumberTap = onNumberTap,
                        segments = listOf(
                            StatSegment(
                                "${report.missed1PlusDays}",
                                RedNum,
                                "Missed 1+ days",
                                report.missed1PlusDays
                            ),
                            StatSegment(" missed 1+ days  ·  "),
                            StatSegment(
                                "${report.noSavingAllWeek}",
                                RedNum,
                                "No saving all week",
                                report.noSavingAllWeek
                            ),
                            StatSegment(" no saving all week")
                        )
                    )
                }

                if (showSection("WEEK TOTALS") && showSection("BALANCE COUNTS")) SectionDivider()

                // ── BALANCE COUNTS ───────────────────────────────────
                if (showSection("BALANCE COUNTS")) ReportSection(icon = Icons.Default.AccountBalanceWallet, title = "BALANCE COUNTS") {
                    TappableStatLine(
                        onNumberTap = onNumberTap,
                        segments = listOf(
                            StatSegment(
                                "${report.startedWithBalance}",
                                YellowNum,
                                "Started with balance",
                                report.startedWithBalance
                            ),
                            StatSegment(" started with balance  ·  "),
                            StatSegment(
                                "${report.clearedBalance}",
                                GreenNum,
                                "Cleared balance",
                                report.clearedBalance
                            ),
                            StatSegment(" cleared balance  ·")
                        )
                    )
                    TappableStatLine(
                        onNumberTap = onNumberTap,
                        segments = listOf(
                            StatSegment(
                                "${report.newBalances}",
                                RedNum,
                                "New balances",
                                report.newBalances
                            ),
                            StatSegment(" new balances  ·  "),
                            StatSegment(
                                "${report.endWithBalance}",
                                RedNum,
                                "End with balance",
                                report.endWithBalance
                            ),
                            StatSegment(" end with balance")
                        )
                    )
                }

                if (showSection("BALANCE COUNTS") && showSection("ACTIVITY")) SectionDivider()

                // ── ACTIVITY ─────────────────────────────────────────
                if (showSection("ACTIVITY")) ReportSection(icon = Icons.Default.AutoGraph, title = "ACTIVITY") {
                    TappableStatLine(
                        onNumberTap = onNumberTap,
                        segments = listOf(
                            StatSegment("busiest ${report.busiestDays}, "),
                            StatSegment(
                                "${report.saversPerDay}",
                                GreenNum,
                                "Savers/day",
                                report.saversPerDay
                            ),
                            StatSegment(" savers/day  ·  quietest ${report.quietestDay}, "),
                            StatSegment(
                                "${report.quietestCount}",
                                YellowNum,
                                "Quietest day count",
                                report.quietestCount
                            )
                        )
                    )
                }

                if (showSection("ACTIVITY") && showSection("PAYOUT ROTATION")) SectionDivider()

                // ── PAYOUT ROTATION ──────────────────────────────────
                if (showSection("PAYOUT ROTATION")) ReportSection(icon = Icons.Default.Refresh, title = "PAYOUT ROTATION") {
                    Text(
                        text = "this week ${report.payoutThisWeek}",
                        color = Color.White,
                        fontSize = 11.sp
                    )
                    TappableStatLine(
                        onNumberTap = onNumberTap,
                        segments = listOf(
                            StatSegment("Next week, ${report.payoutNextWeek}  ·  "),
                            StatSegment(
                                "${report.remainAfterPayout}",
                                YellowNum,
                                "Remain after payout",
                                report.remainAfterPayout
                            ),
                            StatSegment(" remain after next payout")
                        )
                    )
                }

                if (showSection("PAYOUT ROTATION") && showSection("FIRST-WEEK BASELINE")) SectionDivider()

                // ── FIRST-WEEK BASELINE ──────────────────────────────
                if (showSection("FIRST-WEEK BASELINE")) ReportSection(icon = Icons.Default.CompareArrows, title = "FIRST-WEEK BASELINE") {
                    Text(
                        text = report.baselineNote,
                        color = MutedText,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ── FOOTER DISCLAIMER ────────────────────────────────
                HorizontalDivider(color = DividerClr, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Full = daily target met  ·  Partial = some saving, below target  ·  No save = no successful record\n" +
                            "Successful online saves only; failed attempts are not stored.\n" +
                            "Illustrative sample; balance carry-forward is a sample rule.",
                    color = MutedText,
                    fontSize = 9.sp,
                    lineHeight = 12.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // ── SUMMARY NAV ROWS ─────────────────────────────────────
            HorizontalDivider(color = DividerClr, thickness = 0.6.dp)
            SummaryNavRow(
                icon = Icons.Default.CalendarMonth,
                label = "Daily saving by day",
                onClick = { onSummaryRowClick?.invoke("daily_saving") }
            )
            HorizontalDivider(
                color = DividerClr,
                thickness = 0.4.dp,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            SummaryNavRow(
                icon = Icons.Default.PieChart,
                label = "Weekly saving status",
                onClick = { onSummaryRowClick?.invoke("weekly_status") }
            )
            HorizontalDivider(
                color = DividerClr,
                thickness = 0.4.dp,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            SummaryNavRow(
                icon = Icons.Default.AccountBalanceWallet,
                label = "Member balance counts",
                onClick = { onSummaryRowClick?.invoke("balance_counts") }
            )
            HorizontalDivider(
                color = DividerClr,
                thickness = 0.4.dp,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            SummaryNavRow(
                icon = Icons.Default.Refresh,
                label = "Payout rotation",
                onClick = { onSummaryRowClick?.invoke("payout_rotation") }
            )
            HorizontalDivider(
                color = DividerClr,
                thickness = 0.4.dp,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            SummaryNavRow(
                icon = Icons.Default.ManageAccounts,
                label = "Membership changes",
                onClick = { onSummaryRowClick?.invoke("membership_changes") },
                showBottomDivider = false
            )

            // PDF Request + Timestamp row
            HorizontalDivider(color = DividerClr, thickness = 0.6.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // "Request full report PDF" text button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onRequestPdf?.invoke() }
                        .border(0.8.dp, GreenNum.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "📄 Request full report PDF",
                        color = GreenNum,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = report.footerTime,
                    color = MutedText,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Daily Saving Grid Table
// ─────────────────────────────────────────────────────────────

@Composable
private fun DailySavingTable(
    dailySavings: List<Pair<String, Triple<Int, Int, Int>>>,
    memberCount: Int,
    onNumberTap: ((label: String, value: Int) -> Unit)? = null
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(0.6.dp, TableBorder, shape)
            .background(TableBg)
    ) {
        // Header row — day names
        Row(modifier = Modifier.fillMaxWidth()) {
            dailySavings.forEachIndexed { index, (day, _) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (index % 2 == 0) Color.White.copy(alpha = 0.03f)
                            else Color.Transparent
                        )
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        HorizontalDivider(color = TableBorder, thickness = 0.5.dp)

        // Data row — full/partial/noSave tappable
        Row(modifier = Modifier.fillMaxWidth()) {
            dailySavings.forEachIndexed { index, (day, triple) ->
                val (full, partial, noSave) = triple
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (index % 2 == 0) Color.White.copy(alpha = 0.03f)
                            else Color.Transparent
                        )
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        // full/partial/noSave on one row, tappable numbers
                        Row(horizontalArrangement = Arrangement.Center) {
                            Text(
                                text = "$full",
                                color = GreenNum,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onNumberTap?.invoke("$day full", full) }
                            )
                            Text("/", color = MutedText, fontSize = 10.sp)
                            Text(
                                text = "$partial",
                                color = YellowNum,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onNumberTap?.invoke("$day partial", partial) }
                            )
                            Text("/", color = MutedText, fontSize = 10.sp)
                            Text(
                                text = "$noSave",
                                color = RedNum,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onNumberTap?.invoke("$day no-save", noSave) }
                            )
                        }
                        // eligible in brackets
                        Text(
                            text = "($memberCount)",
                            color = MutedText,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Summary nav row
// ─────────────────────────────────────────────────────────────

@Composable
private fun SummaryNavRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    showBottomDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SectionIcon,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Go",
            tint = MutedText,
            modifier = Modifier.size(12.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Helper composables
// ─────────────────────────────────────────────────────────────

@Composable
private fun ReportSection(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SectionIcon,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        content()
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        color = DividerClr,
        thickness = 0.6.dp
    )
}

// ─────────────────────────────────────────────────────────────
// Tappable stat line with colored number spans
// ─────────────────────────────────────────────────────────────

data class StatSegment(
    val text: String,
    val color: Color? = null,
    val tapLabel: String? = null,
    val tapValue: Int? = null
)

@Composable
private fun TappableStatLine(
    segments: List<StatSegment>,
    onNumberTap: ((label: String, value: Int) -> Unit)? = null
) {
    val annotated = buildAnnotatedString {
        segments.forEach { seg ->
            if (seg.color != null && seg.tapLabel != null && seg.tapValue != null) {
                pushStringAnnotation(tag = "NUM", annotation = "${seg.tapLabel}|${seg.tapValue}")
                withStyle(SpanStyle(color = seg.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)) {
                    append(seg.text)
                }
                pop()
            } else {
                withStyle(SpanStyle(color = MutedText, fontSize = 11.sp)) {
                    append(seg.text)
                }
            }
        }
    }

    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = annotated,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures { pos ->
                    layoutResult?.let { result ->
                        val offset = result.getOffsetForPosition(pos)
                        annotated.getStringAnnotations(tag = "NUM", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                val parts = annotation.item.split("|")
                                if (parts.size == 2) {
                                    val label = parts[0]
                                    val value = parts[1].toIntOrNull() ?: 0
                                    onNumberTap?.invoke(label, value)
                                }
                            }
                    }
                }
            },
        lineHeight = 16.sp,
        onTextLayout = { layoutResult = it }
    )
}

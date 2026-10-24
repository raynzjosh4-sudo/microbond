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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.components.chips.MicrobondChoiceChips

private data class GroupRule(
    val title: String,
    val category: String,
    val status: String,
    val checkedInitially: Boolean
)

private data class RuleCategory(
    val title: String,
    val icon: ImageVector,
    val rules: List<GroupRule>
)

private val RuleCategories = listOf(
    RuleCategory(
        "Rules & conduct",
        Icons.Default.PushPin,
        listOf(
            GroupRule("Pay before payout day", "Rules & conduct", "Up to date", true),
            GroupRule("One member receives per turn", "Rules & conduct", "Changed", true),
            GroupRule("New members join next cycle", "Rules & conduct", "New", true)
        )
    ),
    RuleCategory(
        "Privacy & data",
        Icons.Default.Lock,
        listOf(
            GroupRule("Privacy policy", "Privacy & data", "Up to date", true),
            GroupRule("Data visibility", "Privacy & data", "New", true),
            GroupRule("Data retention", "Privacy & data", "Up to date", false)
        )
    ),
    RuleCategory(
        "Money & payouts",
        Icons.Default.Payments,
        listOf(
            GroupRule("Saving conditions", "Money & payouts", "New", true),
            GroupRule("Late payments", "Money & payouts", "Changed", false),
            GroupRule("Payout rotation", "Money & payouts", "Up to date", false),
            GroupRule("Refunds", "Money & payouts", "Up to date", false)
        )
    ),
    RuleCategory(
        "Membership",
        Icons.Default.Groups,
        listOf(
            GroupRule("Joining", "Membership", "Changed", false),
            GroupRule("Leaving", "Membership", "Up to date", true),
            GroupRule("Member responsibilities", "Membership", "Up to date", false)
        )
    )
)

@Composable
fun RulesScreen(
    onBack: () -> Unit,
    onSend: (selectedRules: List<String>, singleRuleText: String, singleRuleStatus: String?) -> Unit,
    groupName: String = "Harbor ROSCA"
) {
    var selectedTab by remember { mutableStateOf("Categories") }
    var statusFilter by remember { mutableStateOf("Any status") }
    var selectedSingleRule by remember { mutableStateOf<GroupRule?>(null) }
    val selectedRules = remember {
        mutableStateListOf<String>().also { selected ->
            RuleCategories.flatMap { it.rules }
                .filter { it.checkedInitially }
                .forEach { selected.add(it.title) }
        }
    }
    val expandedCategories = remember { mutableStateListOf(*RuleCategories.map { it.title }.toTypedArray()) }
    val visibleRules = RuleCategories.flatMap { it.rules }.filter {
        statusFilter == "Any status" || it.status == statusFilter
    }
    val canSend = if (selectedTab == "Single rule text") {
        selectedSingleRule != null
    } else {
        selectedRules.isNotEmpty()
    }

    BackHandler(onBack = onBack)
    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MicrobondDarkBackground)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MicrobondTextPrimary)
                }
                Box(
                    modifier = Modifier.size(36.dp).background(MicrobondActivePillDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Group, null, tint = MicrobondActiveGreen, modifier = Modifier.size(21.dp))
                }
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("Group policy request", color = MicrobondTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("$groupName · 4 members", color = MicrobondInactiveGray, fontSize = 10.sp)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onSend(
                        if (selectedTab == "Categories") selectedRules.toList() else emptyList(),
                        selectedSingleRule?.title.orEmpty().takeIf { selectedTab == "Single rule text" }.orEmpty(),
                        selectedSingleRule?.status.takeIf { selectedTab == "Single rule text" }
                    )
                },
                modifier = Modifier.size(56.dp),
                containerColor = if (canSend) MicrobondActiveGreen else MicrobondDividerDark,
                contentColor = MicrobondDarkBackground,
                shape = CircleShape
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Continue")
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MicrobondOverlayWidgetDark, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                listOf("Categories", "Single rule text").forEach { tab ->
                    val selected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selected) MicrobondActivePillDark else Color.Transparent,
                                RoundedCornerShape(9.dp)
                            )
                            .clickable { selectedTab = tab }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            tab,
                            color = if (selected) MicrobondActiveGreen else MicrobondInactiveGray,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            if (selectedTab == "Categories") {
                MicrobondChoiceChips(
                    labels = listOf("Any status", "New", "Changed", "Up to date"),
                    selectedChip = statusFilter,
                    onChipSelected = { statusFilter = it },
                    modifier = Modifier.fillMaxWidth()
                )

                RuleCategories.forEach { category ->
                    val categoryRules = category.rules.filter { it in visibleRules }
                    if (categoryRules.isNotEmpty()) {
                        val expanded = category.title in expandedCategories
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MicrobondOverlayWidgetDark,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (expanded) expandedCategories.remove(category.title)
                                            else expandedCategories.add(category.title)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(category.icon, null, tint = MicrobondActiveGreen, modifier = Modifier.size(20.dp))
                                    Column(modifier = Modifier.weight(1f).padding(start = 9.dp)) {
                                        Text(category.title, color = MicrobondTextPrimary, fontSize = 12.sp)
                                        Text(
                                            "${category.rules.count { it.title in selectedRules }} of ${category.rules.size} selected",
                                            color = MicrobondInactiveGray,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Text(if (expanded) "⌃" else "⌄", color = MicrobondInactiveGray, fontSize = 16.sp)
                                }
                                if (expanded) {
                                    categoryRules.forEach { rule ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(37.dp)
                                                .clickable {
                                                    if (rule.title in selectedRules) selectedRules.remove(rule.title)
                                                    else selectedRules.add(rule.title)
                                                }
                                                .padding(horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = rule.title in selectedRules,
                                                onCheckedChange = { checked ->
                                                    if (checked) selectedRules.add(rule.title)
                                                    else selectedRules.remove(rule.title)
                                                },
                                                modifier = Modifier.size(28.dp),
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = MicrobondActiveGreen,
                                                    uncheckedColor = MicrobondInactiveGray,
                                                    checkmarkColor = MicrobondDarkBackground
                                                )
                                            )
                                            Text(
                                                rule.title,
                                                modifier = Modifier.weight(1f).padding(start = 5.dp),
                                                color = MicrobondTextPrimary,
                                                fontSize = 10.sp
                                            )
                                            StatusBadge(rule.status)
                                        }
                                        if (rule != categoryRules.last()) {
                                            Box(
                                                Modifier.fillMaxWidth().padding(start = 40.dp, end = 8.dp)
                                                    .height(1.dp).background(MicrobondDividerDark)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                MicrobondChoiceChips(
                    labels = listOf("Any status", "New", "Changed", "Up to date"),
                    selectedChip = statusFilter,
                    onChipSelected = { statusFilter = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MicrobondOverlayWidgetDark,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Single rule text", color = MicrobondTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Choose one existing policy to request an update.", color = MicrobondInactiveGray, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        RuleCategories.flatMap { it.rules }
                            .filter { statusFilter == "Any status" || it.status == statusFilter }
                            .forEach { rule ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                        .clickable { selectedSingleRule = rule }
                                        .padding(horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (selectedSingleRule == rule) Icons.Default.RadioButtonChecked
                                        else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (selectedSingleRule == rule) MicrobondActiveGreen else MicrobondInactiveGray,
                                        modifier = Modifier.size(21.dp)
                                    )
                                    Text(
                                        rule.title,
                                        modifier = Modifier.weight(1f).padding(start = 10.dp),
                                        color = MicrobondTextPrimary,
                                        fontSize = 12.sp
                                    )
                                    StatusBadge(rule.status)
                                }
                            }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 70.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Check, null, tint = MicrobondActiveGreen, modifier = Modifier.size(14.dp))
                Text(
                    if (selectedTab == "Categories") "${selectedRules.size} rules selected"
                    else selectedSingleRule?.title ?: "Select one rule",
                    color = MicrobondActiveGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(6.dp))
                Text("Request these policies from the group", color = MicrobondInactiveGray, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val statusColor = when (status) {
        "New" -> MicrobondActiveGreen
        "Changed" -> MicrobondErrorColor
        else -> MicrobondActiveGreen
    }
    Box(
        modifier = Modifier
            .background(statusColor.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(status, color = statusColor, fontSize = 8.sp, textAlign = TextAlign.Center)
    }
}

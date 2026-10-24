package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.textfeild.MicrobondTextField
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondFabContentDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondPureWhite
import com.microbond.app.core.ui.theme.MicrobondSearchBarDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_groups.ui.newgroup.widgets.ContactListItem
import com.microbond.app.feature_groups.ui.newgroup.widgets.ContactSectionHeader

/**
 * Step 1 — "Add participants".
 *
 * Search bar at the top, then contacts split into two sections:
 *   - "Contacts on Microbond" — selectable with green tick
 *   - "Invite to Microbond"   — phone-only contacts; show Invite button (also selectable for UI testing)
 *
 * Selected contacts appear in a horizontal chip strip below the search bar.
 * The FAB (→) is enabled as soon as ≥1 contact is selected.
 *
 * @param contacts    Full list (both on/off Microbond)
 * @param groupType   The chosen group type from step 0; shown in the top bar
 * @param onBack      Navigate back to SelectGroupTypeScreen
 * @param onNext      Called with selected contacts → go to NewGroupScreen
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddParticipantsScreen(
    contacts: List<ContactEntry>,
    groupType: SavingsGroupType,
    onBack: () -> Unit,
    onNext: (List<ContactEntry>) -> Unit
) {
    var query    by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(emptySet<ContactEntry>()) }

    val onApp  = contacts.filter { it.isOnMicrobond }
    val offApp = contacts.filter { !it.isOnMicrobond }

    fun List<ContactEntry>.applyQuery() = if (query.isBlank()) this
    else filter {
        it.name.contains(query, ignoreCase = true) ||
        it.subtitle.contains(query, ignoreCase = true)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            AddParticipantsTopBar(
                groupType = groupType,
                selectedCount = selected.size,
                onBack = onBack
            )
        },
        floatingActionButton = {
            if (selected.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onNext(selected.toList()) },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MicrobondActiveGreen,
                    contentColor = MicrobondFabContentDark,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            // ── Search bar ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MicrobondSearchBarDark)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MicrobondInactiveGray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                MicrobondTextField(
                    value = query,
                    onValueChange = { query = it },
                    hint = "Search name or number",
                    showDivider = false,
                    textStyle = TextStyle(color = MicrobondTextPrimary, fontSize = 16.sp),
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Selected chip strip ───────────────────────────────────────────
            if (selected.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    selected.forEach { contact ->
                        SelectedContactChip(
                            contact = contact,
                            onRemove = { selected = selected - contact }
                        )
                    }
                }
            }

            // ── Contact list (two sections) ───────────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // On Microbond
                item { ContactSectionHeader("Contacts on Microbond") }
                items(onApp.applyQuery(), key = { "mb_${it.name}" }) { contact ->
                    val isSelected = contact in selected
                    ContactListItem(
                        contact = contact,
                        isSelected = isSelected,
                        onToggle = { selected = if (isSelected) selected - contact else selected + contact }
                    )
                }
                // Invite section
                item { ContactSectionHeader("Invite to Microbond") }
                items(offApp.applyQuery(), key = { "inv_${it.name}" }) { contact ->
                    val isSelected = contact in selected
                    ContactListItem(
                        contact = contact,
                        isSelected = isSelected,
                        onToggle = { selected = if (isSelected) selected - contact else selected + contact }
                    )
                }
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────
@Composable
private fun AddParticipantsTopBar(
    groupType: SavingsGroupType,
    selectedCount: Int,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MicrobondTextPrimary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Add participants",
                    color = MicrobondTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (selectedCount > 0) "$selectedCount selected · ${groupType.uiLabel}"
                           else groupType.uiLabel,
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ── Small chip showing a selected contact with an × to remove ────────────────
@Composable
private fun SelectedContactChip(
    contact: ContactEntry,
    onRemove: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            MicroBondAvatar(
                painter = null,
                name = contact.name,
                modifier = Modifier.size(50.dp),
                backgroundColor = contact.avatarColor
            )
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(MicrobondInactiveGray)
                    .combinedClickable(onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = MicrobondPureWhite,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = contact.name.substringAfterLast(" ").take(8).ifBlank { contact.name.take(8) },
            color = MicrobondTextPrimary,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

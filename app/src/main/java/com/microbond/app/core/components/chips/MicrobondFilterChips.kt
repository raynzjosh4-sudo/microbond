package com.microbond.app.core.components.chips

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@Composable
fun MicrobondChoiceChips(
    labels: List<String>,
    selectedChip: String,
    onChipSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        labels.forEach { label ->
            MicrobondChip(label, selectedChip == label) { onChipSelected(label) }
        }
    }
}

@Composable
fun MicrobondFilterChips(
    selectedChip: String,
    onChipSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    unreadCount: Int = 0,
    initialListNames: List<String> = emptyList(),
    onListCreated: (String) -> Unit = {}
) {
    val listNames = remember { mutableStateListOf<String>().apply { addAll(initialListNames) } }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))
        MicrobondChip("All", selectedChip == "All") { onChipSelected("All") }
        MicrobondChip(
            label = if (unreadCount > 0) "Unread $unreadCount" else "Unread",
            selected = selectedChip == "Unread"
        ) { onChipSelected("Unread") }
        MicrobondChip("Favorites", selectedChip == "Favorites") { onChipSelected("Favorites") }
        MicrobondChip("Groups", selectedChip == "Groups") { onChipSelected("Groups") }
        listNames.forEach { name ->
            MicrobondChip(name, selectedChip == name) { onChipSelected(name) }
        }
        MicrobondChip("New list", selected = false, showAddIcon = true) { showCreateDialog = true }
        Spacer(Modifier.width(16.dp))
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create list") },
            text = {
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("List name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    enabled = newListName.isNotBlank(),
                    onClick = {
                        val name = newListName.trim()
                        val existingName = listNames.firstOrNull { it.equals(name, ignoreCase = true) }
                        if (existingName == null) {
                            listNames.add(name)
                            onListCreated(name)
                            onChipSelected(name)
                        } else {
                            onChipSelected(existingName)
                        }
                        newListName = ""
                        showCreateDialog = false
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MicrobondDarkBackground,
            titleContentColor = MicrobondTextPrimary,
            textContentColor = Color.White
        )
    }
}

@Composable
private fun MicrobondChip(
    label: String,
    selected: Boolean,
    showAddIcon: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .then(
                if (selected) {
                    Modifier.background(MicrobondActivePillDark, CircleShape)
                } else {
                    Modifier
                        .background(MicrobondDarkBackground, CircleShape)
                        .border(1.dp, MicrobondDividerDark, CircleShape)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        if (showAddIcon) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = MicrobondActiveGreen,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = label,
            color = if (selected) MicrobondActiveGreen else MicrobondTextPrimary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1
        )
    }
}


package com.microbond.app.core.components.threadbubbles.itemscreens.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun EventScreen(onBack: () -> Unit, onPreview: (String) -> Unit) {
    var eventName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val canPreview = eventName.isNotBlank() && date.isNotBlank()

    AttachmentScreenFrame(
        title = "Event",
        description = "Create a group event with a title, date, and optional location.",
        actionLabel = "Preview event",
        actionEnabled = canPreview,
        onBack = onBack,
        onAction = { onPreview(listOf(eventName.trim(), date.trim(), location.trim()).filter { it.isNotBlank() }.joinToString(" · ")) }
    ) {
        OutlinedTextField(
            value = eventName,
            onValueChange = { eventName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Event name") },
            singleLine = true
        )
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Date and time") },
            placeholder = { Text("e.g. 12 Oct, 4:00 PM") },
            singleLine = true
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Location (optional)") },
            singleLine = true
        )
        AttachmentInfoCard {
            AttachmentSectionTitle("Event details")
            Text(eventName.ifBlank { "Event name" }, color = AttachmentSecondaryText)
            Text(date.ifBlank { "Date and time" }, color = AttachmentSecondaryText)
            if (location.isNotBlank()) Text(location, color = AttachmentSecondaryText)
        }
    }
}
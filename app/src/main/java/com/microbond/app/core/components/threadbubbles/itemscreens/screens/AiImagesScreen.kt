package com.microbond.app.core.components.threadbubbles.itemscreens.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AiImagesScreen(onBack: () -> Unit, onPreview: (String) -> Unit) {
    var prompt by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Illustration") }
    val styles = listOf("Illustration", "Photo", "Poster")

    AttachmentScreenFrame(
        title = "AI images",
        description = "Describe the image you want and choose a visual style.",
        actionLabel = "Preview prompt",
        actionEnabled = prompt.isNotBlank(),
        onBack = onBack,
        onAction = { onPreview("$selectedStyle · ${prompt.trim()}") }
    ) {
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Describe an image") },
            minLines = 3
        )
        AttachmentSectionTitle("Style")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            styles.forEach { style ->
                FilterChip(
                    selected = selectedStyle == style,
                    onClick = { selectedStyle = style },
                    label = { Text(style) }
                )
            }
        }
        AttachmentInfoCard {
            AttachmentSectionTitle("Prompt preview")
            Text(prompt.ifBlank { "Your prompt will appear here." }, color = AttachmentSecondaryText)
        }
    }
}
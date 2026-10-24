package com.microbond.app.core.components.threadbubbles.itemscreens.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun LocationScreen(onBack: () -> Unit, onPreview: (String) -> Unit) {
    val context = LocalContext.current
    var place by remember { mutableStateOf("") }
    var mapStatus by remember { mutableStateOf<String?>(null) }

    AttachmentScreenFrame(
        title = "Location",
        description = "Find a place in Maps or enter a place or address to share with the group.",
        actionLabel = "Preview location",
        actionEnabled = place.isNotBlank(),
        onBack = onBack,
        onAction = { onPreview(place.trim()) }
    ) {
        OutlinedTextField(
            value = place,
            onValueChange = { place = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Place or address") },
            singleLine = true
        )
        OutlinedButton(
            onClick = {
                val mapIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=${Uri.encode(place.ifBlank { "near me" })}")
                )
                try {
                    context.startActivity(mapIntent)
                    mapStatus = "Choose a place in Maps, then enter its name here."
                } catch (_: ActivityNotFoundException) {
                    mapStatus = "No maps app is available on this device."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Maps")
        }
        mapStatus?.let { Text(it, color = AttachmentSecondaryText) }
        AttachmentInfoCard {
            AttachmentSectionTitle("Location preview")
            Text(place.ifBlank { "A place you enter will appear here." }, color = AttachmentSecondaryText)
        }
    }
}
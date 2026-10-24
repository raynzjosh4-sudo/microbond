package com.microbond.app.core.components.threadbubbles.itemscreens.screens

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun GalleryScreen(onBack: () -> Unit, onPreview: (String) -> Unit) {
    val context = LocalContext.current
    var imageName by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) imageName = cursor.getString(0).orEmpty()
            }
            if (imageName.isBlank()) imageName = uri.lastPathSegment.orEmpty()
        }
    }

    AttachmentScreenFrame(
        title = "Gallery",
        description = "Choose a photo or video from your device.",
        actionLabel = "Preview selection",
        actionEnabled = imageName.isNotBlank(),
        onBack = onBack,
        onAction = { onPreview(imageName) }
    ) {
        OutlinedButton(
            onClick = {
                imagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (imageName.isBlank()) "Choose from gallery" else "Choose another item")
        }
        AttachmentInfoCard {
            AttachmentSectionTitle("Selected media")
            Text(imageName.ifBlank { "No media selected" }, color = AttachmentSecondaryText)
        }
    }
}
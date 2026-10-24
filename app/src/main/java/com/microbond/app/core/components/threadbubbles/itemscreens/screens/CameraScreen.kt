package com.microbond.app.core.components.threadbubbles.itemscreens.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun CameraScreen(onBack: () -> Unit, onPreview: (String) -> Unit) {
    var capturedImage by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    val camera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> capturedImage = bitmap }

    AttachmentScreenFrame(
        title = "Camera",
        description = "Take a photo to attach to the conversation.",
        actionLabel = "Preview photo",
        actionEnabled = capturedImage != null,
        onBack = onBack,
        onAction = {
            capturedImage?.let { onPreview("Photo captured · ${it.width} × ${it.height}") }
        }
    ) {
        OutlinedButton(
            onClick = { camera.launch(null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (capturedImage == null) "Open camera" else "Retake photo")
        }
        capturedImage?.let { bitmap ->
            AttachmentInfoCard {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Captured photo",
                    modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
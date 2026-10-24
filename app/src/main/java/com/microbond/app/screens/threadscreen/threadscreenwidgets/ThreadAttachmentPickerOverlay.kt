package com.microbond.app.screens.threadscreen.threadscreenwidgets

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.microbond.app.core.components.threadbubbles.widgets.attachment_picker.AttachmentPickerPanel
import com.microbond.app.core.data_classes_and_enums.AttachmentOption

/**
 * Overlay wrapper for the attachment picker panel, floating above the input bar in WhatsApp style.
 */
@Composable
fun ThreadAttachmentPickerOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onHeightChanged: (Dp) -> Unit,
    onImagesSelected: (List<Uri>, String) -> Unit,
    onDepositStatusSelected: () -> Unit,
    onOptionSelected: (AttachmentOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(1f),
        contentAlignment = Alignment.BottomCenter
    ) {
        AttachmentPickerPanel(
            visible = visible,
            onDismiss = onDismiss,
            onExpandedChange = onExpandedChange,
            onHeightChanged = onHeightChanged,
            onImagesSelected = { imageUris, captionText ->
                onDismiss()
                onImagesSelected(imageUris, captionText)
            },
            onOptionSelected = { option ->
                onDismiss()
                if (option == AttachmentOption.DEPOSIT_STATUS) {
                    onDepositStatusSelected()
                } else {
                    onOptionSelected(option)
                }
            }
        )
    }
}

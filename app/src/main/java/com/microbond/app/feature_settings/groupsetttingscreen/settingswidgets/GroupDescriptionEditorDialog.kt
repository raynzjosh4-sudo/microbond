package com.microbond.app.feature_settings.groupsetttingscreen.settingswidgets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@Composable
fun GroupDescriptionEditorDialog(
    description: String,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MicrobondOverlayWidgetDark,
        title = { Text("Group description", color = MicrobondTextPrimary) },
        text = {
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                maxLines = 4,
                label = { Text("Description") }
            )
        },
        confirmButton = {
            TextButton(onClick = onSave) { Text("Save", color = MicrobondActiveGreen) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = MicrobondInactiveGray) }
        }
    )
}

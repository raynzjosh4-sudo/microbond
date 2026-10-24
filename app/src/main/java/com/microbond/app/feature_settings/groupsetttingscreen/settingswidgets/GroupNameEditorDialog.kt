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
fun GroupNameEditorDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MicrobondOverlayWidgetDark,
        title = { Text("Edit group name", color = MicrobondTextPrimary) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                label = { Text("Group name") }
            )
        },
        confirmButton = {
            TextButton(onClick = onSave) { Text("Save", color = MicrobondActiveGreen) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel", color = MicrobondInactiveGray) }
        }
    )
}

package com.microbond.app.screens.groupsetttingscreen.settingswidgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondDragHandleDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaSettingsOptionsBottomSheet(
    title: String,
    options: List<String>,
    currentSelection: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MicrobondOverlayWidgetDark,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MicrobondDragHandleDark) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MicrobondTextPrimary,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
            )

            options.forEach { option ->
                val isSelected = option == currentSelection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(option) }
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = option,
                        color = if (isSelected) MicrobondActiveGreen else MicrobondTextPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                WaSettingsDivider()
            }
        }
    }
}

@Composable
fun WaSettingsActionConfirmationDialog(
    title: String,
    text: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = MicrobondTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = text,
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = if (isDestructive) MicrobondDestructiveRed else MicrobondActiveGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissText,
                    color = MicrobondInactiveGray,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = MicrobondOverlayWidgetDark,
        titleContentColor = MicrobondTextPrimary,
        textContentColor = MicrobondInactiveGray,
        shape = RoundedCornerShape(16.dp)
    )
}

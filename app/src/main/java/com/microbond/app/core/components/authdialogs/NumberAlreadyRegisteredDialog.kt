package com.microbond.app.core.components.authdialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Dialog alerting the user that the specified phone number is already registered on this device.
 * Matches WhatsApp reference dialog with "Cancel" and "Go to account" actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberAlreadyRegisteredDialog(
    formattedPhoneNumber: String,
    onDismissRequest: () -> Unit = {},
    onCancelClick: () -> Unit,
    onGoToAccountClick: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MicrobondOverlayWidgetDark,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 22.dp)
            ) {
                Text(
                    text = "$formattedPhoneNumber is already registered on this device.",
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onCancelClick) {
                        Text(
                            text = "Cancel",
                            style = TextStyle(
                                color = MicrobondActiveGreen,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = onGoToAccountClick) {
                        Text(
                            text = "Go to account",
                            style = TextStyle(
                                color = MicrobondActiveGreen,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun NumberAlreadyRegisteredDialogPreview() {
    NumberAlreadyRegisteredDialog(
        formattedPhoneNumber = "+256 746 010410",
        onCancelClick = {},
        onGoToAccountClick = {}
    )
}

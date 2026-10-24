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
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * "Is this the correct number?" Phone Confirmation Dialog.
 * Matches WhatsApp reference dialog with formatted phone number and "Edit" / "Yes" actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmNumberDialog(
    formattedPhoneNumber: String,
    onDismissRequest: () -> Unit = {},
    onEditClick: () -> Unit,
    onYesClick: () -> Unit
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
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Is this the correct number?",
                    style = TextStyle(
                        color = MicrobondInactiveGray,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = formattedPhoneNumber,
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onEditClick) {
                        Text(
                            text = "Edit",
                            style = TextStyle(
                                color = MicrobondActiveGreen,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = onYesClick) {
                        Text(
                            text = "Yes",
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
private fun ConfirmNumberDialogPreview() {
    ConfirmNumberDialog(
        formattedPhoneNumber = "+256 704 903741",
        onEditClick = {},
        onYesClick = {}
    )
}

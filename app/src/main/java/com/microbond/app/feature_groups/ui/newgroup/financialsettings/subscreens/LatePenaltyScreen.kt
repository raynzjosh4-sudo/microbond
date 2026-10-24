package com.microbond.app.feature_groups.ui.newgroup.financialsettings.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondInactiveGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LatePenaltyScreen(
    currentValue: String,
    onSave: (String) -> Unit,
    onBack: () -> Unit
) {
    // Extract numbers from "UGX 5,000" string
    val initialAmount = currentValue.replace(Regex("[^0-9]"), "")
    var amount by remember { mutableStateOf(initialAmount) }
    
    AlertDialog(
        onDismissRequest = onBack,
        containerColor = MicrobondDarkBackground,
        titleContentColor = MicrobondTextPrimary,
        textContentColor = MicrobondTextPrimary,
        title = {
            Text(text = "Late payment penalty", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            amount = newValue
                        }
                    },
                    label = { Text("Penalty Amount (UGX)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MicrobondTextPrimary,
                        unfocusedTextColor = MicrobondTextPrimary,
                        focusedBorderColor = MicrobondActiveGreen,
                        focusedLabelColor = MicrobondActiveGreen,
                        cursorColor = MicrobondActiveGreen
                    )
                )
                
                Text(
                    text = "Enter the fine amount members must pay if they miss their contribution deadline.",
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val formattedAmount = try {
                        val parsed = amount.toLong()
                        if (parsed == 0L) "None" else String.format("UGX %,d", parsed)
                    } catch (e: Exception) {
                        if (amount.isBlank()) "None" else "UGX $amount"
                    }
                    onSave(formattedAmount)
                },
                enabled = true,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MicrobondActiveGreen,
                    disabledContentColor = MicrobondInactiveGray
                )
            ) {
                Text("SAVE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MicrobondActiveGreen
                )
            ) {
                Text("CANCEL", fontWeight = FontWeight.Bold)
            }
        }
    )
}

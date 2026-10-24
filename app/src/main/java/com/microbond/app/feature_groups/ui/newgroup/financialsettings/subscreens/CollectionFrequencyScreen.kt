package com.microbond.app.feature_groups.ui.newgroup.financialsettings.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionFrequencyScreen(
    currentValue: String,
    onSave: (String) -> Unit,
    onBack: () -> Unit
) {
    // Parse initial value, e.g., "8 days, at 02:00 AM"
    var daysStr = "7"
    var timeStr = "08:00 AM"
    
    if (currentValue.contains("days, at")) {
        val parts = currentValue.split("days, at")
        daysStr = parts[0].trim()
        timeStr = parts[1].trim()
    } else if (currentValue == "Daily") {
        daysStr = "1"
    } else if (currentValue == "Weekly") {
        daysStr = "7"
    } else if (currentValue == "Bi-Weekly") {
        daysStr = "14"
    } else if (currentValue == "Monthly") {
        daysStr = "30"
    }
    
    var daysInput by remember { mutableStateOf(daysStr) }
    
    // Simple state for time to avoid complex TimePicker logic for now
    var hourInput by remember { mutableStateOf(timeStr.split(":")[0]) }
    var minInput by remember { mutableStateOf(timeStr.split(":")[1].split(" ")[0]) }
    var amPm by remember { mutableStateOf(if (timeStr.contains("PM")) "PM" else "AM") }

    AlertDialog(
        onDismissRequest = onBack,
        containerColor = MicrobondDarkBackground,
        titleContentColor = MicrobondTextPrimary,
        textContentColor = MicrobondTextPrimary,
        title = {
            Text(text = "Collection frequency", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        },
        text = {
            Column {
                Text(text = "Repeat every", fontSize = 14.sp, color = MicrobondInactiveGray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = daysInput,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                                daysInput = newValue
                            }
                        },
                        modifier = Modifier.width(80.dp),
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
                    Text(text = " days", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(text = "At time", fontSize = 14.sp, color = MicrobondInactiveGray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = hourInput,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                                hourInput = newValue
                            }
                        },
                        modifier = Modifier.width(60.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MicrobondTextPrimary,
                            unfocusedTextColor = MicrobondTextPrimary,
                            focusedBorderColor = MicrobondActiveGreen,
                            cursorColor = MicrobondActiveGreen
                        )
                    )
                    Text(text = " : ", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                    OutlinedTextField(
                        value = minInput,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                                minInput = newValue
                            }
                        },
                        modifier = Modifier.width(60.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MicrobondTextPrimary,
                            unfocusedTextColor = MicrobondTextPrimary,
                            focusedBorderColor = MicrobondActiveGreen,
                            cursorColor = MicrobondActiveGreen
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // AM/PM Toggle
                    Surface(
                        color = if (amPm == "AM") MicrobondActiveGreen.copy(alpha = 0.2f) else MicrobondDarkBackground,
                        onClick = { amPm = "AM" },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("AM", color = if (amPm == "AM") MicrobondActiveGreen else MicrobondInactiveGray, modifier = Modifier.padding(8.dp))
                    }
                    Surface(
                        color = if (amPm == "PM") MicrobondActiveGreen.copy(alpha = 0.2f) else MicrobondDarkBackground,
                        onClick = { amPm = "PM" },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("PM", color = if (amPm == "PM") MicrobondActiveGreen else MicrobondInactiveGray, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", 
                        hourInput.toIntOrNull() ?: 8, 
                        minInput.toIntOrNull() ?: 0, 
                        amPm)
                    val formatted = "${daysInput.ifBlank { "1" }} days, at $formattedTime"
                    onSave(formatted)
                },
                enabled = daysInput.isNotBlank() && hourInput.isNotBlank() && minInput.isNotBlank(),
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

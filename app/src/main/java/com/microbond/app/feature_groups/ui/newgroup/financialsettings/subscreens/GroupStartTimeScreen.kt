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
fun GroupStartTimeScreen(
    currentValue: String,
    onSave: (String) -> Unit,
    onBack: () -> Unit
) {
    var dayInput by remember { mutableStateOf("01") }
    var monthInput by remember { mutableStateOf("01") }
    var yearInput by remember { mutableStateOf("2026") }

    var hourInput by remember { mutableStateOf("08") }
    var minInput by remember { mutableStateOf("00") }
    var amPm by remember { mutableStateOf("AM") }
    
    var isManual by remember { mutableStateOf(!currentValue.contains("group is full")) }

    AlertDialog(
        onDismissRequest = onBack,
        containerColor = MicrobondDarkBackground,
        titleContentColor = MicrobondTextPrimary,
        textContentColor = MicrobondTextPrimary,
        title = {
            Text(text = "Group start time", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                    RadioButton(
                        selected = !isManual,
                        onClick = { isManual = false },
                        colors = RadioButtonDefaults.colors(selectedColor = MicrobondActiveGreen)
                    )
                    Text("Starts automatically when full", color = MicrobondTextPrimary)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    RadioButton(
                        selected = isManual,
                        onClick = { isManual = true },
                        colors = RadioButtonDefaults.colors(selectedColor = MicrobondActiveGreen)
                    )
                    Text("Pick a specific date & time", color = MicrobondTextPrimary)
                }

                if (isManual) {
                    Text(text = "Date (DD / MM / YYYY)", fontSize = 13.sp, color = MicrobondInactiveGray, modifier = Modifier.padding(top = 8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        OutlinedTextField(
                            value = dayInput,
                            onValueChange = { if (it.length <= 2) dayInput = it },
                            modifier = Modifier.width(60.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MicrobondTextPrimary,
                                unfocusedTextColor = MicrobondTextPrimary,
                                focusedBorderColor = MicrobondActiveGreen
                            )
                        )
                        Text(text = "/", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 8.dp), color = MicrobondInactiveGray)
                        OutlinedTextField(
                            value = monthInput,
                            onValueChange = { if (it.length <= 2) monthInput = it },
                            modifier = Modifier.width(60.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MicrobondTextPrimary,
                                unfocusedTextColor = MicrobondTextPrimary,
                                focusedBorderColor = MicrobondActiveGreen
                            )
                        )
                        Text(text = "/", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 8.dp), color = MicrobondInactiveGray)
                        OutlinedTextField(
                            value = yearInput,
                            onValueChange = { if (it.length <= 4) yearInput = it },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MicrobondTextPrimary,
                                unfocusedTextColor = MicrobondTextPrimary,
                                focusedBorderColor = MicrobondActiveGreen
                            )
                        )
                    }

                    Text(text = "Time", fontSize = 13.sp, color = MicrobondInactiveGray, modifier = Modifier.padding(top = 16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        OutlinedTextField(
                            value = hourInput,
                            onValueChange = { if (it.length <= 2) hourInput = it },
                            modifier = Modifier.width(60.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MicrobondTextPrimary,
                                unfocusedTextColor = MicrobondTextPrimary,
                                focusedBorderColor = MicrobondActiveGreen
                            )
                        )
                        Text(text = ":", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 8.dp), color = MicrobondInactiveGray)
                        OutlinedTextField(
                            value = minInput,
                            onValueChange = { if (it.length <= 2) minInput = it },
                            modifier = Modifier.width(60.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MicrobondTextPrimary,
                                unfocusedTextColor = MicrobondTextPrimary,
                                focusedBorderColor = MicrobondActiveGreen
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!isManual) {
                        onSave("Starts when group is full")
                    } else {
                        onSave("$dayInput/$monthInput/$yearInput at $hourInput:$minInput $amPm")
                    }
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MicrobondActiveGreen)
            ) {
                Text("SAVE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onBack, colors = ButtonDefaults.textButtonColors(contentColor = MicrobondActiveGreen)) {
                Text("CANCEL", fontWeight = FontWeight.Bold)
            }
        }
    )
}

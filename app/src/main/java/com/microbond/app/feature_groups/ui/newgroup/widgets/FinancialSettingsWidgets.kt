package com.microbond.app.feature_groups.ui.newgroup.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.microbond.app.core.components.switchbutton.MicrobondSwitch
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@Composable
fun SettingsSelectionRow(
    icon: ImageVector,
    title: String,
    value: String,
    description: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MicrobondInactiveGray,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MicrobondTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = value,
                    color = MicrobondActiveGreen,
                    fontSize = 14.sp
                )
                if (description != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = description,
                        color = MicrobondInactiveGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MicrobondInactiveGray
            )
        }
        HorizontalDivider(
            color = MicrobondDividerDark,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 52.dp)
        )
    }
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MicrobondInactiveGray,
                modifier = Modifier
                    .size(22.dp)
                    .padding(top = 2.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MicrobondTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    color = MicrobondInactiveGray,
                    fontSize = 14.sp
                )
            }

            MicrobondSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }

        HorizontalDivider(
            color = MicrobondDividerDark,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 52.dp)
        )
    }
}

@Composable
fun FullScreenSettingPicker(
    title: String,
    currentValue: String,
    options: List<String>,
    onSave: (String) -> Unit,
    onBack: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onBack,
        containerColor = com.microbond.app.core.ui.theme.MicrobondDarkBackground,
        titleContentColor = com.microbond.app.core.ui.theme.MicrobondTextPrimary,
        textContentColor = com.microbond.app.core.ui.theme.MicrobondTextPrimary,
        title = {
            androidx.compose.material3.Text(
                text = title, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Column {
                options.forEach { option ->
                    val isSelected = option == currentValue
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSave(option); onBack() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = isSelected,
                            onClick = { onSave(option); onBack() },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                selectedColor = com.microbond.app.core.ui.theme.MicrobondActiveGreen,
                                unselectedColor = com.microbond.app.core.ui.theme.MicrobondInactiveGray
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        androidx.compose.material3.Text(
                            text = option,
                            color = com.microbond.app.core.ui.theme.MicrobondTextPrimary,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onBack) {
                androidx.compose.material3.Text(
                    text = "CANCEL",
                    color = com.microbond.app.core.ui.theme.MicrobondActiveGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

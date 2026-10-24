package com.microbond.app.screens.groupsetttingscreen.settingswidgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

// ── WhatsApp-style settings primitives ────────────────────────────────────────
// Dividers ONLY between major sections, NOT between individual rows.

@Composable
fun WaSettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = MicrobondInactiveGray,
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun WaSettingsDivider(
    dividerColor: Color = MicrobondDividerDark
) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(dividerColor)
    )
}

// Simple tappable row — no trailing chevron, just tap the whole row
@Composable
fun WaSettingsRow(
    icon: ImageVector?,
    title: String,
    subtitle: String = "",
    titleColor: Color = MicrobondTextPrimary,
    iconTint: Color = MicrobondInactiveGray,
    onClick: (() -> Unit)? = null
) {
    val mod = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(mod)
            .padding(start = 20.dp, end = 16.dp, top = 13.dp, bottom = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(20.dp))
        } else {
            Spacer(Modifier.width(42.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = titleColor, fontSize = 16.sp)
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

// Row with value label on the trailing side (e.g. "All", "Off", "90 days")
@Composable
fun WaSettingsValueRow(
    icon: ImageVector?,
    title: String,
    value: String,
    subtitle: String = "",
    onClick:  (() -> Unit)? = null
) {
    val mod = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(mod)
            .padding(start = 20.dp, end = 16.dp, top = 13.dp, bottom = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MicrobondInactiveGray,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(20.dp))
        } else {
            Spacer(Modifier.width(42.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MicrobondTextPrimary, fontSize = 16.sp)
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Text(value, color = MicrobondInactiveGray, fontSize = 13.sp)
    }
}

// Row with a toggle on the trailing side
@Composable
fun WaSettingsSwitchRow(
    icon: ImageVector?,
    title: String,
    subtitle: String = "",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MicrobondInactiveGray,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(20.dp))
        } else {
            Spacer(Modifier.width(42.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MicrobondTextPrimary, fontSize = 16.sp)
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MicrobondActiveGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MicrobondInactiveGray.copy(alpha = 0.4f),
                uncheckedBorderColor = Color.Transparent,
                checkedBorderColor = Color.Transparent
            )
        )
    }
}

// Row with a radio button on the trailing side
@Composable
fun WaSettingsRadioRow(
    title: String,
    subtitle: String = "",
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 24.dp, end = 16.dp, top = 13.dp, bottom = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MicrobondTextPrimary, fontSize = 16.sp)
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MicrobondActiveGreen,
                unselectedColor = MicrobondInactiveGray
            )
        )
    }
}

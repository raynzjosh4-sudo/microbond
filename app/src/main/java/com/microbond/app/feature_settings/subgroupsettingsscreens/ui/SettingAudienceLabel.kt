package com.microbond.app.feature_settings.subgroupsettingsscreens.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen

/** Small label telling admins who can see a setting (e.g. "Admins only"). */
@Composable
internal fun SettingAudienceLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MicrobondActiveGreen,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier.padding(top = 3.dp)
    )
}

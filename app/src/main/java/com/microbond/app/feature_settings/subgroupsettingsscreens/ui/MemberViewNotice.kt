package com.microbond.app.feature_settings.subgroupsettingsscreens.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Shown to members: explains that some settings are hidden or read-only. */
@Composable
internal fun MemberViewNotice() {
    Text(
        text = "Some settings are managed by group admins and may be hidden or read-only.",
        color = Color(0xFFFFA726),
        fontSize = 12.sp,
        lineHeight = 17.sp,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A1F00))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    )
}

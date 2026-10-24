package com.microbond.app.feature_groups.ui.newgroup.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen

/** Green section label (e.g. "Contacts on Microbond" / "Invite to Microbond") */
@Composable
fun ContactSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        color = MicrobondActiveGreen,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}

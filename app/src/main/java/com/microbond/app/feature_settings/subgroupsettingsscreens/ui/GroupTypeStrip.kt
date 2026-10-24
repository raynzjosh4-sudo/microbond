package com.microbond.app.feature_settings.subgroupsettingsscreens.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/** WhatsApp-style group info strip — no card, just text. */
@Composable
internal fun GroupTypeStrip(
    groupType: SavingsGroupType,
    memberCount: Int,
    caption: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = groupType.uiLabel,
            color = MicrobondTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "$memberCount members · $caption",
            color = MicrobondInactiveGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

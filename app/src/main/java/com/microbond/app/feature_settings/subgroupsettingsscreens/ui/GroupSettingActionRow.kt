package com.microbond.app.feature_settings.subgroupsettingsscreens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

@Composable
internal fun GroupSettingActionRow(
    setting: GroupSetting,
    onClick: () -> Unit,
    audienceHint: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = iconFor(setting.title),
            contentDescription = null,
            tint = MicrobondInactiveGray,
            modifier = Modifier.size(22.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 18.dp, end = 8.dp)
        ) {
            Text(setting.title, color = MicrobondTextPrimary, fontSize = 15.sp)
            if (setting.description.isNotBlank()) {
                Text(
                    setting.description,
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            audienceHint?.let { SettingAudienceLabel(it) }
        }
        setting.value?.let {
            Text(
                text = it,
                color = MicrobondInactiveGray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        } ?: Text("›", color = MicrobondInactiveGray, fontSize = 22.sp)
    }
}

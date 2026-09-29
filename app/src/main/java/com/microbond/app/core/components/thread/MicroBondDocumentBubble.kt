package com.microbond.app.core.components.thread

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MicroBondDocumentBubble(
    name: String,
    size: String,
    meta: MicroBondMeta,
    modifier: Modifier = Modifier
) {
    MicroBondBubbleSurface(meta, modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.InsertDriveFile,
                contentDescription = null,
                tint = MicroBondAccent,
                modifier = Modifier.size(36.dp)
            )
            Column {
                Text(name, color = MicroBondInk, fontSize = 14.sp)
                Text(size, color = MicroBondMuted, fontSize = 11.sp)
            }
        }
    }
}

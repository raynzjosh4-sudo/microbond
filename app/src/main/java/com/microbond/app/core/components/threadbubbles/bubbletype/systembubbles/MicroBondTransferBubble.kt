package com.microbond.app.core.components.threadbubbles.bubbletype.systembubbles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondTransferBubble(amount: String, note: String, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(meta, modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet,
                null,
                tint = Color(0xFF7ED7C4),
                modifier = Modifier.size(36.dp)
            ); Column {
            Text(amount, color = MicroBondInk, fontSize = 18.sp); Text(
            note,
            color = MicroBondMuted,
            fontSize = 12.sp
        )
        }
        }
    }
}

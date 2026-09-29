package com.microbond.app.core.components.thread

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MicroBondPollBubble(
    question: String,
    options: List<String>,
    meta: MicroBondMeta,
    modifier: Modifier = Modifier
) {
    MicroBondBubbleSurface(meta, modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(question, color = MicroBondInk, fontSize = 15.sp)
            options.forEachIndexed { index, option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (index == 0) Icons.Default.RadioButtonChecked
                        else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = MicroBondAccent
                    )
                    Text(
                        option,
                        color = MicroBondInk,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
    }
}

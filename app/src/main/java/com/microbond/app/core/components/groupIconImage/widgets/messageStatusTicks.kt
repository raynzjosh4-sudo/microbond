package com.microbond.app.core.components.groupIconImage.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondMassageReadColor

@Composable
fun MessageStatusTicks(
    status: MessageStatus,
    modifier: Modifier = Modifier,
    iconSize: Dp = 16.dp,
    readColor: Color = MicrobondMassageReadColor,
    unreadColor: Color = MicrobondInactiveGray,
    errorColor: Color = MicrobondErrorColor
){
    when (status){
        MessageStatus.PENDING -> {
            Icon(imageVector = Icons.Default.Done,
                contentDescription = "sent",
                tint = unreadColor,
                modifier = modifier.size(iconSize)
            )
        }

        MessageStatus.DELIVERED -> {
            Icon(imageVector = Icons.Default.DoneAll,
                contentDescription = "Delivered",
                tint = readColor,
                modifier = modifier.size(iconSize)
            )
        }

        MessageStatus.READ -> {
            Icon(imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Failed",
                tint = errorColor,
                modifier = modifier.size(iconSize)
            )
        }


        else -> {

        }
    }
}
package com.microbond.app.core.components.thread

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@Composable
fun ThreadInputBar(
    inputValue: String,
    onValueChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E2329))
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            IconButton(onClick = { /* Toggle Emoji */ }) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEmotions,
                    contentDescription = "Emoji",
                    tint = MicrobondInactiveGray
                )
            }

            TextField(
                value = inputValue,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                placeholder = {
                    Text("Message...", color = MicrobondInactiveGray, fontSize = 16.sp)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = MicrobondTextPrimary,
                    unfocusedTextColor = MicrobondTextPrimary,
                    cursorColor = MicrobondActiveGreen,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 6
            )

            IconButton(onClick = { /* Attach File/Receipt */ }) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach Document",
                    tint = MicrobondInactiveGray
                )
            }
            if (inputValue.isBlank()) {
                IconButton(onClick = { /* Open Camera */ }) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        tint = MicrobondInactiveGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MicrobondActiveGreen)
                .clickable { if (inputValue.isNotBlank()) onSubmitClick() else onMicClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (inputValue.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
                contentDescription = if (inputValue.isNotBlank()) "Send" else "Microphone",
                tint = MicrobondDarkBackground,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

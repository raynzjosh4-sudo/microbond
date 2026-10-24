package com.microbond.app.core.components.threadbubbles.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.data_classes_and_enums.AttachmentItem
import com.microbond.app.core.data_classes_and_enums.AttachmentOption

private val attachmentItems = listOf(
    AttachmentItem(AttachmentOption.GALLERY,   Icons.Default.Image,       "Gallery",   Color(0xFFFFFFFF), Color(0xFF7B61FF)),
    AttachmentItem(AttachmentOption.CAMERA,    Icons.Default.CameraAlt,   "Camera",    Color(0xFFFFFFFF), Color(0xFFE53935)),
    AttachmentItem(AttachmentOption.LOCATION,  Icons.Default.LocationOn,  "Location",  Color(0xFFFFFFFF), Color(0xFF43A047)),
    AttachmentItem(AttachmentOption.CONTACT,   Icons.Default.Person,      "Contact",   Color(0xFFFFFFFF), Color(0xFF1E88E5)),
    AttachmentItem(AttachmentOption.DOCUMENT,  Icons.Default.Article,     "Document",  Color(0xFFFFFFFF), Color(0xFF00ACC1)),
    AttachmentItem(AttachmentOption.POLL,      Icons.Default.HowToVote,   "Poll",      Color(0xFFFFFFFF), Color(0xFF8E24AA)),
    AttachmentItem(AttachmentOption.EVENT,     Icons.Default.Event,       "Event",     Color(0xFFFFFFFF), Color(0xFFFF6F00)),
    AttachmentItem(AttachmentOption.AI_IMAGES, Icons.Default.AutoAwesome, "AI Images", Color(0xFFFFFFFF), Color(0xFF00897B)),
)

/**
 * WhatsApp-style attachment panel that slides up from below the input bar.
 * The input bar stays visible above — this panel does NOT cover it.
 *
 * Place this composable directly in the Column that contains [ThreadInputBar],
 * positioned AFTER the input bar so it appears below it.
 *
 * @param visible Whether the panel is expanded.
 * @param onOptionSelected Called with the chosen [AttachmentOption].
 */
@Composable
fun AttachmentPickerPanel(
    visible: Boolean,
    onOptionSelected: (AttachmentOption) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1F2C34))
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Drag handle visual hint
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3B4A54))
            )

            // Two rows of 4 icons each
            attachmentItems.chunked(4).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { item ->
                        AttachmentCell(item = item, onClick = { onOptionSelected(item.option) })
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AttachmentCell(item: AttachmentItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(item.background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = item.tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = item.label,
            color = Color(0xFFE9EDEF),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

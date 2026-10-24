package com.microbond.app.feature_groups.ui.newgroup.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondBottomSheetDark
import com.microbond.app.core.ui.theme.MicrobondDragHandleGray
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupIconBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onOpenGallery: () -> Unit,
    title: String = "Group icon",
    onCameraClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MicrobondBottomSheetDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MicrobondDragHandleGray)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismissRequest, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MicrobondTextPrimary)
                }
                Spacer(Modifier.width(24.dp))
                Text(title, color = MicrobondTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))

            BottomSheetRow(
                icon = Icons.Default.CameraAlt,
                text = "Camera",
                onClick = { onCameraClick?.invoke() }
            )
            BottomSheetRow(icon = Icons.Default.Image, text = "Gallery", onClick = onOpenGallery)
            BottomSheetRow(icon = Icons.Default.EmojiEmotions, text = "Emoji & stickers", onClick = { })
            BottomSheetRow(icon = Icons.Default.Search, text = "Search web", onClick = { })
            BottomSheetRow(icon = Icons.Default.AutoAwesome, text = "AI images", onClick = { })

            if (onDeleteClick != null) {
                BottomSheetRow(
                    icon = Icons.Default.Delete,
                    text = "Remove photo",
                    iconTint = Color(0xFFFF4F4F),
                    textColor = Color(0xFFFF4F4F),
                    onClick = { onDeleteClick.invoke() }
                )
            }
        }
    }
}

@Composable
private fun BottomSheetRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    iconTint: Color = MicrobondInactiveGray,
    textColor: Color = MicrobondTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(24.dp))
        Text(text, color = textColor, fontSize = 16.sp)
    }
}

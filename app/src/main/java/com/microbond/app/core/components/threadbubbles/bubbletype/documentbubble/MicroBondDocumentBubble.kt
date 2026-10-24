package com.microbond.app.core.components.threadbubbles.bubbletype.documentbubble

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondAccent
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondDocumentBubble(
    name: String,
    size: String,
    meta: MicroBondMeta,
    uri: String? = null,
    fileType: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extension = name.substringAfterLast('.', fileType?.substringAfterLast('/') ?: "FILE")
        .take(4)
        .uppercase()
    val showTypeBadge = extension == "WEBP"
    val sizeLabel = if (showTypeBadge) "$size · $extension" else size
    val previewBackground = if (meta.outgoing) Color(0xFF00483B) else Color(0xFF172126)
    MicroBondBubbleSurface(meta, modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(previewBackground)
                .clickable(enabled = uri != null) {
                    val openIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(uri), fileType ?: "*/*")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    try {
                        context.startActivity(openIntent)
                    } catch (_: ActivityNotFoundException) {
                    }
                }
                .padding(horizontal = 5.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (showTypeBadge) {
                Box(
                    modifier = Modifier
                        .size(width = 30.dp, height = 36.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF879195)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(21.dp)
                        )
                        Text(extension, color = Color.White, fontSize = 6.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Icon(
                    Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = MicroBondAccent,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    color = MicroBondInk,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(sizeLabel, color = MicroBondMuted, fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}

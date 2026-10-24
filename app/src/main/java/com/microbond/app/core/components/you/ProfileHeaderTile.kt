package com.microbond.app.core.components.you

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileHeaderTile(
    name: String,
    about: String,
    initials: String,
    avatarUrl: String? = null,
    onProfileClick: () -> Unit,
    onQrCodeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val avatarBitmap by produceState<ImageBitmap?>(initialValue = null, avatarUrl) {
        if (!avatarUrl.isNullOrBlank()) {
            value = withContext(Dispatchers.IO) {
                try {
                    if (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://")) {
                        val connection = java.net.URL(avatarUrl).openConnection() as java.net.HttpURLConnection
                        connection.doInput = true
                        connection.connectTimeout = 8000
                        connection.readTimeout = 8000
                        connection.connect()
                        connection.inputStream.use { input ->
                            android.graphics.BitmapFactory.decodeStream(input)?.asImageBitmap()
                        }
                    } else if (avatarUrl.startsWith("content://") || avatarUrl.startsWith("file://")) {
                        context.contentResolver.openInputStream(android.net.Uri.parse(avatarUrl))?.use { input ->
                            android.graphics.BitmapFactory.decodeStream(input)?.asImageBitmap()
                        }
                    } else {
                        null
                    }
                } catch (_: Exception) {
                    null
                }
            }
        } else {
            value = null
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onProfileClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MicrobondActivePillDark),
            contentAlignment = Alignment.Center
        ) {
            val bitmap = avatarBitmap
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = initials,
                    color = MicrobondTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = MicrobondTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = about,
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        IconButton(onClick = onQrCodeClick) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = "My QR code",
                tint = MicrobondActiveGreen,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
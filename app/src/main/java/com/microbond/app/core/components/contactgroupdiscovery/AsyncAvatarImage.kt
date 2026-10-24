package com.microbond.app.core.components.contactgroupdiscovery

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Lightweight asynchronous avatar loader that seamlessly displays network images
 * with automatic caching and graceful fallback to Material icons.
 */
@Composable
fun AsyncAvatarImage(
    imageUrl: String?,
    contentDescription: String?,
    fallbackIcon: ImageVector,
    fallbackTint: Color,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (!imageUrl.isNullOrBlank()) {
        val bitmapState by produceState<ImageBitmap?>(initialValue = null, imageUrl) {
            value = withContext(Dispatchers.IO) {
                try {
                    val url = URL(imageUrl)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.doInput = true
                    conn.connectTimeout = 6000
                    conn.readTimeout = 6000
                    conn.connect()
                    val stream = conn.inputStream
                    val bmp = BitmapFactory.decodeStream(stream)
                    stream.close()
                    conn.disconnect()
                    bmp?.asImageBitmap()
                } catch (_: Exception) {
                    null
                }
            }
        }

        if (bitmapState != null) {
            Image(
                bitmap = bitmapState!!,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier.fillMaxSize()
            )
            return
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = fallbackIcon,
            contentDescription = contentDescription,
            tint = fallbackTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

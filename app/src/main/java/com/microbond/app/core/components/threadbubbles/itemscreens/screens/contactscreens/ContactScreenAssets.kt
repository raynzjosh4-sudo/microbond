package com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun ContactPhotoPainter(photoUri: String?): Painter? {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, photoUri) {
        value = withContext(Dispatchers.IO) {
            photoUri?.let { value ->
                runCatching {
                    context.contentResolver.openInputStream(Uri.parse(value))?.use(BitmapFactory::decodeStream)
                }.getOrNull()
            }
        }
    }
    return bitmap?.asImageBitmap()?.let(::BitmapPainter)
}

@Composable
internal fun ContactAvatarFallback() {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = null,
        tint = Color(0xFFCFD8DC),
        modifier = Modifier.size(34.dp)
    )
}

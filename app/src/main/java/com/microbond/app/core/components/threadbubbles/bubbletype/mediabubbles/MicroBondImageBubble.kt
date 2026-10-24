package com.microbond.app.core.components.threadbubbles.bubbletype.mediabubbles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.rememberAsyncBitmap
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.components.threadbubbles.widgets.statusicon.MicroBondStatusIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

sealed class MediaImageSource {
    data class UriSource(val uri: String) : MediaImageSource()
    data class ResSource(@DrawableRes val resId: Int) : MediaImageSource()
    data class PainterSource(val painter: Painter) : MediaImageSource()
}

@Composable
fun MicroBondImageBubble(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    caption: String? = null,
    meta: MicroBondMeta,
    imageUri: String? = null,
    imageUris: List<String>? = null,
    @DrawableRes imageRes: Int? = null,
    imageResList: List<Int>? = null,
    aspectRatio: Float? = null,
    onImageClick: ((Int) -> Unit)? = null
) {
    val allImages: List<MediaImageSource> = remember(imageUris, imageResList, imageUri, imageRes, painter) {
        when {
            !imageUris.isNullOrEmpty() -> imageUris.map { MediaImageSource.UriSource(it) }
            !imageResList.isNullOrEmpty() -> imageResList.map { MediaImageSource.ResSource(it) }
            imageUri != null -> listOf(MediaImageSource.UriSource(imageUri))
            imageRes != null && imageRes != 0 -> listOf(MediaImageSource.ResSource(imageRes))
            painter != null -> listOf(MediaImageSource.PainterSource(painter))
            else -> emptyList()
        }
    }

    MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        showTimestamp = false,
        contentPadding = PaddingValues(
            start = if (meta.outgoing) 2.dp else 10.dp,
            end = if (meta.outgoing) 10.dp else 2.dp,
            top = 2.dp,
            bottom = 2.dp
        )
    ) {
        Column {
            if (allImages.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    when (allImages.size) {
                        1 -> SingleImageLayout(
                            image = allImages[0],
                            explicitAspectRatio = aspectRatio,
                            onClick = { onImageClick?.invoke(0) }
                        )
                        2 -> TwoImagesLayout(
                            images = allImages,
                            explicitAspectRatio = aspectRatio,
                            onClick = onImageClick
                        )
                        3 -> ThreeImagesLayout(
                            images = allImages,
                            explicitAspectRatio = aspectRatio,
                            onClick = onImageClick
                        )
                        4 -> FourImagesLayout(
                            images = allImages,
                            explicitAspectRatio = aspectRatio,
                            onClick = onImageClick
                        )
                        else -> MultipleImagesGrid(
                            images = allImages,
                            explicitAspectRatio = aspectRatio,
                            onClick = onImageClick
                        )
                    }

                    // If no caption, overlay the timestamp on the image grid in bottom-right corner
                    if (caption.isNullOrBlank()) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.55f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(meta.time, color = Color.White, fontSize = 11.sp)
                            if (meta.outgoing) MicroBondStatusIcon(meta.status)
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color(0xFF1B2B33), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, null, tint = MicroBondMuted)
                }
            }

            if (!caption.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = caption,
                        color = MicroBondInk,
                        fontSize = 15.sp,
                        // Leave space for the floating timestamp
                        modifier = Modifier.padding(bottom = 8.dp, end = 44.dp)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(meta.time, color = MicroBondMuted, fontSize = 11.sp)
                        if (meta.outgoing) MicroBondStatusIcon(meta.status)
                    }
                }
            }
        }
    }
}

@Composable
fun rememberImageAspectRatio(source: MediaImageSource): Float? {
    val context = LocalContext.current
    return produceState<Float?>(initialValue = null, source) {
        value = withContext(Dispatchers.IO) {
            when (source) {
                is MediaImageSource.ResSource -> {
                    try {
                        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        BitmapFactory.decodeResource(context.resources, source.resId, options)
                        if (options.outWidth > 0 && options.outHeight > 0) {
                            options.outWidth.toFloat() / options.outHeight.toFloat()
                        } else null
                    } catch (_: Exception) {
                        null
                    }
                }
                is MediaImageSource.UriSource -> {
                    try {
                        val uri = Uri.parse(source.uri)
                        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        context.contentResolver.openInputStream(uri)?.use {
                            BitmapFactory.decodeStream(it, null, options)
                        }
                        if (options.outWidth > 0 && options.outHeight > 0) {
                            options.outWidth.toFloat() / options.outHeight.toFloat()
                        } else null
                    } catch (_: Exception) {
                        null
                    }
                }
                is MediaImageSource.PainterSource -> {
                    val size = source.painter.intrinsicSize
                    if (size.isSpecified && size.width > 0 && size.height > 0) {
                        size.width / size.height
                    } else null
                }
            }
        }
    }.value
}

@Composable
private fun SingleImageLayout(
    image: MediaImageSource,
    explicitAspectRatio: Float?,
    onClick: (() -> Unit)?
) {
    val detectedRatio = rememberImageAspectRatio(image)
    val effectiveRatio = (explicitAspectRatio ?: detectedRatio ?: (4f / 3f)).coerceIn(0.65f, 1.85f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(effectiveRatio)
            .heightIn(min = 150.dp, max = 340.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        MediaGridCell(
            source = image,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TwoImagesLayout(
    images: List<MediaImageSource>,
    explicitAspectRatio: Float?,
    onClick: ((Int) -> Unit)?
) {
    val effectiveRatio = (explicitAspectRatio ?: 1.3f).coerceIn(0.8f, 1.8f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(effectiveRatio)
            .heightIn(min = 160.dp, max = 260.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .then(if (onClick != null) Modifier.clickable { onClick(0) } else Modifier)
        ) {
            MediaGridCell(source = images[0], modifier = Modifier.fillMaxSize())
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .then(if (onClick != null) Modifier.clickable { onClick(1) } else Modifier)
        ) {
            MediaGridCell(source = images[1], modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun ThreeImagesLayout(
    images: List<MediaImageSource>,
    explicitAspectRatio: Float?,
    onClick: ((Int) -> Unit)?
) {
    val effectiveRatio = (explicitAspectRatio ?: 1.15f).coerceIn(0.85f, 1.6f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(effectiveRatio)
            .heightIn(min = 190.dp, max = 280.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Left column (tall single image)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .then(if (onClick != null) Modifier.clickable { onClick(0) } else Modifier)
        ) {
            MediaGridCell(source = images[0], modifier = Modifier.fillMaxSize())
        }

        // Right column (2 stacked images)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .then(if (onClick != null) Modifier.clickable { onClick(1) } else Modifier)
            ) {
                MediaGridCell(source = images[1], modifier = Modifier.fillMaxSize())
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .then(if (onClick != null) Modifier.clickable { onClick(2) } else Modifier)
            ) {
                MediaGridCell(source = images[2], modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun FourImagesLayout(
    images: List<MediaImageSource>,
    explicitAspectRatio: Float?,
    onClick: ((Int) -> Unit)?
) {
    val effectiveRatio = (explicitAspectRatio ?: 1.15f).coerceIn(0.85f, 1.6f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(effectiveRatio)
            .heightIn(min = 200.dp, max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Top row
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(if (onClick != null) Modifier.clickable { onClick(0) } else Modifier)
            ) {
                MediaGridCell(source = images[0], modifier = Modifier.fillMaxSize())
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(if (onClick != null) Modifier.clickable { onClick(1) } else Modifier)
            ) {
                MediaGridCell(source = images[1], modifier = Modifier.fillMaxSize())
            }
        }

        // Bottom row
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(if (onClick != null) Modifier.clickable { onClick(2) } else Modifier)
            ) {
                MediaGridCell(source = images[2], modifier = Modifier.fillMaxSize())
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(if (onClick != null) Modifier.clickable { onClick(3) } else Modifier)
            ) {
                MediaGridCell(source = images[3], modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun MultipleImagesGrid(
    images: List<MediaImageSource>,
    explicitAspectRatio: Float?,
    onClick: ((Int) -> Unit)?
) {
    val remainingCount = images.size - 3
    val effectiveRatio = (explicitAspectRatio ?: 1.15f).coerceIn(0.85f, 1.6f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(effectiveRatio)
            .heightIn(min = 200.dp, max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Top row
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(if (onClick != null) Modifier.clickable { onClick(0) } else Modifier)
            ) {
                MediaGridCell(source = images[0], modifier = Modifier.fillMaxSize())
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(if (onClick != null) Modifier.clickable { onClick(1) } else Modifier)
            ) {
                MediaGridCell(source = images[1], modifier = Modifier.fillMaxSize())
            }
        }

        // Bottom row
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(if (onClick != null) Modifier.clickable { onClick(2) } else Modifier)
            ) {
                MediaGridCell(source = images[2], modifier = Modifier.fillMaxSize())
            }

            // 4th cell with +N overlay
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(if (onClick != null) Modifier.clickable { onClick(3) } else Modifier)
            ) {
                MediaGridCell(source = images[3], modifier = Modifier.fillMaxSize())
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+$remainingCount",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaGridCell(
    source: MediaImageSource,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    when (source) {
        is MediaImageSource.ResSource -> {
            Image(
                painter = painterResource(id = source.resId),
                contentDescription = "Photo",
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }
        is MediaImageSource.PainterSource -> {
            Image(
                painter = source.painter,
                contentDescription = "Photo",
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }
        is MediaImageSource.UriSource -> {
            val isRemote = source.uri.startsWith("http://", ignoreCase = true) ||
                source.uri.startsWith("https://", ignoreCase = true)

            val remoteBitmap = if (isRemote) rememberAsyncBitmap(source.uri) else null

            val localBitmap = if (!isRemote) {
                produceState<Bitmap?>(initialValue = null, source.uri) {
                    value = withContext(Dispatchers.IO) {
                        try {
                            val uri = Uri.parse(source.uri)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                context.contentResolver.loadThumbnail(uri, Size(800, 800), null)
                            } else {
                                decodeUriBitmap(context, uri)
                            }
                        } catch (_: Exception) {
                            null
                        }
                    }
                }.value
            } else null

            val bitmap = remoteBitmap ?: localBitmap
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Shared photo",
                    modifier = modifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = modifier.background(Color(0xFF1B2B33)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Loading image",
                        tint = MicroBondMuted
                    )
                }
            }
        }
    }
}

private fun decodeUriBitmap(context: Context, uri: Uri): Bitmap? {
    val resolver = context.contentResolver
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) } ?: return null
    var sampleSize = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / sampleSize > 800) {
        sampleSize *= 2
    }
    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    return resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
}

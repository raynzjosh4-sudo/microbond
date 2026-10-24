package com.microbond.app.core.components.threadbubbles.widgets.attachment_picker

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class FolderItemType {
    MEDIA_BUCKET,
    MORE_APPS,
    SEE_MORE
}

data class MediaFolder(
    val id: String,
    val name: String,
    val count: Int,
    val thumbnailUri: Uri? = null,
    val type: FolderItemType = FolderItemType.MEDIA_BUCKET
)

@Composable
fun AlbumFolderDropdownMenu(
    visible: Boolean,
    folders: List<MediaFolder>,
    selectedFolderId: String,
    onFolderSelected: (MediaFolder) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onDismiss
            )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.92f, animationSpec = tween(180)),
            exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.92f, animationSpec = tween(150)),
            modifier = modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF1F2C34),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .width(300.dp)
                    .heightIn(max = 440.dp)
                    .border(1.dp, Color(0xFF2A3942), RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { /* prevent backdrop dismissal */ }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 6.dp)
                ) {
                    folders.forEach { folder ->
                        val isSelected = folder.id == selectedFolderId && folder.type == FolderItemType.MEDIA_BUCKET
                        FolderMenuItemRow(
                            folder = folder,
                            isSelected = isSelected,
                            onClick = {
                                onFolderSelected(folder)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderMenuItemRow(
    folder: MediaFolder,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail or Folder Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (folder.type) {
                FolderItemType.MEDIA_BUCKET -> {
                    if (folder.thumbnailUri != null) {
                        DeviceImageThumbnail(
                            imageUri = folder.thumbnailUri,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF2A3942)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = null,
                                tint = Color(0xFF8696A0),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                FolderItemType.MORE_APPS -> {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEAA61A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "More apps",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                FolderItemType.SEE_MORE -> {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.5.dp, Color(0xFF8696A0), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = "See more",
                            tint = Color(0xFF8696A0),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.width(14.dp))

        // Name and Count
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = folder.name,
                color = Color(0xFFE9EDEF),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val subtitle = when (folder.type) {
                FolderItemType.MEDIA_BUCKET -> {
                    if (folder.count > 0) "${formatNumber(folder.count)} items" else ""
                }
                FolderItemType.MORE_APPS -> "Files, +2"
                FolderItemType.SEE_MORE -> ""
            }
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF8696A0),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Trailing Checkmark or Chevron
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected folder",
                tint = Color(0xFF25D366),
                modifier = Modifier.size(20.dp)
            )
        } else if (folder.type == FolderItemType.MORE_APPS) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open more apps",
                tint = Color(0xFF8696A0),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Floating Folder Shortcut Button placed at bottom right of the expanded photo grid.
 */
@Composable
fun FloatingFolderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(14.dp)
            .size(46.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF202C33))
            .border(1.dp, Color(0xFF2A3942), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Folder,
            contentDescription = "Select album folder",
            tint = Color(0xFFE9EDEF),
            modifier = Modifier.size(22.dp)
        )
    }
}

suspend fun queryMediaFolders(context: Context, recentUris: List<Uri>): List<MediaFolder> = withContext(Dispatchers.IO) {
    val folders = mutableListOf<MediaFolder>()
    val bucketMap = mutableMapOf<String, Pair<String, MutableList<Uri>>>()

    try {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketIdCol = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameCol = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val bucketId = if (bucketIdCol != -1) cursor.getString(bucketIdCol) ?: "default" else "default"
                val bucketName = if (bucketNameCol != -1) cursor.getString(bucketNameCol) ?: "Gallery" else "Gallery"

                val entry = bucketMap.getOrPut(bucketId) { bucketName to mutableListOf() }
                entry.second.add(uri)
            }
        }
    } catch (_: Exception) { }

    // 1. Recents (All items)
    val totalCount = recentUris.size.takeIf { it > 0 } ?: bucketMap.values.sumOf { it.second.size }
    folders.add(
        MediaFolder(
            id = "ALL_RECENTS",
            name = "Recents",
            count = totalCount,
            thumbnailUri = recentUris.firstOrNull() ?: bucketMap.values.firstOrNull()?.second?.firstOrNull()
        )
    )

    // Add scanned folders from device
    bucketMap.forEach { (bucketId, pair) ->
        val (name, uris) = pair
        if (name.isNotBlank()) {
            folders.add(
                MediaFolder(
                    id = bucketId,
                    name = name,
                    count = uris.size,
                    thumbnailUri = uris.firstOrNull()
                )
            )
        }
    }

    // Default WhatsApp standard folders if none or few found on device
    if (folders.size <= 1) {
        val firstThumb = recentUris.firstOrNull()
        folders.add(MediaFolder("CAMERA", "Camera", 327, firstThumb))
        folders.add(MediaFolder("VIDEOS", "Videos", 13, recentUris.getOrNull(1) ?: firstThumb))
        folders.add(MediaFolder("SCREENSHOTS", "Screenshots", 1648, recentUris.getOrNull(2) ?: firstThumb))
        folders.add(MediaFolder("DOWNLOADS", "Downloads", 432, recentUris.getOrNull(3) ?: firstThumb))
        folders.add(MediaFolder("WHATSAPP", "WhatsApp", 820, recentUris.getOrNull(4) ?: firstThumb))
    }

    // Special items matching WhatsApp's UI:
    folders.add(
        MediaFolder(
            id = "MORE_APPS",
            name = "More apps",
            count = 0,
            type = FolderItemType.MORE_APPS
        )
    )
    folders.add(
        MediaFolder(
            id = "SEE_MORE",
            name = "See more",
            count = 0,
            type = FolderItemType.SEE_MORE
        )
    )

    folders
}

private fun formatNumber(num: Int): String {
    return String.format("%,d", num)
}

package com.microbond.app.core.components.threadbubbles.widgets.attachment_picker

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.microbond.app.core.data_classes_and_enums.AttachmentItem
import com.microbond.app.core.data_classes_and_enums.AttachmentOption
import com.microbond.app.core.ui.theme.MicrobondAttachmentSheetDark
import com.microbond.app.core.ui.theme.MicrobondAttachmentIconBgDark
import com.microbond.app.core.ui.theme.MicrobondDragHandleDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondAttachDepositGreen
import com.microbond.app.core.ui.theme.MicrobondAttachReportBlue
import com.microbond.app.core.ui.theme.MicrobondAttachLocationGreen
import com.microbond.app.core.ui.theme.MicrobondAttachContactBlue
import com.microbond.app.core.ui.theme.MicrobondAttachDocumentPurple
import com.microbond.app.core.ui.theme.MicrobondAttachPollOrange
import com.microbond.app.core.ui.theme.MicrobondAttachRulesOrange
import com.microbond.app.core.ui.theme.MicrobondAttachStatsBlue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val COMPACT_HEIGHT    = 300.dp
private val EXPAND_THRESHOLD  = 40.dp

private val attachmentItems = listOf(
    AttachmentItem(AttachmentOption.DEPOSIT_STATUS, AttachDepositStatus, "Deposit status", MicrobondAttachDepositGreen, Color.Transparent),
    AttachmentItem(AttachmentOption.SAVE_REPORT,    AttachSaveReport,    "Save report",    MicrobondAttachReportBlue, Color.Transparent),
    AttachmentItem(AttachmentOption.LOCATION,       AttachLocation,      "Location",       MicrobondAttachLocationGreen, Color.Transparent),
    AttachmentItem(AttachmentOption.CONTACT,        AttachContact,       "Contact",        MicrobondAttachContactBlue, Color.Transparent),
    AttachmentItem(AttachmentOption.DOCUMENT,       AttachDocument,      "Document",       MicrobondAttachDocumentPurple, Color.Transparent),
    AttachmentItem(AttachmentOption.POLL,           AttachPoll,          "Poll",           MicrobondAttachPollOrange, Color.Transparent),
    AttachmentItem(AttachmentOption.RULES,          AttachRules,         "Rules",          MicrobondAttachRulesOrange, Color.Transparent),
    AttachmentItem(AttachmentOption.STATISTICS,     AttachStatistics,    "Statistics",     MicrobondAttachStatsBlue, Color.Transparent),
)

@Composable
fun AttachmentPickerPanel(
    visible: Boolean,
    onOptionSelected: (AttachmentOption) -> Unit,
    onImagesSelected: (List<Uri>, String) -> Unit,
    onDismiss: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onHeightChanged: (Dp) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope   = rememberCoroutineScope()

    var hasImageAccess         by remember { mutableStateOf<Boolean>(hasImageReadPermission(context)) }
    var permissionWasRequested by remember { mutableStateOf(false) }
    var imageUris              by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoadingImages        by remember { mutableStateOf(false) }
    var imageLoadFailed        by remember { mutableStateOf(false) }
    var selectedImages         by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var captionText            by remember { mutableStateOf("") }
    var isCaptionFocused       by remember { mutableStateOf(false) }
    var showFolderMenu         by remember { mutableStateOf(false) }
    var selectedFolder         by remember { mutableStateOf<MediaFolder?>(null) }
    var mediaFolders           by remember { mutableStateOf<List<MediaFolder>>(emptyList()) }

    val compactPx = with(density) { COMPACT_HEIGHT.toPx() }
    val sheetHeight = remember { Animatable(0f) }

    val imeBottom = WindowInsets.ime.getBottom(density)
    val isKeyboardOpen = imeBottom > 0

    val isExpanded = sheetHeight.value > compactPx + with(density) { EXPAND_THRESHOLD.toPx() }

    LaunchedEffect(sheetHeight.value) {
        onHeightChanged(with(density) { sheetHeight.value.toDp() })
    }
    LaunchedEffect(visible, isExpanded) {
        onExpandedChange(visible && isExpanded)
    }
    LaunchedEffect(visible) {
        if (visible) sheetHeight.animateTo(compactPx, tween(220))
        else         { sheetHeight.animateTo(0f, tween(220)); selectedImages = emptySet(); captionText = ""; isCaptionFocused = false }
    }

    BackHandler(enabled = visible) {
        if (isExpanded) scope.launch { sheetHeight.animateTo(compactPx, tween(220)) }
        else onDismiss()
    }

    val imagePerms = remember {
        when {
            Build.VERSION.SDK_INT >= 34 -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            Build.VERSION.SDK_INT >= 33 -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            else                        -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        hasImageAccess = results.values.any { it } || hasImageReadPermission(context)
        permissionWasRequested = true
    }
    LaunchedEffect(visible, hasImageAccess) {
        if (!visible) return@LaunchedEffect
        if (!hasImageAccess) { if (!permissionWasRequested) permLauncher.launch(imagePerms); return@LaunchedEffect }
        isLoadingImages = true; imageLoadFailed = false
        try   {
            val uris = withContext(Dispatchers.IO) { queryRecentImages(context, selectedFolder?.id) }
            imageUris = uris
            mediaFolders = queryMediaFolders(context, uris)
        }
        catch (_: SecurityException)        { imageLoadFailed = true }
        catch (_: IllegalArgumentException) { imageLoadFailed = true }
        finally { isLoadingImages = false }
    }

    if (sheetHeight.value <= 0f) return

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val maxPx       = constraints.maxHeight.toFloat()
        val panelHeight = with(density) { sheetHeight.value.toDp() }

        val gridState = rememberLazyGridState()
        val nestedScroll = remember(maxPx, compactPx) {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val d = available.y
                    if (d < 0 && sheetHeight.value < maxPx) {
                        val newH = (sheetHeight.value - d).coerceAtMost(maxPx)
                        scope.launch { sheetHeight.snapTo(newH) }
                        return Offset(0f, sheetHeight.value - newH)
                    }
                    return Offset.Zero
                }
                override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                    val d = available.y
                    if (d > 0 && sheetHeight.value > compactPx) {
                        val newH = (sheetHeight.value - d).coerceAtLeast(compactPx)
                        scope.launch { sheetHeight.snapTo(newH) }
                        return Offset(0f, sheetHeight.value - newH)
                    } else if (d > 40f && sheetHeight.value <= compactPx) { onDismiss() }
                    return Offset.Zero
                }
            }
        }

        LaunchedEffect(isKeyboardOpen, isCaptionFocused) {
            if ((isKeyboardOpen || isCaptionFocused) && visible) {
                sheetHeight.animateTo(maxPx, tween(220))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(panelHeight)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(MicrobondAttachmentSheetDark)
                .windowInsetsPadding(
                    if (isKeyboardOpen) WindowInsets.ime
                    else WindowInsets.navigationBars
                )
        ) {

            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                            .clickable { scope.launch { sheetHeight.animateTo(compactPx, tween(220)) } },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, "Collapse", tint = MicrobondTextPrimary, modifier = Modifier.size(22.dp))
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { showFolderMenu = !showFolderMenu }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedFolder?.name ?: "Recents",
                                color = MicrobondTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Album",
                                tint = MicrobondTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.size(width = 32.dp, height = 22.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, MicrobondInactiveGray, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("HD", color = MicrobondInactiveGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                DragHandle(
                    onDrag = { delta ->
                        scope.launch { sheetHeight.snapTo((sheetHeight.value - delta).coerceIn(0f, maxPx)) }
                    },
                    onDragEnd = {
                        when {
                            sheetHeight.value < compactPx * 0.6f      -> onDismiss()
                            sheetHeight.value < maxPx       * 0.6f    -> scope.launch { sheetHeight.animateTo(compactPx, tween(220)) }
                            else                                        -> scope.launch { sheetHeight.animateTo(maxPx, tween(220)) }
                        }
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedImages.isEmpty()) "Recent photos" else "${selectedImages.size} selected",
                        color = MicrobondTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = gridState,
                modifier = Modifier.fillMaxWidth().weight(1f).nestedScroll(nestedScroll),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AnimatedVisibility(
                        visible = !isExpanded,
                        enter = expandVertically(tween(180)) + fadeIn(tween(180)),
                        exit  = shrinkVertically(tween(180)) + fadeOut(tween(180))
                    ) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            attachmentItems.chunked(4).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    rowItems.forEach { item ->
                                        AttachmentCell(item = item, onClick = { onOptionSelected(item.option) })
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }

                if (!hasImageAccess) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        PhotoPermissionMessage { permLauncher.launch(imagePerms) }
                    }
                } else if (isLoadingImages) {
                    item(span = { GridItemSpan(maxLineSpan) }) { ImageListMessage("Loading photos...") }
                } else if (imageLoadFailed) {
                    item(span = { GridItemSpan(maxLineSpan) }) { ImageListMessage("Could not load device photos") }
                } else if (imageUris.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { ImageListMessage("No photos found") }
                } else {
                    items(imageUris, key = { it.toString() }) { uri ->
                        SelectablePhoto(
                            imageUri = uri,
                            selected = uri in selectedImages,
                            selectionNumber = selectedImages.toList().indexOf(uri).takeIf { it >= 0 }?.plus(1),
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                            onClick  = { selectedImages = selectedImages.toggle(uri) }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = selectedImages.isNotEmpty(),
                enter = expandVertically(tween(180)) + fadeIn(tween(180)),
                exit  = shrinkVertically(tween(180)) + fadeOut(tween(180))
            ) {
                PhotoCaptionBar(
                    captionText = captionText,
                    onCaptionChange = { captionText = it },
                    selectedCount = selectedImages.size,
                    onFocusChanged = { isCaptionFocused = it },
                    onSendClick = {
                        onImagesSelected(selectedImages.toList(), captionText)
                        selectedImages = emptySet()
                        captionText = ""
                        isCaptionFocused = false
                    }
                )
            }
        }

        // Floating folder button on expanded grid
        if (isExpanded && selectedImages.isEmpty()) {
            FloatingFolderButton(
                onClick = { showFolderMenu = true },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        // Dropdown folder popup menu
        AlbumFolderDropdownMenu(
            visible = showFolderMenu,
            folders = mediaFolders,
            selectedFolderId = selectedFolder?.id ?: "ALL_RECENTS",
            onFolderSelected = { folder ->
                selectedFolder = if (folder.id == "ALL_RECENTS") null else folder
                showFolderMenu = false
                scope.launch {
                    isLoadingImages = true
                    try {
                        imageUris = withContext(Dispatchers.IO) {
                            queryRecentImages(context, bucketId = if (folder.id == "ALL_RECENTS") null else folder.id)
                        }
                    } finally {
                        isLoadingImages = false
                    }
                }
            },
            onDismiss = { showFolderMenu = false }
        )
    }
}

private fun Set<Uri>.toggle(uri: Uri): Set<Uri> = if (uri in this) this - uri else this + uri

@Composable
private fun SelectablePhoto(imageUri: Uri, selected: Boolean, selectionNumber: Int?, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.clickable(onClick = onClick)) {
        DeviceImageThumbnail(imageUri = imageUri, modifier = Modifier.fillMaxSize())
        if (selected) {
            Box(
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(24.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selectionNumber == null) Icon(Icons.Default.Check, "Selected", tint = Color.White)
                else Text("$selectionNumber", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DragHandle(onDrag: (Float) -> Unit, onDragEnd: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(24.dp).draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta -> onDrag(delta) },
            onDragStopped = { onDragEnd() }
        ),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(width = 36.dp, height = 4.dp).clip(CircleShape).background(MicrobondDragHandleDark))
    }
}

@Composable
private fun ImageListMessage(message: String) {
    Box(modifier = Modifier.height(76.dp).padding(horizontal = 8.dp), contentAlignment = Alignment.CenterStart) {
        Text(message, color = MicrobondInactiveGray, fontSize = 12.sp)
    }
}

@Composable
private fun PhotoPermissionMessage(onRequestPermission: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.Center) {
        Text("Allow photo access to show your images", color = MicrobondInactiveGray, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier.clip(RoundedCornerShape(50)).background(MicrobondActivePillDark)
                .clickable { onRequestPermission() }.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Allow access", color = MicrobondTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AttachmentCell(item: AttachmentItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier.size(54.dp).clip(CircleShape)
                .background(MicrobondAttachmentIconBgDark),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = item.tint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = item.label,
            modifier = Modifier.width(64.dp),
            color = MicrobondTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun hasImageReadPermission(context: Context): Boolean {
    val permissions = when {
        Build.VERSION.SDK_INT >= 34 -> listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        Build.VERSION.SDK_INT >= 33 -> listOf(Manifest.permission.READ_MEDIA_IMAGES)
        else                        -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    return permissions.any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
}

private fun queryRecentImages(context: Context, bucketId: String? = null): List<Uri> {
    val uris = mutableListOf<Uri>()
    val selection = if (bucketId != null && bucketId != "ALL_RECENTS" && bucketId != "CAMERA" && bucketId != "SCREENSHOTS" && bucketId != "DOWNLOADS" && bucketId != "VIDEOS" && bucketId != "WHATSAPP") {
        "${MediaStore.Images.Media.BUCKET_ID} = ?"
    } else null
    val selectionArgs = if (selection != null) arrayOf(bucketId) else null

    try {
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID), selection, selectionArgs,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val col = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                uris += ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(col))
            }
        }
    } catch (_: Exception) { }
    return uris
}

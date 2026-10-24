package com.microbond.app.core.components.threadbubbles.itemscreens.screens.documentscreens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.text.format.DateFormat
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.data_classes_and_enums.DocumentData
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

private val filesActionTint = Color(0xFF00C875)
private val recentFileSurface = Color(0xFF899397)

@Composable
fun DocumentFilesBrowserScreen(
    onBack: () -> Unit,
    onDocumentSelected: (DocumentData) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current
    var recentDocuments by remember { mutableStateOf<List<DocumentData>>(emptyList()) }
    var searchOpen by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var sortByName by remember { mutableStateOf(false) }
    var hasMediaAccess by remember { mutableStateOf(hasAnyMediaReadPermission(context)) }
    var permissionWasRequested by remember { mutableStateOf(false) }
    var browserMessage by remember { mutableStateOf<String?>(null) }
    val searchFocusRequester = remember { FocusRequester() }

    val mediaPermissions = remember {
        when {
            Build.VERSION.SDK_INT >= 34 -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
            Build.VERSION.SDK_INT >= 33 -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasMediaAccess = results.values.any { it }
        permissionWasRequested = true
    }

    val coroutineScopeSelect: (Uri) -> Unit = { uri ->
        coroutineScope.launch {
            runCatching { readDocumentData(context, uri) }
                .onSuccess(onDocumentSelected)
                .onFailure { browserMessage = "Could not open that file. Choose another one." }
        }
    }

    val documentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            coroutineScopeSelect(uri)
        }
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) coroutineScopeSelect(uri)
    }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            coroutineScopeSelect(uri)
        }
    }

    val scanner = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            coroutineScope.launch {
                val scannedUri = withContext(Dispatchers.IO) { saveScannedPage(context, bitmap) }
                if (scannedUri != null) coroutineScopeSelect(scannedUri)
                else browserMessage = "Could not save the scanned page."
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasMediaAccess && !permissionWasRequested) {
            permissionLauncher.launch(mediaPermissions)
        }
    }

    LaunchedEffect(hasMediaAccess, sortByName) {
        if (hasMediaAccess) {
            recentDocuments = withContext(Dispatchers.IO) {
                queryRecentMedia(context, sortByName)
            }
        }
    }

    LaunchedEffect(searchOpen) {
        if (searchOpen) {
            searchFocusRequester.requestFocus()
            keyboardController?.show()
        } else {
            keyboardController?.hide()
        }
    }

    val imeBottom = androidx.compose.foundation.layout.WindowInsets.ime.getBottom(density)
    var imeWasVisible by remember { mutableStateOf(false) }
    LaunchedEffect(imeBottom, searchOpen) {
        val imeVisible = imeBottom > 0
        if (searchOpen && imeWasVisible && !imeVisible) {
            searchOpen = false
            searchText = ""
        }
        imeWasVisible = imeVisible
    }

    BackHandler {
        if (searchOpen) {
            searchOpen = false
            searchText = ""
            keyboardController?.hide()
        } else {
            onBack()
        }
    }

    val visibleDocuments = remember(recentDocuments, searchText) {
        if (searchText.isBlank()) recentDocuments
        else recentDocuments.filter { document ->
            document.fileName.contains(searchText, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MicrobondDarkBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (searchOpen) 48.dp else 56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (searchOpen) {
                        searchOpen = false
                        searchText = ""
                    } else {
                        onBack()
                    }
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MicrobondTextPrimary)
            }
            if (searchOpen) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B2225))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .focusRequester(searchFocusRequester),
                    singleLine = true,
                    textStyle = TextStyle(color = MicrobondTextPrimary, fontSize = 16.sp),
                    cursorBrush = SolidColor(MicrobondActiveGreen),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (searchText.isEmpty()) Text("Search...", color = MicrobondInactiveGray, fontSize = 16.sp)
                            inner()
                        }
                    }
                )
            } else {
                Text(
                    text = "Files",
                    modifier = Modifier.weight(1f),
                    color = MicrobondTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal
                )
                IconButton(onClick = { searchOpen = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Search files", tint = MicrobondTextPrimary)
                }
                MicrobondOverflowMenu(
                    actions = listOf(
                        MicrobondMenuAction(
                            title = "Sort by name",
                            onClick = { sortByName = true },
                            icon = if (sortByName) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked
                        ),
                        MicrobondMenuAction(
                            title = "Sort by date",
                            onClick = { sortByName = false },
                            icon = if (sortByName) Icons.Default.RadioButtonUnchecked else Icons.Default.RadioButtonChecked
                        )
                    ),
                    modifier = Modifier.size(40.dp),
                    contentDescription = "Sort files",
                    triggerIcon = Icons.Default.Sort
                )
            }
        }
        Spacer(Modifier.fillMaxWidth().height(1.dp).background(MicrobondDividerDark))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                FilesActionRow(
                    icon = { Icon(Icons.Default.Storage, null, tint = filesActionTint, modifier = Modifier.size(30.dp)) },
                    title = "Browse documents",
                    subtitle = "Select files up to 2 GB in size",
                    onClick = { documentPicker.launch(arrayOf("*/*")) }
                )
            }
            item {
                FilesActionRow(
                    icon = { Icon(Icons.Default.Image, null, tint = filesActionTint, modifier = Modifier.size(30.dp)) },
                    title = "Choose from gallery",
                    subtitle = "Select original quality photos or videos",
                    onClick = {
                        galleryPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    }
                )
            }
            item {
                FilesActionRow(
                    icon = { Icon(Icons.Default.DocumentScanner, null, tint = filesActionTint, modifier = Modifier.size(30.dp)) },
                    title = "Scan document",
                    subtitle = "Take photos of a document",
                    onClick = { scanner.launch(null) }
                )
            }
            item {
                FilesActionRow(
                    icon = { Icon(Icons.Default.Headphones, null, tint = filesActionTint, modifier = Modifier.size(30.dp)) },
                    title = "Browse audio",
                    subtitle = "Select audio or music files",
                    onClick = { audioPicker.launch(arrayOf("audio/*")) }
                )
            }
            item {
                Spacer(Modifier.fillMaxWidth().height(1.dp).background(MicrobondDividerDark))
                Text(
                    text = "Recents",
                    color = MicrobondInactiveGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 18.dp, bottom = 6.dp)
                )
            }
            if (!hasMediaAccess) {
                item {
                    Text(
                        text = if (permissionWasRequested) "Allow media access to show recent files" else "Loading recent files…",
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { permissionLauncher.launch(mediaPermissions) }
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }
            } else if (visibleDocuments.isEmpty()) {
                item {
                    Text(
                        text = if (searchText.isBlank()) "No recent files" else "No matching files",
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            } else {
                items(visibleDocuments, key = { it.uri.orEmpty() }) { document ->
                    RecentDocumentRow(document) {
                        document.uri?.let(Uri::parse)?.let(coroutineScopeSelect)
                    }
                }
            }
            browserMessage?.let { message ->
                item { Text(message, color = MicrobondInactiveGray, modifier = Modifier.padding(16.dp)) }
            }
        }
    }
}

@Composable
private fun FilesActionRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) { icon() }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, color = MicrobondTextPrimary, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = MicrobondInactiveGray, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun RecentDocumentRow(document: DocumentData, onClick: () -> Unit) {
    val extension = document.fileName.substringAfterLast('.', "FILE").take(4).uppercase()
    val context = LocalContext.current
    val modifiedLabel = document.dateModified?.let { seconds ->
        DateFormat.getMediumDateFormat(context).format(Date(seconds * 1000L))
    }.orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp, 26.dp)
                .background(recentFileSurface, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(extension, color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(document.fileName, color = MicrobondTextPrimary, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(document.fileSize, color = MicrobondInactiveGray, fontSize = 14.sp)
                Text(modifiedLabel, color = MicrobondInactiveGray, fontSize = 14.sp)
            }
        }
    }
}

private suspend fun readDocumentData(context: Context, uri: Uri): DocumentData = withContext(Dispatchers.IO) {
    val details = context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null,
        null,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val name = cursor.getString(0).orEmpty().ifBlank { uri.lastPathSegment.orEmpty() }
            val size = if (cursor.isNull(1)) 0L else cursor.getLong(1)
            name to size
        } else null
    } ?: (uri.lastPathSegment.orEmpty() to 0L)

    DocumentData(
        fileName = details.first,
        fileType = context.contentResolver.getType(uri) ?: "application/octet-stream",
        fileSize = details.second.toDisplaySize(),
        uri = uri.toString()
    )
}

private suspend fun queryRecentMedia(context: Context, sortByName: Boolean): List<DocumentData> = withContext(Dispatchers.IO) {
    val collection = MediaStore.Files.getContentUri("external")
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.MIME_TYPE
    )
    val mimeColumn = MediaStore.Files.FileColumns.MIME_TYPE
    val selection = "$mimeColumn LIKE ? OR $mimeColumn LIKE ? OR $mimeColumn LIKE ?"
    val args = arrayOf("image/%", "video/%", "audio/%")
    val order = if (sortByName) {
        "${MediaStore.Files.FileColumns.DISPLAY_NAME} COLLATE NOCASE ASC"
    } else {
        "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
    }
    runCatching {
        val recent = mutableListOf<DocumentData>()
        context.contentResolver.query(collection, projection, selection, args, order)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
            val typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            while (cursor.moveToNext() && recent.size < 100) {
                val id = cursor.getLong(idColumn)
                recent += DocumentData(
                    fileName = cursor.getString(nameColumn).orEmpty(),
                    fileType = cursor.getString(typeColumn).orEmpty(),
                    fileSize = cursor.getLong(sizeColumn).toDisplaySize(),
                    uri = Uri.withAppendedPath(collection, id.toString()).toString(),
                    dateModified = cursor.getLong(dateColumn)
                )
            }
        }
        recent
    }.getOrDefault(emptyList())
}

private suspend fun saveScannedPage(context: Context, bitmap: Bitmap): Uri? = withContext(Dispatchers.IO) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return@withContext null

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "Microbond_scan_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/Microbond Scans")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }
    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        ?: return@withContext null
    runCatching {
        context.contentResolver.openOutputStream(uri)?.use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output))
        } ?: error("Could not write scanned page")
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        context.contentResolver.update(uri, values, null, null)
        uri
    }.getOrElse {
        context.contentResolver.delete(uri, null, null)
        null
    }
}

private fun hasAnyMediaReadPermission(context: Context): Boolean {
    val permissions = when {
        Build.VERSION.SDK_INT >= 34 -> listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )
        Build.VERSION.SDK_INT >= 33 -> listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
        else -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    return permissions.any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
}

private fun Long.toDisplaySize(): String = when {
    this < 1024L -> "$this B"
    this < 1024L * 1024L -> "${this / 1024L} kB"
    this < 1024L * 1024L * 1024L -> "${this / (1024L * 1024L)} MB"
    else -> "${this / (1024L * 1024L * 1024L)} GB"
}

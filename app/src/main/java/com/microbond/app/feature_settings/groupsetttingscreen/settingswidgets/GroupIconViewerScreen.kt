package com.microbond.app.feature_settings.groupsetttingscreen.settingswidgets

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.AsyncImageLoader
import com.microbond.app.core.components.avatar.rememberAsyncImagePainter
import com.microbond.app.core.media.MediaCompressor
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.UpdateGroupProfileRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.feature_groups.data.UserGroupsRepository
import com.microbond.app.feature_groups.ui.newgroup.widgets.GroupIconBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

/**
 * WhatsApp-style full screen Group Icon viewer matching Screenshot 2.
 * Allows viewing the group picture in full resolution and editing it via
 * the existing [GroupIconBottomSheet] with Gallery, Camera, and other sources.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupIconViewerScreen(
    groupName: String,
    iconUri: String?,
    groupId: String,
    onBack: () -> Unit,
    onIconUpdated: (String) -> Unit,
    canEditIcon: Boolean = true,
    canShare: Boolean = true
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentIconUri by remember(iconUri) { mutableStateOf(iconUri) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    fun handlePickedImage(uri: Uri) {
        coroutineScope.launch(Dispatchers.IO) {
            isUpdating = true
            try {
                // 1. FIRST COMPRESS THE IMAGE using MediaCompressor (1080px max dimension, ~150KB - 300KB)
                val compressed = MediaCompressor.compressImage(
                    context = context,
                    uri = uri,
                    maxDimension = 1080,
                    quality = 85
                )

                if (compressed == null || compressed.bytes.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Could not compress or load image", Toast.LENGTH_SHORT).show()
                        isUpdating = false
                    }
                    return@launch
                }

                val ext = if (compressed.mimeType.contains("png", ignoreCase = true)) "png" else "jpg"
                val safeId = if (groupId.isNotBlank()) groupId.replace(Regex("[^a-zA-Z0-9_-]"), "_") else "custom_${System.currentTimeMillis()}"
                val targetFile = File(context.filesDir, "group_icon_${safeId}.$ext")
                targetFile.writeBytes(compressed.bytes)

                val bitmap = BitmapFactory.decodeByteArray(compressed.bytes, 0, compressed.bytes.size)
                val permanentUri = Uri.fromFile(targetFile).toString()

                if (bitmap != null) {
                    AsyncImageLoader.putInCache(permanentUri, bitmap)
                    AsyncImageLoader.putInCache(targetFile.absolutePath, bitmap)
                }

                withContext(Dispatchers.Main) {
                    currentIconUri = permanentUri
                }

                // 2. Upload to Cloudflare R2 via POST /api/v1/groups/{group_id}/settings/image
                var remoteUrl: String? = null
                if (groupId.isNotBlank() && !groupId.startsWith("group_custom")) {
                    try {
                        val body = compressed.bytes.toRequestBody(compressed.mimeType.toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("file", "group_icon_${safeId}.$ext", body)
                        val uploadResp = NetworkClient.apiService.uploadGroupIcon(groupId, part)
                        if (uploadResp.isSuccessful && uploadResp.body() != null) {
                            val uploadData = uploadResp.body()!!.data
                            val uploadedUrl = uploadData?.groupImageUrl
                            if (!uploadedUrl.isNullOrBlank()) {
                                remoteUrl = uploadedUrl
                                if (bitmap != null) {
                                    AsyncImageLoader.putInCache(remoteUrl, bitmap)
                                }
                                withContext(Dispatchers.Main) {
                                    currentIconUri = remoteUrl
                                    onIconUpdated(remoteUrl)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("GroupIconViewerScreen", "Backend multipart upload failed: ${e.message}")
                    }

                    // Also sync profile in backend DB table
                    try {
                        val urlToSave = remoteUrl ?: permanentUri
                        NetworkClient.apiService.updateGroupProfile(
                            groupId = groupId,
                            payload = UpdateGroupProfileRequest(groupImageUrl = urlToSave)
                        )
                    } catch (e: Exception) {
                        android.util.Log.w("GroupIconViewerScreen", "updateGroupProfile sync warning: ${e.message}")
                    }
                }

                // 3. Persist to local Database / Repository
                val finalUri = remoteUrl ?: permanentUri
                UserGroupsRepository.updateGroupProfile(
                    idOrName = groupId.ifBlank { groupName },
                    iconUri = finalUri,
                    context = context
                )

                withContext(Dispatchers.Main) {
                    onIconUpdated(finalUri)
                    Toast.makeText(context, "Group icon updated", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupIconViewerScreen", "Error handling picked image", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save group icon", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isUpdating = false
                }
            }
        }
    }

    fun handleCameraBitmap(bitmap: Bitmap) {
        coroutineScope.launch(Dispatchers.IO) {
            isUpdating = true
            try {
                // 1. FIRST COMPRESS THE CAMERA BITMAP
                val compressed = MediaCompressor.compressBitmap(
                    bitmap = bitmap,
                    maxDimension = 1080,
                    quality = 85
                )

                val safeId = if (groupId.isNotBlank()) groupId.replace(Regex("[^a-zA-Z0-9_-]"), "_") else "custom_${System.currentTimeMillis()}"
                val targetFile = File(context.filesDir, "group_icon_${safeId}.jpg")
                targetFile.writeBytes(compressed.bytes)

                val permanentUri = Uri.fromFile(targetFile).toString()
                AsyncImageLoader.putInCache(permanentUri, bitmap)
                AsyncImageLoader.putInCache(targetFile.absolutePath, bitmap)

                withContext(Dispatchers.Main) {
                    currentIconUri = permanentUri
                }

                var remoteUrl: String? = null
                if (groupId.isNotBlank() && !groupId.startsWith("group_custom")) {
                    try {
                        val body = compressed.bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("file", "group_icon_${safeId}.jpg", body)
                        val uploadResp = NetworkClient.apiService.uploadGroupIcon(groupId, part)
                        if (uploadResp.isSuccessful && uploadResp.body() != null) {
                            val uploadData = uploadResp.body()!!.data
                            val uploadedUrl = uploadData?.groupImageUrl
                            if (!uploadedUrl.isNullOrBlank()) {
                                remoteUrl = uploadedUrl
                                AsyncImageLoader.putInCache(remoteUrl, bitmap)
                                withContext(Dispatchers.Main) {
                                    currentIconUri = remoteUrl
                                    onIconUpdated(remoteUrl)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("GroupIconViewerScreen", "Backend multipart upload failed: ${e.message}")
                    }

                    try {
                        val urlToSave = remoteUrl ?: permanentUri
                        NetworkClient.apiService.updateGroupProfile(
                            groupId = groupId,
                            payload = UpdateGroupProfileRequest(groupImageUrl = urlToSave)
                        )
                    } catch (e: Exception) {
                        android.util.Log.w("GroupIconViewerScreen", "updateGroupProfile sync warning: ${e.message}")
                    }
                }

                val finalUri = remoteUrl ?: permanentUri
                UserGroupsRepository.updateGroupProfile(
                    idOrName = groupId.ifBlank { groupName },
                    iconUri = finalUri,
                    context = context
                )

                withContext(Dispatchers.Main) {
                    onIconUpdated(finalUri)
                    Toast.makeText(context, "Group icon updated", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupIconViewerScreen", "Error handling camera bitmap", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isUpdating = false
                }
            }
        }
    }

    fun handleRemoveImage() {
        coroutineScope.launch(Dispatchers.IO) {
            isUpdating = true
            try {
                if (groupId.isNotBlank() && !groupId.startsWith("group_custom")) {
                    try {
                        NetworkClient.apiService.deleteGroupIcon(groupId)
                        NetworkClient.apiService.updateGroupProfile(
                            groupId = groupId,
                            payload = UpdateGroupProfileRequest(groupImageUrl = "")
                        )
                    } catch (e: Exception) {
                        android.util.Log.w("GroupIconViewerScreen", "Backend deleteGroupIcon failed: ${e.message}")
                    }
                }

                val safeId = if (groupId.isNotBlank()) groupId.replace(Regex("[^a-zA-Z0-9_-]"), "_") else "custom"
                val targetFile = File(context.filesDir, "group_icon_${safeId}.jpg")
                if (targetFile.exists()) targetFile.delete()

                UserGroupsRepository.updateGroupProfile(
                    idOrName = groupId.ifBlank { groupName },
                    iconUri = "",
                    context = context
                )

                withContext(Dispatchers.Main) {
                    currentIconUri = null
                    onIconUpdated("")
                    Toast.makeText(context, "Group icon removed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupIconViewerScreen", "Failed to remove group icon", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isUpdating = false
                }
            }
        }
    }

    // ── Photo Picker (Gallery) ─────────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            handlePickedImage(uri)
        }
    }

    // ── Camera Launcher ────────────────────────────────────────────────────────
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            handleCameraBitmap(bitmap)
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Group icon",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Edit / Pencil icon (opens bottom sheet) - only visible if user can edit settings
                    if (canEditIcon) {
                        IconButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit icon",
                                tint = Color.White
                            )
                        }
                    }

                    // Share icon
                    if (canShare) {
                        IconButton(onClick = {
                            shareGroupInfo(context, groupName, currentIconUri)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { insets ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .background(Color.Black)
                .clickable { showBottomSheet = true },
            contentAlignment = Alignment.Center
        ) {
            val painter = currentIconUri?.let { rememberAsyncImagePainter(it) }

            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = groupName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            } else {
                // Default full-scale group icon container matching WhatsApp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color(0xFF182229)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Default group icon",
                        tint = Color(0xFF6B7B85),
                        modifier = Modifier.size(180.dp)
                    )
                }
            }

            if (isUpdating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MicrobondActiveGreen)
                }
            }
        }
    }

    // ── Existing Project Bottom Sheet ──────────────────────────────────────────
    if (showBottomSheet) {
        GroupIconBottomSheet(
            sheetState = sheetState,
            title = "Group icon",
            onDismissRequest = { showBottomSheet = false },
            onOpenGallery = {
                showBottomSheet = false
                galleryLauncher.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            onCameraClick = {
                showBottomSheet = false
                cameraLauncher.launch(null)
            },
            onDeleteClick = if (!currentIconUri.isNullOrBlank()) {
                {
                    showBottomSheet = false
                    handleRemoveImage()
                }
            } else null
        )
    }
}

private fun shareGroupInfo(context: Context, groupName: String, iconUri: String?) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Microbond Group: $groupName\nSave and grow wealth together.")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share group via")
        context.startActivity(shareIntent)
    } catch (_: Exception) {}
}

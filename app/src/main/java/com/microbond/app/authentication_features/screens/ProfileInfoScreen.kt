package com.microbond.app.authentication_features.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.AsyncImageLoader
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondAvatarIconDark
import com.microbond.app.core.ui.theme.MicrobondAvatarPlaceholderDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondTextPrimaryLight
import com.microbond.app.feature_groups.ui.newgroup.widgets.GroupIconBottomSheet

/**
 * Microbond "Profile info" Screen. Presented after successful phone number verification for user
 * profile setup. Features profile photo selection, character-counted name input with WhatsApp-style
 * green underline, and the reusable full-width CTA button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreen(
    initialName: String = "",
    initialAvatarUrl: String? = null,
    initialAboutStatus: String = "Hey there! I am using Microbond",
    initialAvatarColor: String? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onNextClick: (name: String, imageUri: Uri?, aboutStatus: String, avatarColorHex: String) -> Unit = { _, _, _, _ -> },
    onNavigateBack: () -> Unit = {},
    onHelpClick: () -> Unit = {},
) {
    val context = LocalContext.current
    var name by remember(initialName) { mutableStateOf(initialName) }
    var aboutStatus by remember(initialAboutStatus) {
        mutableStateOf(if (initialAboutStatus.isNotBlank()) initialAboutStatus else "Hey there! I am using Microbond")
    }
    var effectiveAvatarUrl by remember(initialAvatarUrl) { mutableStateOf(initialAvatarUrl) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val remoteAvatarBitmap by produceState<ImageBitmap?>(initialValue = null, effectiveAvatarUrl) {
        val url = effectiveAvatarUrl
        if (!url.isNullOrBlank()) {
            val bitmap = AsyncImageLoader.loadBitmap(context, url)
            if (bitmap != null) {
                value = bitmap.asImageBitmap()
            }
        }
    }
    val maxNameLength = 25
    val maxAboutLength = 139

    // Diverse, modern avatar color palette
    val avatarPalette = remember {
        listOf(
            Color(0xFF00897B), // Teal
            Color(0xFF1E88E5), // Blue
            Color(0xFF8E24AA), // Purple
            Color(0xFF00ACC1), // Cyan
            Color(0xFFD81B60), // Magenta
            Color(0xFFE53935), // Red / Crimson
            Color(0xFFFB8C00), // Vibrant Orange
            Color(0xFF3949AB), // Indigo
            Color(0xFF43A047), // Green
            Color(0xFF5E35B1), // Deep Purple
            Color(0xFFF4511E), // Deep Orange
            Color(0xFF00838F), // Dark Cyan
            Color(0xFF6D4C41), // Warm Brown
            Color(0xFF546E7A), // Slate Grey
            Color(0xFF2E7D32), // Forest Green
            Color(0xFFAD1457)  // Deep Rose
        )
    }

    val parsedInitialColor = remember(initialAvatarColor) {
        if (!initialAvatarColor.isNullOrBlank()) {
            try {
                android.graphics.Color.parseColor(initialAvatarColor)
            } catch (_: Exception) {
                null
            }
        } else null
    }

    // Avatar color chosen per user and preserved across rotations/recompositions
    val randomAvatarColorArgb = rememberSaveable(initialAvatarColor) {
        parsedInitialColor ?: avatarPalette.random().toArgb()
    }
    val userAvatarColor = remember(randomAvatarColorArgb) {
        Color(randomAvatarColorArgb)
    }

    // Automatically sync initialName and initialAvatarUrl when available
    LaunchedEffect(initialName) {
        if (initialName.isNotBlank() && name.isBlank()) {
            name = initialName
        }
    }

    LaunchedEffect(initialAvatarUrl) {
        if (!initialAvatarUrl.isNullOrBlank() && effectiveAvatarUrl.isNullOrBlank()) {
            effectiveAvatarUrl = initialAvatarUrl
        }
    }

    // Fetch user profile from backend to ensure name and avatar are populated in the widget
    LaunchedEffect(Unit) {
        if (name.isBlank() || effectiveAvatarUrl.isNullOrBlank()) {
            try {
                val resp = NetworkClient.apiService.getCurrentUserProfile()
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val profile = resp.body()!!.data!!
                    if (name.isBlank() && !profile.fullName.isNullOrBlank()) {
                        name = profile.fullName
                    }
                    val pic = profile.profilePictureUrl ?: profile.avatarUrl
                    if (!pic.isNullOrBlank() && effectiveAvatarUrl.isNullOrBlank()) {
                        effectiveAvatarUrl = pic
                    }
                    if (!profile.aboutStatus.isNullOrBlank() && (aboutStatus.isBlank() || aboutStatus == "Hey there! I am using Microbond")) {
                        aboutStatus = profile.aboutStatus
                    }
                }
            } catch (_: Exception) {}
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showImageSheet by remember { mutableStateOf(false) }

    BackHandler(onBack = onNavigateBack)

    // Photo picker launcher (Gallery)
    val photoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
            uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                selectedBitmap =
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(context.contentResolver, uri)
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        }
                    } catch (_: Exception) {
                        null
                    }
            }
        }

    // Camera launcher
    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) {
            bitmap: Bitmap? ->
            if (bitmap != null) {
                selectedBitmap = bitmap
                try {
                    val path =
                        MediaStore.Images.Media.insertImage(
                            context.contentResolver,
                            bitmap,
                            "Profile_${System.currentTimeMillis()}",
                            "Profile photo",
                        )
                    if (path != null) {
                        selectedImageUri = Uri.parse(path)
                    }
                } catch (_: Exception) {
                    // Fail soft
                }
            }
        }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MicrobondTextPrimary,
                    )
                }

                MicrobondOverflowMenu(
                    actions = listOf(MicrobondMenuAction(title = "Help", onClick = onHelpClick))
                )
            }
        },
        bottomBar = {
            val hasName = name.trim().isNotBlank()
            val isFormComplete = hasName

            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(MicrobondDarkBackground)
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!isFormComplete) {
                    Text(
                        text = "Please enter your name",
                        style =
                            TextStyle(
                                color = MicrobondInactiveGray,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                            ),
                        modifier = Modifier.padding(bottom = 10.dp),
                    )
                }

                // Primary Bottom Action: "Next" (docked on top of soft keyboard)
                ReusableMicroButtonBig(
                    text = "Next",
                    onClick = {
                        if (isFormComplete) {
                            val colorHex = String.format("#%06X", 0xFFFFFF and userAvatarColor.toArgb())
                            onNextClick(name.trim(), selectedImageUri, aboutStatus.trim(), colorHex)
                        }
                    },
                    enabled = isFormComplete,
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Screen Title
            Text(
                text = "Profile info",
                style =
                    TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    ),
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle
            Text(
                text = "Please provide your full name and an optional profile photo.",
                style =
                    TextStyle(
                        color = MicrobondInactiveGray,
                        fontSize = 14.5.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 21.sp,
                    ),
                modifier = Modifier.padding(horizontal = 32.dp),
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Circular Profile Photo Picker with random avatar color fallback & green '+' badge
            Box(
                modifier =
                    Modifier.size(130.dp).clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        showImageSheet = true
                    },
                contentAlignment = Alignment.Center,
            ) {
                // Outer circle container
                Box(
                    modifier =
                        Modifier.size(130.dp)
                            .clip(CircleShape)
                            .background(
                                if (selectedBitmap == null && remoteAvatarBitmap == null) userAvatarColor
                                else MicrobondAvatarPlaceholderDark
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selectedBitmap != null) {
                        Image(
                            bitmap = selectedBitmap!!.asImageBitmap(),
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else if (remoteAvatarBitmap != null) {
                        Image(
                            bitmap = remoteAvatarBitmap!!,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else if (name.isNotBlank()) {
                        val initials =
                            name
                                .trim()
                                .split(" ")
                                .filter { it.isNotEmpty() }
                                .take(2)
                                .map { it.first().uppercaseChar() }
                                .joinToString("")
                        Text(
                            text = initials,
                            style =
                                TextStyle(
                                    color = Color.White,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Avatar Placeholder",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(80.dp),
                        )
                    }
                }

                // Green plus badge at bottom-right
                Box(
                    modifier =
                        Modifier.align(Alignment.BottomEnd)
                            .size(36.dp)
                            .background(MicrobondActiveGreen, CircleShape)
                            .border(2.5.dp, MicrobondDarkBackground, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add profile photo",
                        tint = microBondTextPrimaryLight,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            val hasPhoto = (selectedBitmap != null || remoteAvatarBitmap != null || !effectiveAvatarUrl.isNullOrBlank())
            Text(
                text =
                    if (hasPhoto) "Change profile photo" else "Add photo (optional)",
                style =
                    TextStyle(
                        color = MicrobondInactiveGray,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                modifier = Modifier.clickable {
                    showImageSheet = true
                },
            )
            Spacer(modifier = Modifier.height(26.dp))

            // Name Input Row with character counter and emoji button
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        value = name,
                        onValueChange = { input ->
                            if (input.length <= maxNameLength) {
                                name = input
                            }
                        },
                        textStyle = TextStyle(color = MicrobondTextPrimary, fontSize = 16.sp),
                        singleLine = true,
                        cursorBrush = SolidColor(MicrobondActiveGreen),
                        keyboardOptions =
                            KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next,
                            ),
                        decorationBox = { innerTextField ->
                            if (name.isEmpty()) {
                                Text(
                                    text = "Type your name here",
                                    style =
                                        TextStyle(color = MicrobondInactiveGray, fontSize = 16.sp),
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.weight(1f).padding(bottom = 6.dp),
                    )

                    // Remaining character count
                    val remaining = maxNameLength - name.length
                    Text(
                        text = "$remaining",
                        style = TextStyle(color = MicrobondInactiveGray, fontSize = 14.sp),
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )

                    // Smiley face icon
                    Icon(
                        imageVector = Icons.Outlined.SentimentSatisfied,
                        contentDescription = "Emoji selector",
                        tint = MicrobondInactiveGray,
                        modifier = Modifier.size(22.dp),
                    )
                }

                HorizontalDivider(
                    color = MicrobondActiveGreen,
                    thickness = 1.5.dp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About / Status Input Row
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp)) {
                Text(
                    text = "About",
                    style =
                        TextStyle(
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    modifier = Modifier.padding(bottom = 4.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        value = aboutStatus,
                        onValueChange = { input ->
                            if (input.length <= maxAboutLength) {
                                aboutStatus = input
                            }
                        },
                        textStyle = TextStyle(color = MicrobondTextPrimary, fontSize = 15.sp),
                        singleLine = true,
                        cursorBrush = SolidColor(MicrobondActiveGreen),
                        keyboardOptions =
                            KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done,
                            ),
                        decorationBox = { innerTextField ->
                            if (aboutStatus.isEmpty()) {
                                Text(
                                    text = "Write your about status",
                                    style =
                                        TextStyle(
                                            color = MicrobondInactiveGray.copy(alpha = 0.5f),
                                            fontSize = 15.sp,
                                        ),
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.weight(1f).padding(bottom = 6.dp),
                    )

                    val remainingAbout = maxAboutLength - aboutStatus.length
                    Text(
                        text = "$remainingAbout",
                        style = TextStyle(color = MicrobondInactiveGray, fontSize = 12.5.sp),
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }

                HorizontalDivider(
                    color =
                        if (aboutStatus.isNotEmpty()) MicrobondActiveGreen else Color(0xFF2A3942),
                    thickness = 1.2.dp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    style =
                        TextStyle(
                            color = MicrobondErrorColor,
                            fontSize = 13.5.sp,
                            textAlign = TextAlign.Center,
                        ),
                    modifier = Modifier.padding(horizontal = 32.dp),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Modal sheet for photo selection (Camera, Gallery, etc.)
    if (showImageSheet) {
        GroupIconBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showImageSheet = false },
            title = "Profile photo",
            onOpenGallery = {
                showImageSheet = false
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onCameraClick = {
                showImageSheet = false
                cameraLauncher.launch(null)
            },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun ProfileInfoScreenPreview() {
    ProfileInfoScreen(initialName = "raynzjosh4")
}

package com.microbond.app.feature_settings.groupsetttingscreen.settingswidgets

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.emojipicker.MicrobondEmojiPicker
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.UpdateGroupProfileRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_groups.data.UserGroupsRepository
import kotlinx.coroutines.launch

/**
 * WhatsApp-style full screen editor for Group Name and Group Description.
 * Matches Screenshot 1 with a green outlined box, emoji & keyboard switcher, helper description,
 * bottom action bar (Cancel and OK) that floats above the software keyboard,
 * and a rich emoji picker sheet matching Screenshot 3.
 */
@Composable
fun EditGroupFieldScreen(
    title: String,
    initialValue: String,
    groupId: String,
    isNameField: Boolean = false,
    helperText: String? = null,
    onBack: () -> Unit,
    onSavedSuccessfully: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialValue,
                selection = TextRange(initialValue.length)
            )
        )
    }

    var isSaving by remember { mutableStateOf(false) }
    var isEmojiPickerOpen by remember { mutableStateOf(false) }

    BackHandler(onBack = {
        if (isEmojiPickerOpen) {
            isEmojiPickerOpen = false
        } else {
            onBack()
        }
    })

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
            keyboardController?.show()
        } catch (_: Exception) {}
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .background(MicrobondDarkBackground)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MicrobondTextPrimary
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    color = MicrobondTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MicrobondDarkBackground)
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                HorizontalDivider(color = Color(0xFF1F2C34), thickness = 0.8.dp)

                // ── Action Row: Cancel (Left) | OK (Right) ─────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel button (Left half)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                keyboardController?.hide()
                                isEmojiPickerOpen = false
                                onBack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            color = MicrobondActiveGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Vertical separator between Cancel and OK
                    VerticalDivider(
                        color = Color(0xFF1F2C34),
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(0.8.dp)
                    )

                    // OK button (Right half)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                enabled = !isSaving,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                val trimmed = textFieldValue.text.trim()
                                if (isNameField && trimmed.isBlank()) {
                                    Toast.makeText(context, "Group name cannot be empty", Toast.LENGTH_SHORT).show()
                                    return@clickable
                                }

                                coroutineScope.launch {
                                    isSaving = true
                                    try {
                                        if (groupId.isNotBlank()) {
                                            val payload = if (isNameField) {
                                                UpdateGroupProfileRequest(name = trimmed)
                                            } else {
                                                UpdateGroupProfileRequest(description = trimmed)
                                            }
                                            val response = NetworkClient.apiService.updateGroupProfile(
                                                groupId = groupId,
                                                payload = payload
                                            )
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "$title updated", Toast.LENGTH_SHORT).show()
                                            } else {
                                                val err = response.errorBody()?.string() ?: response.message()
                                                android.util.Log.w("EditGroupFieldScreen", "Server update error: $err")
                                            }
                                        }

                                        // Update local repository and persistent cache
                                        if (isNameField) {
                                            UserGroupsRepository.updateGroupProfile(
                                                idOrName = groupId,
                                                name = trimmed,
                                                context = context
                                            )
                                        } else {
                                            UserGroupsRepository.updateGroupProfile(
                                                idOrName = groupId,
                                                description = trimmed,
                                                context = context
                                            )
                                        }

                                        keyboardController?.hide()
                                        isEmojiPickerOpen = false
                                        onSavedSuccessfully(trimmed)
                                    } catch (e: Exception) {
                                        android.util.Log.e("EditGroupFieldScreen", "Failed to update profile", e)
                                        if (isNameField) {
                                            UserGroupsRepository.updateGroupProfile(
                                                idOrName = groupId,
                                                name = trimmed,
                                                context = context
                                            )
                                        } else {
                                            UserGroupsRepository.updateGroupProfile(
                                                idOrName = groupId,
                                                description = trimmed,
                                                context = context
                                            )
                                        }
                                        keyboardController?.hide()
                                        isEmojiPickerOpen = false
                                        onSavedSuccessfully(trimmed)
                                    } finally {
                                        isSaving = false
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MicrobondActiveGreen,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "OK",
                                color = MicrobondActiveGreen,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF1F2C34), thickness = 0.8.dp)

                // ── Emoji Picker Sheet docked under Cancel & OK bar ────────────
                AnimatedVisibility(
                    visible = isEmojiPickerOpen,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    MicrobondEmojiPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(310.dp),
                        onEmojiSelected = { emoji ->
                            val text = textFieldValue.text
                            val selection = textFieldValue.selection
                            val start = selection.min.coerceIn(0, text.length)
                            val end = selection.max.coerceIn(0, text.length)
                            val maxLen = if (isNameField) 100 else 500
                            if (text.length + emoji.length - (end - start) <= maxLen) {
                                val newText = text.replaceRange(start, end, emoji)
                                textFieldValue = TextFieldValue(
                                    text = newText,
                                    selection = TextRange(start + emoji.length)
                                )
                            }
                        },
                        onBackspace = {
                            val text = textFieldValue.text
                            val selection = textFieldValue.selection
                            val start = selection.min.coerceIn(0, text.length)
                            val end = selection.max.coerceIn(0, text.length)
                            if (start != end) {
                                val newText = text.removeRange(start, end)
                                textFieldValue = TextFieldValue(
                                    text = newText,
                                    selection = TextRange(start)
                                )
                            } else if (start > 0) {
                                val codePointBefore = Character.codePointBefore(text, start)
                                val charCount = Character.charCount(codePointBefore)
                                val newText = text.removeRange(start - charCount, start)
                                textFieldValue = TextFieldValue(
                                    text = newText,
                                    selection = TextRange(start - charCount)
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // ── Green Outlined Editable Box ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = MicrobondActiveGreen,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(Color.Transparent, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { newVal ->
                            val maxLen = if (isNameField) 100 else 500
                            if (newVal.text.length <= maxLen) {
                                textFieldValue = newVal
                            }
                        },
                        textStyle = TextStyle(
                            color = MicrobondTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(MicrobondActiveGreen),
                        keyboardOptions = KeyboardOptions(
                            capitalization = if (isNameField) KeyboardCapitalization.Words else KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                isEmojiPickerOpen = false
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .clickable {
                                if (isEmojiPickerOpen) {
                                    isEmojiPickerOpen = false
                                    keyboardController?.show()
                                }
                            }
                    )

                    Spacer(Modifier.width(8.dp))

                    // Switcher button: Shows Keyboard icon if Emoji Sheet is open; otherwise Smiley icon
                    IconButton(
                        onClick = {
                            if (isEmojiPickerOpen) {
                                isEmojiPickerOpen = false
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            } else {
                                keyboardController?.hide()
                                isEmojiPickerOpen = true
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        if (isEmojiPickerOpen) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = "Switch to keyboard",
                                tint = MicrobondInactiveGray,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.SentimentSatisfiedAlt,
                                contentDescription = "Switch to emoji",
                                tint = MicrobondInactiveGray,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Helper / Subtitle text ─────────────────────────────────────────
            val descriptionSubtitle = helperText ?: if (isNameField) {
                "Provide a group name and optional group description."
            } else {
                "The group description is visible to members of this group and people invited to this group."
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = descriptionSubtitle,
                    color = MicrobondInactiveGray,
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                if (isNameField) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${textFieldValue.text.length}/100",
                        color = MicrobondInactiveGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

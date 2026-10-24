package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.textfeild.MicrobondTextField
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondBottomSheetDark
import com.microbond.app.core.ui.theme.MicrobondCameraIconBgDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondFabContentDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_groups.data.GroupCreationManager
import com.microbond.app.feature_groups.ui.newgroup.widgets.GroupIconBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGroupScreen(
    participants: List<ContactEntry>,
    groupIconUri: String?,
    onBack: () -> Unit,
    onGroupCreated: (NewGroupSetup) -> Unit,
    onFinancialSettings: () -> Unit,
    onGroupPermissions: () -> Unit,
    onOpenGallery: () -> Unit,
    groupType: SavingsGroupType = SavingsGroupType.GOAL_SAVINGS
) {
    var groupName by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val isCreatingGroup by GroupCreationManager.isCreatingGroup.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = { NewGroupTopBar(onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (groupName.isNotBlank() && !isCreatingGroup) {
                        coroutineScope.launch {
                            val result = GroupCreationManager.createGroup(
                                name = groupName.trim(),
                                type = groupType,
                                description = "",
                                groupImageUrl = groupIconUri
                            )
                            result.onSuccess { createdData ->
                                onGroupCreated(
                                    NewGroupSetup(
                                        name = createdData.name.ifBlank { groupName.trim() },
                                        participants = participants,
                                        groupId = createdData.groupId,
                                        inviteCode = createdData.inviteCode,
                                        groupType = groupType
                                    )
                                )
                            }.onFailure { error ->
                                errorMessage = error.localizedMessage ?: "Failed to create group"
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MicrobondActiveGreen,
                contentColor = MicrobondFabContentDark,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                if (isCreatingGroup) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MicrobondFabContentDark,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = "Create group")
                }
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(insets)
        ) {
            // ── Avatar + Name row ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MicrobondDarkBackground)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera circle (group icon picker)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MicrobondCameraIconBgDark)
                        .clickable { showBottomSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (groupIconUri != null) {
                        // Indicating successfully chosen icon with theme green accent
                        Box(modifier = Modifier.fillMaxSize().background(MicrobondActiveGreen))
                    } else {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Group icon",
                            tint = MicrobondInactiveGray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                // Group name field
                Column(modifier = Modifier.weight(1f)) {
                    MicrobondTextField(
                        value = groupName,
                        onValueChange = { if (it.length <= 100) groupName = it },
                        hint = "Group name (optional)",
                        capitalization = KeyboardCapitalization.Words,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Character counter
                    Text(
                        text = "${groupName.length}/100",
                        color = MicrobondInactiveGray,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    )
                }
            }

            // ── Financial settings ─────────────────────────────────────────
            SettingNavigationRow(
                icon = {
                    Icon(
                        Icons.Default.Timer, // Or a different icon like Payment
                        contentDescription = null,
                        tint = MicrobondInactiveGray,
                        modifier = Modifier.size(22.dp)
                    )
                },
                title = "Group rules & financials",
                subtitle = "Required settings",
                onClick = onFinancialSettings
            )

            // ── Group permissions ─────────────────────────────────────────────
            SettingNavigationRow(
                icon = {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null,
                        tint = MicrobondInactiveGray,
                        modifier = Modifier.size(22.dp)
                    )
                },
                title = "Group permissions",
                subtitle = null,
                onClick = onGroupPermissions
            )

            Spacer(Modifier.height(16.dp))

            // ── Members strip ─────────────────────────────────────────────────
            Text(
                text = "Members: ${participants.size}",
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(participants, key = { it.name }) { contact ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(56.dp)
                    ) {
                        MicroBondAvatar(
                            painter = null,
                            name = contact.name,
                            modifier = Modifier.size(52.dp),
                            backgroundColor = contact.avatarColor
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = contact.name.take(8),
                            color = MicrobondInactiveGray,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
        
        if (showBottomSheet) {
            GroupIconBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
                onOpenGallery = {
                    showBottomSheet = false
                    onOpenGallery()
                }
            )
        }

        errorMessage?.let { errorText ->
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                containerColor = MicrobondBottomSheetDark,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Error",
                        tint = MicrobondErrorColor,
                        modifier = Modifier.size(28.dp)
                    )
                },
                title = {
                    Text(
                        text = "Group Creation Failed",
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        text = errorText,
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { errorMessage = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = MicrobondActiveGreen)
                    ) {
                        Text("OK", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    }
}

// ── Reusable tappable setting row with chevron ────────────────────────────────
@Composable
fun SettingNavigationRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MicrobondTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = subtitle,
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .padding(start = 52.dp)
                .background(MicrobondDividerDark)
        )
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────
@Composable
private fun NewGroupTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MicrobondDarkBackground)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MicrobondTextPrimary
            )
        }
        Text(
            text = "New group",
            color = MicrobondTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

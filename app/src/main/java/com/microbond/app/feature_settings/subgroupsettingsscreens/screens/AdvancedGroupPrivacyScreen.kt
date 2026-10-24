package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateGroupPrivacyRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_groups.data.UserGroupsRepository
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import kotlinx.coroutines.launch

/**
 * Advanced group privacy settings matching Screenshot.
 *
 * Exposes 3 core visibility/privacy switches backed by:
 * - GET  /api/v1/groups/{group_id}/settings/privacy
 * - PATCH /api/v1/groups/{group_id}/settings/privacy
 *
 * Each toggle auto-saves immediately on change (no Save button needed),
 * with optimistic UI updates and revert on server failure.
 */
@Composable
fun AdvancedGroupPrivacyScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val effectiveGroupId = remember(groupId, groupName) {
        groupId.ifBlank {
            UserGroupsRepository.findByName(groupName)?.id ?: ""
        }
    }

    var hideBalances by remember { mutableStateOf(false) }
    var hideMemberContact by remember { mutableStateOf(true) }
    var inviteOnly by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun loadPrivacySettings() {
        if (effectiveGroupId.isBlank() || effectiveGroupId.startsWith("group_custom")) {
            isLoading = false
            return
        }
        isLoading = true
        loadError = null
        scope.launch {
            try {
                val res = NetworkClient.apiService.getGroupPrivacy(effectiveGroupId)
                if (res.isSuccessful && res.body()?.data != null) {
                    val d = res.body()!!.data!!
                    hideBalances = d.hideTotalGroupBalance
                    hideMemberContact = d.hideMemberContactInfo
                    inviteOnly = d.inviteOnlyDiscoverability
                } else {
                    loadError = "Failed to load settings (HTTP ${res.code()})"
                }
            } catch (e: Exception) {
                loadError = "Could not reach server: ${e.localizedMessage}"
                android.util.Log.w("AdvancedGroupPrivacyScreen", "Load error", e)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(effectiveGroupId) {
        loadPrivacySettings()
    }

    fun patch(request: UpdateGroupPrivacyRequest, onRevert: () -> Unit) {
        if (effectiveGroupId.isBlank() || !isAdmin) return
        isSaving = true
        scope.launch {
            try {
                val res = NetworkClient.apiService.updateGroupPrivacy(effectiveGroupId, request)
                if (res.isSuccessful && res.body()?.data != null) {
                    val d = res.body()!!.data!!
                    hideBalances = d.hideTotalGroupBalance
                    hideMemberContact = d.hideMemberContactInfo
                    inviteOnly = d.inviteOnlyDiscoverability
                } else {
                    onRevert()
                    val msg = res.errorBody()?.string() ?: res.message()
                    Toast.makeText(context, "Failed to save: $msg", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                onRevert()
                Toast.makeText(context, "Network error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                isSaving = false
            }
        }
    }

    val onToggleHideBalances: (Boolean) -> Unit = { newVal ->
        if (!isAdmin) {
            Toast.makeText(context, "Only group admins can change group privacy settings.", Toast.LENGTH_SHORT).show()
        } else {
            val old = hideBalances
            hideBalances = newVal
            patch(UpdateGroupPrivacyRequest(hideTotalGroupBalance = newVal)) {
                hideBalances = old
            }
        }
    }

    val onToggleHideMemberContact: (Boolean) -> Unit = { newVal ->
        if (!isAdmin) {
            Toast.makeText(context, "Only group admins can change group privacy settings.", Toast.LENGTH_SHORT).show()
        } else {
            val old = hideMemberContact
            hideMemberContact = newVal
            patch(UpdateGroupPrivacyRequest(hideMemberContactInfo = newVal)) {
                hideMemberContact = old
            }
        }
    }

    val onToggleInviteOnly: (Boolean) -> Unit = { newVal ->
        if (!isAdmin) {
            Toast.makeText(context, "Only group admins can change group privacy settings.", Toast.LENGTH_SHORT).show()
        } else {
            val old = inviteOnly
            inviteOnly = newVal
            patch(UpdateGroupPrivacyRequest(inviteOnlyDiscoverability = newVal)) {
                inviteOnly = old
            }
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Advanced group privacy",
                subtitle = groupName.uppercase(),
                onBack = onBack,
                trailing = {
                    if (isSaving || isLoading) {
                        Box(
                            modifier = Modifier.padding(end = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MicrobondActiveGreen,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(20.dp)
        ) {
            if (loadError != null) {
                Text(
                    text = "$loadError — Tap to retry",
                    color = Color(0xFFFFB74D),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .clickable { loadPrivacySettings() }
                )
            }

            Text(
                text = "Control exactly what information members can see about each other and the group.",
                color = MicrobondInactiveGray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            PrivacyToggleRow(
                title = "Hide total group balance",
                description = "Only admins will see the total treasury amount. Members see only their own contributions.",
                checked = hideBalances,
                onCheckedChange = onToggleHideBalances
            )

            HorizontalDivider(color = Color(0xFF2C2C2C), modifier = Modifier.padding(vertical = 16.dp))

            PrivacyToggleRow(
                title = "Hide member contact info",
                description = "Members cannot see each other's phone numbers or external payment accounts.",
                checked = hideMemberContact,
                onCheckedChange = onToggleHideMemberContact
            )

            HorizontalDivider(color = Color(0xFF2C2C2C), modifier = Modifier.padding(vertical = 16.dp))

            PrivacyToggleRow(
                title = "Invite-only discoverability",
                description = "This group will not appear in local searches. Members can only join via direct invite links.",
                checked = inviteOnly,
                onCheckedChange = onToggleInviteOnly
            )
        }
    }
}

@Composable
fun PrivacyToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = title,
                color = MicrobondTextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = MicrobondInactiveGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MicrobondActiveGreen,
                uncheckedThumbColor = Color(0xFF8696A0),
                uncheckedTrackColor = Color(0xFF374248),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

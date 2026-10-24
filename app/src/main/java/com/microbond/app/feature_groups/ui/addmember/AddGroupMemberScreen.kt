package com.microbond.app.feature_groups.ui.addmember

import android.Manifest
import android.content.Intent
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.AddMemberRequest
import com.microbond.app.core.network.sync.ContactSyncEngine
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.feature_groups.ui.addmember.widgets.AddMemberActionHeader
import com.microbond.app.feature_groups.ui.addmember.widgets.AddMemberContactItem
import com.microbond.app.feature_groups.ui.addmember.widgets.AddMemberTopBar
import com.microbond.app.feature_groups.ui.newgroup.ContactEntry
import kotlinx.coroutines.launch

/**
 * Screen opened when an admin taps "+ Add members" in a group chat.
 * Calls POST /api/v1/contacts/sync to list Microbond users and POST /api/v1/groups/{id}/members/add
 * to add selected participants directly.
 */
@Composable
fun AddGroupMemberScreen(
    groupId: String,
    groupName: String,
    inviteCode: String? = null,
    onBack: () -> Unit,
    onEditPermissions: () -> Unit = {},
    onMembersAdded: (List<String>) -> Unit = {},
    canInviteViaLink: Boolean = true
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var selectedContacts by remember { mutableStateOf(setOf<ContactEntry>()) }
    var isAdding by remember { mutableStateOf(false) }

    // ── 1. Contact Synchronization with Backend API (POST /api/v1/contacts/sync) ──
    val syncedContacts by ContactSyncEngine.onMicrobondContacts.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            ContactSyncEngine.syncContacts(context)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        if (syncedContacts.isEmpty()) {
            ContactSyncEngine.syncContacts(context)
        }
    }

    // Baseline fallback contacts matching the design demo
    val defaultContacts = remember {
        listOf(
            ContactEntry(name = "Sam", subtitle = "Available", userId = "user_sam_1"),
            ContactEntry(name = "Norine", subtitle = "At work", userId = "user_norine_2"),
            ContactEntry(name = "Christine2", subtitle = "Hey there! I am using Microbond", userId = "user_christine_3"),
            ContactEntry(name = "Vanesa", subtitle = "Busy", userId = "user_vanesa_4"),
            ContactEntry(name = "Nelson Customer Old Tax Park", subtitle = "In a meeting", userId = "user_nelson_5"),
            ContactEntry(name = "Olivia", subtitle = "Battery about to die", userId = "user_olivia_6"),
            ContactEntry(name = "Nelson Old Tax Park For Busin...", subtitle = "Urgent calls only", userId = "user_nelson_biz_7"),
            ContactEntry(name = "..Dorii..~Dorah~", subtitle = "Hey there! I am using Microbond", userId = "user_dorah_8")
        )
    }

    val activeContacts = if (syncedContacts.isNotEmpty()) syncedContacts else defaultContacts
    val frequentContacts = activeContacts.take(7)
    val remainingContacts = activeContacts.drop(7)

    fun filterList(list: List<ContactEntry>) = if (query.isBlank()) list
    else list.filter {
        it.name.contains(query, ignoreCase = true) || it.subtitle.contains(query, ignoreCase = true)
    }

    BackHandler { onBack() }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            AddMemberTopBar(
                query = query,
                onQueryChange = { query = it },
                onBack = onBack
            )
        },
        floatingActionButton = {
            if (selectedContacts.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        if (isAdding) return@FloatingActionButton
                        coroutineScope.launch {
                            isAdding = true
                            val addedIds = mutableListOf<String>()
                            var hasError = false
                            for (contact in selectedContacts) {
                                val targetId = contact.userId ?: contact.phoneNumber.ifBlank { contact.name }
                                try {
                                    val res = NetworkClient.apiService.addMember(
                                        groupId = groupId,
                                        payload = AddMemberRequest(userId = targetId, role = "MEMBER")
                                    )
                                    if (res.isSuccessful) {
                                        addedIds.add(targetId)
                                    } else {
                                        hasError = true
                                    }
                                } catch (e: Exception) {
                                    hasError = true
                                }
                            }
                            isAdding = false
                            if (addedIds.isNotEmpty()) {
                                Toast.makeText(context, "Added ${addedIds.size} member(s) to $groupName", Toast.LENGTH_SHORT).show()
                                onMembersAdded(addedIds)
                                onBack()
                            } else if (hasError) {
                                Toast.makeText(context, "Could not add member(s). Check admin permissions.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    shape = CircleShape,
                    containerColor = MicrobondActiveGreen,
                    contentColor = Color.Black,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    if (isAdding) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirm add members",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Action header: notice banner + "New contact" + "Invite via link"
            item {
                AddMemberActionHeader(
                    onEditPermissionsClick = onEditPermissions,
                    onNewContactClick = {
                        val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    onInviteViaLinkClick = {
                        val code = inviteCode?.ifBlank { null } ?: groupId.take(8).uppercase()
                        val shareText = "Hey! Join our savings group \"$groupName\" on Microbond: microbond://join?code=$code"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Invite via link"))
                    },
                    canInviteViaLink = canInviteViaLink
                )
            }

            // Frequently contacted section
            val filteredFrequent = filterList(frequentContacts)
            if (filteredFrequent.isNotEmpty()) {
                item {
                    Text(
                        text = "Frequently contacted",
                        color = MicrobondInactiveGray,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(filteredFrequent, key = { "freq_${it.userId ?: it.name}" }) { contact ->
                    val isSelected = contact in selectedContacts
                    AddMemberContactItem(
                        contact = contact,
                        isSelected = isSelected,
                        onToggle = {
                            selectedContacts = if (isSelected) selectedContacts - contact else selectedContacts + contact
                        }
                    )
                }
            }

            // Contacts on Microbond section
            val filteredRemaining = filterList(remainingContacts)
            if (filteredRemaining.isNotEmpty()) {
                item {
                    Text(
                        text = "Contacts on Microbond",
                        color = MicrobondInactiveGray,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
                items(filteredRemaining, key = { "mb_${it.userId ?: it.name}" }) { contact ->
                    val isSelected = contact in selectedContacts
                    AddMemberContactItem(
                        contact = contact,
                        isSelected = isSelected,
                        onToggle = {
                            selectedContacts = if (isSelected) selectedContacts - contact else selectedContacts + contact
                        }
                    )
                }
            }
        }
    }
}

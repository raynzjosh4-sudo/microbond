package com.microbond.app.feature_groups.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader

data class DeviceContact(val name: String, val phoneNumber: String, val isOnApp: Boolean)

val mockGroups = listOf("Weekend Trip Fund", "Family Savings", "Startup Syndicate")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteMemberScreen(
    onBack: () -> Unit,
    isFromYouPage: Boolean = false // Passed from routing
) {
    val context = LocalContext.current
    var groupPickerContact by remember { mutableStateOf<DeviceContact?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val contactsList = remember { mutableStateListOf<DeviceContact>() }
    
    val referralLink = "https://play.google.com/store/apps/details?id=com.microbond.app"

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
            
            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                
                while (it.moveToNext()) {
                    val name = it.getString(nameIndex) ?: ""
                    val number = it.getString(numberIndex) ?: ""
                    // Simulate backend check: about 20% of contacts are "on the app"
                    val isOnApp = number.hashCode() % 5 == 0
                    if (contactsList.none { c -> c.phoneNumber == number }) {
                        contactsList.add(DeviceContact(name, number, isOnApp))
                    }
                }
            }
        } else {
            statusMessage = "Permission required to show contacts."
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    BackHandler {
        if (groupPickerContact != null) {
            groupPickerContact = null
        } else {
            onBack()
        }
    }

    fun openSmsInvite(contact: DeviceContact) {
        val message = "Join me on Microbond to save together: $referralLink"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", contact.phoneNumber, null))
            .putExtra("sms_body", message)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // Handle no SMS app
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = if (isFromYouPage) "Invite a friend" else "Select contact",
                showBackButton = true,
                showMoreIcon = true,
                onBackClick = onBack
            )
        },
        snackbarHost = {
            if (statusMessage != null) {
                Snackbar(modifier = Modifier.padding(16.dp)) { Text(statusMessage!!) }
                LaunchedEffect(statusMessage) {
                    kotlinx.coroutines.delay(2000)
                    statusMessage = null
                }
            }
        }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            if (isFromYouPage) {
                item {
                    WaSettingsSectionHeader("Invite to Microbond")
                }
                items(contactsList.filter { !it.isOnApp }) { contact ->
                    ContactRow(
                        contact = contact,
                        actionText = "Invite",
                        onClick = { openSmsInvite(contact) }
                    )
                }
            } else {
                item {
                    WaSettingsSectionHeader("Contacts on Microbond")
                }
                items(contactsList.filter { it.isOnApp }) { contact ->
                    ContactRow(
                        contact = contact,
                        actionText = null,
                        onClick = { groupPickerContact = contact }
                    )
                }

                item {
                    WaSettingsSectionHeader("Invite to Microbond")
                }
                items(contactsList.filter { !it.isOnApp }) { contact ->
                    ContactRow(
                        contact = contact,
                        actionText = "Invite",
                        onClick = { openSmsInvite(contact) }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Join me on Microbond: $referralLink")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share invite link"))
                        }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MicrobondActiveGreen,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Share invite link", color = MicrobondTextPrimary, fontSize = 16.sp)
                }
            }
        }

        if (groupPickerContact != null) {
            ModalBottomSheet(
                onDismissRequest = { groupPickerContact = null },
                containerColor = MicrobondDarkBackground
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Invite ${groupPickerContact!!.name} to...",
                        color = MicrobondTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    mockGroups.forEach { groupName ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    statusMessage = "Invitation sent to $groupName (Awaiting admin approval)"
                                    groupPickerContact = null
                                }
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MicrobondInactiveGray.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MicrobondInactiveGray,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(groupName, color = MicrobondTextPrimary, fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ContactRow(contact: DeviceContact, actionText: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MicrobondInactiveGray.copy(alpha = 0.3f),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(contact.name, color = MicrobondTextPrimary, fontSize = 16.sp)
            Text(contact.phoneNumber, color = MicrobondInactiveGray, fontSize = 13.sp)
        }
        if (actionText != null) {
            TextButton(onClick = onClick) {
                Text(actionText, color = MicrobondActiveGreen, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
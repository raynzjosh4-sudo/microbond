package com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.selectscreens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.ContactAvatarFallback
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.ContactPhotoPainter
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.ContactSendItem
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.DeviceContact
import com.microbond.app.core.components.userlisttile.MicrobondUserListTile
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ContactScreen(
    onBack: () -> Unit,
    initiallySelected: List<ContactSendItem> = emptyList(),
    onPreview: (List<ContactSendItem>) -> Unit
) {
    val context = LocalContext.current
    var hasContactsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var contacts by remember { mutableStateOf<List<DeviceContact>>(emptyList()) }
    var selectedContacts by remember(initiallySelected) {
        mutableStateOf(initiallySelected.map { DeviceContact(it.id, it.name, it.phone, it.photoUri) })
    }
    var searchExpanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { hasContactsPermission = it }

    LaunchedEffect(Unit) {
        if (!hasContactsPermission) permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    LaunchedEffect(searchExpanded) {
        if (searchExpanded) {
            searchFocusRequester.requestFocus()
            keyboardController?.show()
        } else {
            keyboardController?.hide()
        }
    }

    LaunchedEffect(hasContactsPermission) {
        if (hasContactsPermission) {
            isLoading = true
            contacts = withContext(Dispatchers.IO) {
                val loadedContacts = mutableListOf<DeviceContact>()
                context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                    ),
                    null,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} COLLATE LOCALIZED ASC"
                )?.use { cursor ->
                    val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                    val nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val phoneIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val photoIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                    while (cursor.moveToNext()) {
                        val phone = cursor.getString(phoneIndex).orEmpty()
                        if (phone.isNotBlank()) {
                            loadedContacts += DeviceContact(
                                id = cursor.getLong(idIndex),
                                name = cursor.getString(nameIndex).orEmpty().ifBlank { phone },
                                phone = phone,
                                photoUri = cursor.getString(photoIndex)
                            )
                        }
                    }
                }
                loadedContacts.distinctBy(DeviceContact::key)
            }
            isLoading = false
        }
    }

    val filteredContacts = remember(contacts, searchText) {
        if (searchText.isBlank()) contacts
        else contacts.filter {
            it.name.contains(searchText, ignoreCase = true) || it.phone.contains(searchText, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MicrobondDarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (searchExpanded) 48.dp else 64.dp)
                        .background(if (searchExpanded) Color(0xFF1F2C34) else MicrobondDarkBackground)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (searchExpanded) {
                                searchExpanded = false
                                searchText = ""
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MicrobondTextPrimary)
                    }
                    if (searchExpanded) {
                        BasicTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(searchFocusRequester),
                            singleLine = true,
                            textStyle = TextStyle(color = MicrobondTextPrimary, fontSize = 16.sp),
                            cursorBrush = SolidColor(MicrobondActiveGreen),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (searchText.isEmpty()) {
                                        Text("Search...", color = MicrobondInactiveGray, fontSize = 16.sp)
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    } else {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Contacts to send", color = MicrobondTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = "${selectedContacts.size} selected",
                                color = MicrobondTextPrimary,
                                fontSize = 14.sp
                            )
                        }
                        IconButton(onClick = { searchExpanded = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search contacts", tint = MicrobondTextPrimary)
                        }
                    }
                }

                if (selectedContacts.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(selectedContacts, key = DeviceContact::key) { contact ->
                            SelectedContactTile(
                                contact = contact,
                                onRemove = { selectedContacts = selectedContacts.filterNot { it.key == contact.key } }
                            )
                        }
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MicrobondDividerDark)
                )
            }

            when {
                !hasContactsPermission -> ContactPermissionContent(
                    onRequestPermission = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }
                )
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading contacts…", color = MicrobondInactiveGray)
                }
                filteredContacts.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (searchText.isBlank()) "No contacts found" else "No matching contacts", color = MicrobondInactiveGray)
                }
                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    item {
                        Text(
                            text = "You",
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 2.dp)
                        )
                        MicrobondUserListTile(
                            name = "raynzjosh",
                            avatarColor = Color(0xFF607D8B),
                            avatarSize = 48.dp,
                            avatarFallbackContent = { ContactAvatarFallback() }
                        )
                        Text(
                            text = "Contacts on WhatsApp",
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 2.dp)
                        )
                    }
                    items(filteredContacts, key = DeviceContact::key) { contact ->
                        val selected = selectedContacts.any { it.key == contact.key }
                        MicrobondUserListTile(
                            name = contact.name,
                            painter = ContactPhotoPainter(contact.photoUri),
                            avatarColor = Color(0xFF607D8B),
                            avatarSize = 54.dp,
                            avatarFallbackContent = { ContactAvatarFallback() },
                            avatarBadge = if (selected) {
                                {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(MicrobondActiveGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                            } else null,
                            onClick = {
                                selectedContacts = if (selected) {
                                    selectedContacts.filterNot { it.key == contact.key }
                                } else {
                                    selectedContacts + contact
                                }
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (selectedContacts.isNotEmpty()) {
                    onPreview(selectedContacts.map { ContactSendItem(it.id, it.name, it.phone, it.photoUri) })
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp),
            containerColor = MicrobondActiveGreen,
            contentColor = Color(0xFF07130F),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Continue")
        }
    }
}

@Composable
private fun SelectedContactTile(contact: DeviceContact, onRemove: () -> Unit) {
    Column(
        modifier = Modifier.size(width = 82.dp, height = 104.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(64.dp)) {
            MicroBondAvatar(
                name = contact.name,
                painter = ContactPhotoPainter(contact.photoUri),
                backgroundColor = Color(0xFF607D8B),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                fallbackContent = { ContactAvatarFallback() }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB0BEC5))
                    .border(1.dp, MicrobondDarkBackground, CircleShape)
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove ${contact.name}", tint = Color(0xFF172127), modifier = Modifier.size(11.dp))
            }
        }
        Text(
            text = contact.phone,
            color = MicrobondInactiveGray,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

@Composable
private fun ContactPermissionContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Allow access to contacts to choose who to send.", color = MicrobondTextPrimary)
        OutlinedButton(onClick = onRequestPermission, modifier = Modifier.padding(top = 16.dp)) {
            Text("Allow contacts access")
        }
    }
}


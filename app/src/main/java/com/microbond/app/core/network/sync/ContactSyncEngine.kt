package com.microbond.app.core.network.sync

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.microbond.app.core.network.ContactSyncRequest
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.feature_groups.ui.newgroup.ContactEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ContactSyncEngine: Scans local device contacts, normalizes them to E.164,
 * and synchronizes with the Microbond backend to identify:
 * 1. Contacts who already have a Microbond account.
 * 2. Which groups / savings circles they share with the user.
 * 3. Off-app contacts who can be invited via SMS.
 */
object ContactSyncEngine {

    private val avatarPalette = listOf(
        Color(0xFF3E8C6C), Color(0xFF527E65), Color(0xFF8D765F),
        Color(0xFF9D746A), Color(0xFF617F91), Color(0xFF96665C),
        Color(0xFF765A8D), Color(0xFF527B91), Color(0xFF8A795A)
    )

    private val _onMicrobondContacts = MutableStateFlow<List<ContactEntry>>(emptyList())
    val onMicrobondContacts: StateFlow<List<ContactEntry>> = _onMicrobondContacts.asStateFlow()

    private val _offAppContacts = MutableStateFlow<List<ContactEntry>>(emptyList())
    val offAppContacts: StateFlow<List<ContactEntry>> = _offAppContacts.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun syncContacts(context: Context, defaultDialCode: String = "256") {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            _isSyncing.value = true
            try {
                // 1. Query device contacts
                val deviceContacts = mutableMapOf<String, String>() // normalizedPhone -> Name
                val contentResolver = context.contentResolver
                val cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null,
                    null,
                    null
                )

                cursor?.use {
                    val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (it.moveToNext()) {
                        val rawName = if (nameIdx >= 0) it.getString(nameIdx) ?: "" else ""
                        val rawNum = if (numIdx >= 0) it.getString(numIdx) ?: "" else ""
                        val normalized = normalizePhoneNumber(rawNum, defaultDialCode)
                        if (normalized.isNotBlank() && !deviceContacts.containsKey(normalized)) {
                            deviceContacts[normalized] = rawName.ifBlank { normalized }
                        }
                    }
                }

                if (deviceContacts.isEmpty()) {
                    _isSyncing.value = false
                    return@launch
                }

                // 2. Dispatch bulk sync to Microbond backend
                val phoneList = deviceContacts.keys.toList()
                val response = try {
                    NetworkClient.apiService.syncContacts(ContactSyncRequest(phoneNumbers = phoneList))
                } catch (e: Exception) {
                    android.util.Log.e("ContactSyncEngine", "Failed to sync contacts with backend", e)
                    null
                }

                if (response != null && response.isSuccessful && response.body() != null) {
                    val matchedContacts = response.body()!!
                    val onAppList = mutableListOf<ContactEntry>()
                    val processedPhones = mutableSetOf<String>()

                    matchedContacts.forEach { contact ->
                        val phoneBookName = deviceContacts[contact.phoneNumber]
                        val displayName = contact.profileName.ifBlank { null } ?: phoneBookName ?: contact.phoneNumber
                        val groupSubtitle = contact.aboutStatus?.ifBlank { null } ?: "Hey there! I am using Microbond"

                        onAppList.add(
                            ContactEntry(
                                name = displayName,
                                subtitle = groupSubtitle,
                                avatarColor = pickColor(contact.phoneNumber),
                                isOnMicrobond = true,
                                phoneNumber = contact.phoneNumber,
                                userId = contact.userId
                            )
                        )
                        processedPhones.add(contact.phoneNumber)
                    }

                    val offAppList = mutableListOf<ContactEntry>()
                    deviceContacts.forEach { (phone, name) ->
                        if (!processedPhones.contains(phone)) {
                            offAppList.add(
                                ContactEntry(
                                    name = name,
                                    subtitle = phone,
                                    avatarColor = pickColor(phone),
                                    isOnMicrobond = false,
                                    phoneNumber = phone
                                )
                            )
                        }
                    }

                    withContext(Dispatchers.Main) {
                        _onMicrobondContacts.value = onAppList
                        _offAppContacts.value = offAppList
                    }
                    android.util.Log.d("ContactSyncEngine", "Synced contacts successfully: ${onAppList.size} on Microbond, ${offAppList.size} off-app.")
                } else {
                    android.util.Log.w("ContactSyncEngine", "Sync response not successful: code=${response?.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("ContactSyncEngine", "Unexpected error in syncContacts", e)
            } finally {
                _isSyncing.value = false
            }
        }
    }

    private fun normalizePhoneNumber(raw: String, defaultDialCode: String): String {
        val digitsOnly = raw.filter { it.isDigit() || it == '+' }
        if (digitsOnly.isBlank()) return ""
        return when {
            digitsOnly.startsWith("+") -> digitsOnly
            digitsOnly.startsWith("0") -> "+$defaultDialCode${digitsOnly.drop(1)}"
            else -> "+$defaultDialCode$digitsOnly"
        }
    }

    private fun pickColor(key: String): Color {
        val hash = kotlin.math.abs(key.hashCode())
        return avatarPalette[hash % avatarPalette.size]
    }
}

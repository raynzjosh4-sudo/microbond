package com.microbond.app.feature_groups.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_groups.data.TrustConnection

@Composable
fun InviteMemberScreen(
    onBack: () -> Unit,
    trustedConnections: List<TrustConnection>,
    onSaveConnection: (TrustConnection) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var memberName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val canSave = memberName.isNotBlank() && phoneNumber.filter(Char::isDigit).length >= 7
    val referralLink = "https://play.google.com/store/apps/details?id=com.microbond.app"

    val contactPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val contactUri = result.data?.data ?: return@rememberLauncherForActivityResult
        context.contentResolver.query(
            contactUri,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                memberName = cursor.getString(0).orEmpty()
                phoneNumber = cursor.getString(1).orEmpty()
                statusMessage = "Contact selected"
            }
        }
    }

    fun currentConnection(): TrustConnection = TrustConnection(
        name = memberName.trim(),
        phoneNumber = phoneNumber.trim()
    )

    fun openSmsInvite() {
        val connection = currentConnection()
        onSaveConnection(connection)
        val message = "Join me on Microbond to save together: $referralLink"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", connection.phoneNumber, null))
            .putExtra("sms_body", message)
        try {
            context.startActivity(intent)
            statusMessage = "Invite ready in your messaging app"
        } catch (_: ActivityNotFoundException) {
            statusMessage = "No messaging app is available on this device"
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Invite a member",
                showBackButton = true,
                showMoreIcon = false,
                onBackClick = onBack
            )
        },
        bottomBar = {
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(insets)
                .padding(horizontal = 16.dp)
        ) {
            FormSectionTitle("Add a trust connection")
            Text(
                text = "Save a trusted peer now, then invite them to Microbond by SMS.",
                color = MicrobondInactiveGray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(
                value = memberName,
                onValueChange = { memberName = it },
                label = "Full name"
            )
            AppTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone number",
                keyboardType = KeyboardType.Phone
            )

            OutlinedButton(
                onClick = {
                    contactPicker.launch(
                        Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                border = BorderStroke(1.dp, MicrobondDividerDark)
            ) {
                Text("Choose from device contacts", color = MicrobondTextPrimary)
            }

            Button(
                onClick = {
                    onSaveConnection(currentConnection())
                    statusMessage = "${memberName.trim()} saved to your trust network"
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MicrobondActiveGreen,
                    contentColor = Color(0xFF07130F)
                )
            ) {
                Text("Save trust connection", fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = ::openSmsInvite,
                enabled = canSave,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                border = BorderStroke(1.dp, MicrobondDividerDark)
            ) {
                Text("Save and invite by SMS", color = MicrobondTextPrimary)
            }

            FormSectionTitle("Invite link")
            Text(
                text = "Share the app link so friends can join your trust network.",
                color = MicrobondInactiveGray,
                fontSize = 14.sp
            )
            OutlinedButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(referralLink))
                    statusMessage = "Invite link copied"
                },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                border = BorderStroke(1.dp, MicrobondDividerDark)
            ) {
                Text("Copy Microbond invite link", color = MicrobondTextPrimary)
            }

            statusMessage?.let { message ->
                Text(
                    text = message,
                    color = MicrobondActiveGreen,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            if (trustedConnections.isNotEmpty()) {
                FormSectionTitle("Your trust network")
                trustedConnections.forEach { connection ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(connection.name, color = MicrobondTextPrimary, fontSize = 15.sp)
                            Text(connection.phoneNumber, color = MicrobondInactiveGray, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
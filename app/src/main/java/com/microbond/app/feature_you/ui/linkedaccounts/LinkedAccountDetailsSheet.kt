package com.microbond.app.feature_you.ui.linkedaccounts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondPureWhite
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkedAccountDetailsSheet(
    account: LinkedAccount,
    onDismissRequest: () -> Unit,
    onUnlinkAccount: (LinkedAccount) -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MicrobondDarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
        ) {
            Text(
                text = "Account Details",
                color = MicrobondTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DetailRow("Provider", account.provider)
            DetailRow("Account Type", account.type)
            DetailRow(if (account.type == "Mobile Money") "Phone Number" else "Account Number", account.number)
            DetailRow("Registered Name (KYC)", account.registeredName)

            Spacer(modifier = Modifier.height(24.dp))

            // Unlink Account Action
            OutlinedButton(
                onClick = {
                    onUnlinkAccount(account)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MicrobondDestructiveRed)
            ) {
                Text("Unlink Account", color = MicrobondDestructiveRed, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))
            
            Button(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MicrobondActiveGreen)
            ) {
                Text("Close", color = MicrobondPureWhite)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, color = MicrobondInactiveGray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = MicrobondTextPrimary, fontSize = 16.sp)
    }
}

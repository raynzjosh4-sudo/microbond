package com.microbond.app.feature_you.ui.linkedaccounts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondPureWhite
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondTransparent
import kotlinx.coroutines.launch

data class LinkedAccount(
    val id: String,
    val type: String,
    val provider: String,
    val number: String,
    val registeredName: String,
    val icon: ImageVector,
    val isDefault: Boolean = false,
    val isVerified: Boolean = true
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LinkedAccountsScreen(
    onNavigateToSetting: (String) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val accounts by LinkedAccountsManager.accounts.collectAsState()
    val isLoading by LinkedAccountsManager.isLoading.collectAsState()

    val selectedAccountIds = remember { mutableStateListOf<String>() }
    var accountToShowDetails by remember { mutableStateOf<LinkedAccount?>(null) }

    val isInSelectionMode = selectedAccountIds.isNotEmpty()

    LaunchedEffect(Unit) {
        LinkedAccountsManager.fetchLinkedAccounts()
    }

    BackHandler {
        if (isInSelectionMode) {
            selectedAccountIds.clear()
        } else {
            onBack()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            if (isInSelectionMode) {
                TopAppBar(
                    title = { Text("${selectedAccountIds.size} selected", color = MicrobondPureWhite) },
                    navigationIcon = {
                        IconButton(onClick = { selectedAccountIds.clear() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Selection", tint = MicrobondPureWhite)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val idsToDelete = selectedAccountIds.toSet()
                            selectedAccountIds.clear()
                            coroutineScope.launch {
                                LinkedAccountsManager.deleteAccounts(idsToDelete)
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = MicrobondPureWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MicrobondActiveGreen.copy(alpha = 0.2f),
                        titleContentColor = MicrobondPureWhite,
                        navigationIconContentColor = MicrobondPureWhite,
                        actionIconContentColor = MicrobondPureWhite
                    )
                )
            } else {
                MicrobondTopAppBar(
                    title = "Linked accounts",
                    showMoreIcon = false,
                    showBackButton = true,
                    onBackClick = onBack
                )
            }
        },
        floatingActionButton = {
            if (!isInSelectionMode) {
                FloatingActionButton(
                    onClick = { onNavigateToSetting("Add Linked Account") },
                    containerColor = MicrobondActiveGreen,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Account", tint = MicrobondPureWhite)
                }
            }
        }
    ) { paddingValues ->
        if (isLoading && accounts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MicrobondActiveGreen,
                    strokeWidth = 2.dp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Text(
                        text = "These accounts are used for your group deposits and payouts.",
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (accounts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No linked payout accounts yet",
                                color = MicrobondTextPrimary,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Tap the + button below to link your Mobile Money or Bank Account.",
                                color = MicrobondInactiveGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(accounts) { account ->
                        val isSelected = account.id in selectedAccountIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) MicrobondActiveGreen.copy(alpha = 0.2f) else MicrobondTransparent)
                                .combinedClickable(
                                    onClick = {
                                        if (isInSelectionMode) {
                                            if (isSelected) selectedAccountIds.remove(account.id)
                                            else selectedAccountIds.add(account.id)
                                        } else {
                                            accountToShowDetails = account
                                        }
                                    },
                                    onLongClick = {
                                        if (!isSelected) selectedAccountIds.add(account.id)
                                    }
                                )
                                .padding(start = 20.dp, end = 16.dp, top = 13.dp, bottom = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = account.icon,
                                contentDescription = null,
                                tint = MicrobondInactiveGray,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(20.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(account.provider, color = MicrobondTextPrimary, fontSize = 16.sp)
                                Text(
                                    text = account.number,
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (accountToShowDetails != null) {
            LinkedAccountDetailsSheet(
                account = accountToShowDetails!!,
                onDismissRequest = { accountToShowDetails = null },
                onUnlinkAccount = { accountToUnlink ->
                    coroutineScope.launch {
                        LinkedAccountsManager.deleteAccount(accountToUnlink.id)
                    }
                }
            )
        }
    }
}


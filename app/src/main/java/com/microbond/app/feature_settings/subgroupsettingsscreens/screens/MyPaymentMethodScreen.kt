package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.UpdateGroupPaymentMethodRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondPureWhite
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.feature_you.ui.linkedaccounts.AddLinkedAccountScreen
import com.microbond.app.feature_you.ui.linkedaccounts.LinkedAccountsManager
import kotlinx.coroutines.launch

data class PaymentMethodDisplayItem(
    val id: String,
    val provider: String,
    val accountNumber: String,
    val isDefault: Boolean = false,
    val isVerified: Boolean = false
)

@Composable
fun MyPaymentMethodScreen(
    groupName: String,
    groupId: String = "",
    onBack: () -> Unit
) {
    var isAddingMethod by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) }

    if (isAddingMethod) {
        AddLinkedAccountScreen(
            onBack = {
                isAddingMethod = false
                refreshKey++
            }
        )
        return
    }

    BackHandler(onBack = onBack)

    val scope = rememberCoroutineScope()
    var allMethods by remember { mutableStateOf<List<PaymentMethodDisplayItem>>(emptyList()) }
    var selectedMethodId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(groupId, refreshKey) {
        isLoading = true
        errorMsg = null
        try {
            val items = mutableListOf<PaymentMethodDisplayItem>()
            val seenNumbers = mutableSetOf<String>()

            // 1. Fetch user payment methods
            try {
                val resp = NetworkClient.apiService.getMyPaymentMethods()
                if (resp.isSuccessful && resp.body() != null) {
                    resp.body()!!.forEach { m ->
                        val num = m.maskedNumber.ifBlank { m.accountNumber }
                        if (seenNumbers.add(num)) {
                            items.add(
                                PaymentMethodDisplayItem(
                                    id = m.id,
                                    provider = m.providerName,
                                    accountNumber = num,
                                    isDefault = m.isDefault,
                                    isVerified = m.isVerified
                                )
                            )
                        }
                    }
                }
            } catch (_: Exception) {}

            // 2. Also fetch linked accounts from LinkedAccountsManager
            try {
                LinkedAccountsManager.fetchLinkedAccounts()
                LinkedAccountsManager.accounts.value.forEach { la ->
                    if (seenNumbers.add(la.number)) {
                        items.add(
                            PaymentMethodDisplayItem(
                                id = la.id,
                                provider = la.provider,
                                accountNumber = la.number,
                                isDefault = la.isDefault,
                                isVerified = la.isVerified
                            )
                        )
                    }
                }
            } catch (_: Exception) {}

            allMethods = items

            // 3. Fetch active group payment method
            if (groupId.isNotBlank()) {
                try {
                    val groupResp = NetworkClient.apiService.getMyGroupPaymentMethod(groupId)
                    if (groupResp.isSuccessful && groupResp.body() != null) {
                        val body = groupResp.body()!!
                        selectedMethodId = body.paymentMethodId
                            ?: items.firstOrNull { it.accountNumber == body.accountNumber || it.accountNumber == body.maskedNumber }?.id
                            ?: items.firstOrNull { it.isDefault }?.id
                            ?: items.firstOrNull()?.id
                    } else {
                        selectedMethodId = items.firstOrNull { it.isDefault }?.id ?: items.firstOrNull()?.id
                    }
                } catch (_: Exception) {
                    selectedMethodId = items.firstOrNull { it.isDefault }?.id ?: items.firstOrNull()?.id
                }
            } else {
                selectedMethodId = items.firstOrNull { it.isDefault }?.id ?: items.firstOrNull()?.id
            }
        } catch (e: Exception) {
            errorMsg = "Could not load payment methods."
        } finally {
            isLoading = false
        }
    }

    fun selectMethod(methodId: String) {
        val prev = selectedMethodId
        selectedMethodId = methodId
        if (groupId.isBlank()) return
        scope.launch {
            isSaving = true
            try {
                val resp = NetworkClient.apiService.updateMyGroupPaymentMethod(
                    groupId,
                    UpdateGroupPaymentMethodRequest(paymentMethodId = methodId)
                )
                if (!resp.isSuccessful) selectedMethodId = prev
            } catch (_: Exception) {
                selectedMethodId = prev
            }
            isSaving = false
        }
    }

    fun providerIcon(provider: String): ImageVector =
        if (provider.contains("bank", ignoreCase = true)) Icons.Default.AccountBalance
        else Icons.Default.PhoneAndroid

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "My payment method",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isLoading || isSaving) {
                        Box(modifier = Modifier.padding(end = 12.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = MicrobondActiveGreen,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingMethod = true },
                containerColor = MicrobondActiveGreen,
                contentColor = MicrobondPureWhite,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Payment Method",
                    tint = MicrobondPureWhite
                )
            }
        }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select the payment method you will use for making contributions and receiving payouts in this group.",
                    color = MicrobondInactiveGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            errorMsg?.let { msg ->
                item {
                    Text(
                        text = "⚠ $msg",
                        color = Color(0xFFFFB74D),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MicrobondActiveGreen)
                    }
                }
            } else if (allMethods.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MicrobondOverlayWidgetDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = MicrobondInactiveGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No linked payment methods",
                            color = MicrobondTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap the round + button below to link a Mobile Money or Bank Account.",
                            color = MicrobondInactiveGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(allMethods, key = { it.id }) { method ->
                    PaymentMethodCard(
                        methodName = method.provider,
                        accountNumber = method.accountNumber,
                        icon = providerIcon(method.provider),
                        isSelected = selectedMethodId == method.id,
                        isDefault = method.isDefault,
                        onClick = { selectMethod(method.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(88.dp)) }
        }
    }
}

@Composable
fun PaymentMethodCard(
    methodName: String,
    accountNumber: String,
    icon: ImageVector,
    isSelected: Boolean,
    isDefault: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MicrobondActiveGreen.copy(alpha = 0.12f) else MicrobondOverlayWidgetDark)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isSelected) MicrobondActiveGreen.copy(alpha = 0.2f) else Color(0xFF1E2830)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MicrobondActiveGreen else MicrobondInactiveGray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = methodName,
                    color = if (isSelected) MicrobondActiveGreen else MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isDefault) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DEFAULT",
                        color = MicrobondActiveGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = accountNumber,
                color = MicrobondInactiveGray,
                fontSize = 13.sp
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MicrobondActiveGreen,
                unselectedColor = MicrobondInactiveGray
            )
        )
    }
}

package com.microbond.app.feature_services.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.services.ServiceCategoryTile
import com.microbond.app.core.components.services.ServiceHistoryTile
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

private data class UtilityHistoryItem(
    val id: Int,
    val provider: String,
    val initials: String,
    val providerColor: Color,
    val details: String,
    val time: String,
    val amount: String
)

@Composable
fun ServicesScreen(
    onNavigateToNewPayment: () -> Unit = {}
) {
    val recentPurchases = listOf(
        UtilityHistoryItem(
            id = 1,
            provider = "MTN Uganda",
            initials = "MTN",
            providerColor = Color(0xFFFFC928),
            details = "Airtime · 077•••4567",
            time = "Today, 10:30 AM",
            amount = "UGX 5,000"
        ),
        UtilityHistoryItem(
            id = 2,
            provider = "Airtel",
            initials = "A",
            providerColor = Color(0xFFE53935),
            details = "Data bundle · 1 GB",
            time = "Yesterday, 4:15 PM",
            amount = "UGX 2,000"
        ),
        UtilityHistoryItem(
            id = 3,
            provider = "UMEME Yaka",
            initials = "U",
            providerColor = Color(0xFF3D8B58),
            details = "Electricity token",
            time = "Aug 24, 2:00 PM",
            amount = "UGX 20,000"
        ),
        UtilityHistoryItem(
            id = 4,
            provider = "MTN Uganda",
            initials = "MTN",
            providerColor = Color(0xFFFFC928),
            details = "Airtime · 077•••4567",
            time = "Aug 20, 9:00 AM",
            amount = "UGX 10,000"
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Services",
                showSearchIcon = false,
                showMoreIcon = false
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewPayment,
                containerColor = MicrobondActiveGreen,
                contentColor = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = "New payment"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MicrobondDarkBackground)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            item {
                Text(
                    text = "Buy utilities",
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                )
            }

            item {
                ServiceCategoryTile(
                    title = "Buy airtime",
                    subtitle = "MTN, Airtel and more",
                    icon = Icons.Default.PhoneAndroid,
                    iconBgColor = MicrobondActiveGreen,
                    iconTintColor = Color(0xFF111B21),
                    onClick = onNavigateToNewPayment
                )
            }

            item {
                ServiceCategoryTile(
                    title = "Buy data bundles",
                    subtitle = "Daily, weekly and monthly packs",
                    icon = Icons.Default.Wifi,
                    iconBgColor = Color(0xFF2B343B),
                    onClick = onNavigateToNewPayment
                )
            }

            item {
                ServiceCategoryTile(
                    title = "Pay bills",
                    subtitle = "Electricity, water and pay TV",
                    icon = Icons.Default.Lightbulb,
                    iconBgColor = Color(0xFF2B343B),
                    onClick = onNavigateToNewPayment
                )
            }

            item {
                Text(
                    text = "Recent purchases",
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
                )
            }

            items(recentPurchases, key = { it.id }) { purchase ->
                ServiceHistoryTile(
                    providerName = purchase.provider,
                    providerInitials = purchase.initials,
                    providerColor = purchase.providerColor,
                    serviceDetails = purchase.details,
                    time = purchase.time,
                    amount = purchase.amount,
                    onClick = onNavigateToNewPayment
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
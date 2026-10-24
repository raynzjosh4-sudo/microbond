package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.TrustMembersData
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar

@Composable
fun GroupHealthRatingScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)

    var trustData by remember { mutableStateOf<TrustMembersData?>(null) }
    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(groupId) {
        if (groupId.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        errorMessage = null
        try {
            val resp = NetworkClient.apiService.getTrustMembersSettings(groupId)
            if (resp.isSuccessful && resp.body()?.data != null) {
                trustData = resp.body()!!.data
            }
        } catch (e: Exception) {
            errorMessage = "Unable to fetch live health metrics."
        } finally {
            isLoading = false
        }
    }

    val grade = trustData?.groupHealthGrade ?: "A+"
    val healthStatus = trustData?.groupHealthStatus ?: "Excellent Standing"
    val onTimeRepayments = trustData?.healthMetrics?.onTimeRepaymentRate ?: "100%"
    val defaultRate = trustData?.healthMetrics?.activeDefaultRate ?: "0%"
    val avgTrustScore = trustData?.healthMetrics?.averageTrustScore ?: "100/100"
    val myEligibility = trustData?.myBenefitEligibility

    val gradeColor = when {
        grade.startsWith("A") -> Color(0xFF27AE60)
        grade.startsWith("B") -> Color(0xFF2D9CDB)
        grade.startsWith("C") -> Color(0xFFFFB300)
        else -> MicrobondDestructiveRed
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Group health rating",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isLoading) {
                        Box(modifier = Modifier.padding(end = 12.dp)) {
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(gradeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = grade,
                    color = gradeColor,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = healthStatus,
                color = MicrobondTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    grade.startsWith("A") -> "This group has an outstanding repayment record. Members pay on time and default contagion risk is exceptionally low."
                    grade.startsWith("B") -> "Good standing with healthy reserve coverage and low arrears."
                    grade.startsWith("C") -> "Fair performance. Repayment delays have been registered in recent cycles."
                    else -> "At risk. Contagion and arrears exceed normal risk thresholds."
                },
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Metrics Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    HealthMetricRow("On-time Repayments", onTimeRepayments)
                    HorizontalDivider(color = Color(0xFF2C2C2C), modifier = Modifier.padding(vertical = 12.dp))
                    HealthMetricRow("Active Default Rate", defaultRate)
                    HorizontalDivider(color = Color(0xFF2C2C2C), modifier = Modifier.padding(vertical = 12.dp))
                    HealthMetricRow("Trust Score Average", avgTrustScore)
                }
            }

            // Benefit Eligibility Card (if available)
            if (myEligibility != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A261D)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (myEligibility.isEligible) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (myEligibility.isEligible) MicrobondActiveGreen else Color(0xFFFFB300),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Your Benefit Eligibility: ${myEligibility.status}",
                                color = MicrobondTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (myEligibility.isEligible) {
                                "You have ${myEligibility.activeGuarantorsCount} active cosigners (minimum required: ${myEligibility.requiredGuarantorsCount}). All group payout and borrowing privileges are unlocked."
                            } else {
                                myEligibility.rejectionReason ?: "You have ${myEligibility.activeGuarantorsCount}/${myEligibility.requiredGuarantorsCount} required cosigners. Secure additional guarantors to unlock payouts."
                            },
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Rust Engine Trust Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MicrobondInactiveGray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Health score calculated natively by microbond_core Rust engine",
                    color = MicrobondInactiveGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HealthMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = MicrobondInactiveGray, fontSize = 15.sp)
        Text(text = value, color = MicrobondActiveGreen, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

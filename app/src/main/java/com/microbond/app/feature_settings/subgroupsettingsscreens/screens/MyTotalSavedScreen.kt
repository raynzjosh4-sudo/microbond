package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.MembershipMeData
import com.microbond.app.core.network.api.goupapis.SavingsGrowthMeta
import com.microbond.app.core.network.api.goupapis.SavingsGrowthPoint
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondDragHandleDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondPureWhite
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTotalSavedScreen(
    groupName: String,
    groupId: String = "",
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var memberData by remember { mutableStateOf<MembershipMeData?>(null) }
    var growthMeta by remember { mutableStateOf<SavingsGrowthMeta?>(null) }
    var growthPoints by remember { mutableStateOf<List<SavingsGrowthPoint>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var isDownloadingStatement by remember { mutableStateOf(false) }
    var isLoadingGrowthChart by remember { mutableStateOf(false) }

    var showGrowthChartSheet by remember { mutableStateOf(false) }
    var selectedChartPeriod by remember { mutableStateOf("monthly") }
    var statementRestrictedMsg by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun loadGrowthChart(period: String) {
        if (groupId.isBlank()) return
        scope.launch {
            isLoadingGrowthChart = true
            try {
                val resp = NetworkClient.apiService.getSavingsGrowth(groupId, period = period)
                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!
                    growthMeta = body.meta
                    growthPoints = body.points
                }
            } catch (_: Exception) {
            } finally {
                isLoadingGrowthChart = false
            }
        }
    }

    LaunchedEffect(groupId) {
        if (groupId.isBlank()) { isLoading = false; return@LaunchedEffect }
        isLoading = true
        errorMsg = null
        try {
            // 1. Fetch member overview
            val memberResp = NetworkClient.apiService.getMyMembership(groupId)
            if (memberResp.isSuccessful) {
                memberData = memberResp.body()?.data
            }

            // 2. Fetch savings growth summary
            val growthResp = NetworkClient.apiService.getSavingsGrowth(groupId, period = "monthly")
            if (growthResp.isSuccessful && growthResp.body() != null) {
                growthMeta = growthResp.body()?.meta
                growthPoints = growthResp.body()?.points ?: emptyList()
            }
        } catch (e: Exception) {
            errorMsg = "Network error: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    fun downloadStatement() {
        if (groupId.isBlank()) return
        scope.launch {
            isDownloadingStatement = true
            statementRestrictedMsg = null
            try {
                val resp = NetworkClient.apiService.downloadSavingsStatement(
                    groupId = groupId,
                    periodDays = 30,
                    format = "csv"
                )
                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!
                    withContext(Dispatchers.IO) {
                        val file = File(context.cacheDir, "Group_${groupId}_Statement_Last30Days.csv")
                        file.outputStream().use { out -> body.byteStream().copyTo(out) }
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, "$groupName Savings Statement")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Savings Statement"))
                    }
                    Toast.makeText(context, "Statement downloaded successfully", Toast.LENGTH_SHORT).show()
                } else if (resp.code() == 403) {
                    statementRestrictedMsg = "The Group Admin has restricted members from viewing or downloading their savings statements."
                } else {
                    Toast.makeText(context, "Failed to download statement (HTTP ${resp.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                isDownloadingStatement = false
            }
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Total saved",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isLoading || isDownloadingStatement) {
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
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            errorMsg?.let { msg ->
                Text(
                    text = "⚠ $msg",
                    color = MicrobondDestructiveRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── Hero Total Saved Card ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MicrobondOverlayWidgetDark)
                    .padding(vertical = 28.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Saved (with Interest)", color = MicrobondInactiveGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        CircularProgressIndicator(
                            color = MicrobondActiveGreen,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    val isHidden = memberData?.savingsHidden == true
                    val currency = memberData?.currency ?: growthMeta?.currency ?: "UGX"

                    val rawFormatted = memberData?.totalSavedFormatted
                        ?: growthMeta?.totalSavedFormatted
                        ?: "0"

                    val formattedDisplay = if (rawFormatted.contains(currency, ignoreCase = true)) {
                        rawFormatted
                    } else {
                        "$rawFormatted $currency"
                    }

                    val interestGained = growthMeta?.interestGained ?: 0.0

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Total Saved (with Interest)",
                            color = MicrobondInactiveGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHidden) "Hidden by group admin" else formattedDisplay,
                            color = if (isHidden) MicrobondInactiveGray else MicrobondTextPrimary,
                            fontSize = if (isHidden) 18.sp else 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        if (isHidden) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "The group admin has hidden total savings.",
                                color = MicrobondInactiveGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        } else if (interestGained > 0.0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            val formattedInterest = NumberFormat.getNumberInstance(Locale.US).format(interestGained)
                            Text(
                                text = "+$formattedInterest $currency gained in interest",
                                color = MicrobondActiveGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            WaSettingsSectionHeader("Savings actions")

            WaSettingsRow(
                icon = Icons.Default.TrendingUp,
                title = "View growth chart",
                subtitle = "See how your savings have compounded over time.",
                onClick = {
                    showGrowthChartSheet = true
                    loadGrowthChart(selectedChartPeriod)
                }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Download,
                title = if (isDownloadingStatement) "Downloading statement…" else "Download savings statement",
                subtitle = "Export your cumulative savings history.",
                onClick = {
                    if (!isDownloadingStatement) {
                        downloadStatement()
                    }
                }
            )

            WaSettingsDivider()
        }
    }

    // ── Growth Chart Modal Bottom Sheet ──
    if (showGrowthChartSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val currency = growthMeta?.currency ?: memberData?.currency ?: "UGX"

        ModalBottomSheet(
            onDismissRequest = { showGrowthChartSheet = false },
            sheetState = sheetState,
            containerColor = MicrobondOverlayWidgetDark,
            dragHandle = { BottomSheetDefaults.DragHandle(color = MicrobondDragHandleDark) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Savings growth",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MicrobondTextPrimary
                        )
                        Text(
                            text = groupName,
                            fontSize = 13.sp,
                            color = MicrobondInactiveGray
                        )
                    }
                    IconButton(onClick = { showGrowthChartSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MicrobondInactiveGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Period Chips (Weekly, Monthly, All Time)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("weekly" to "Weekly", "monthly" to "Monthly", "all_time" to "All time").forEach { (code, label) ->
                        val isSelected = selectedChartPeriod == code
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedChartPeriod = code
                                loadGrowthChart(code)
                            },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MicrobondDarkBackground,
                                selectedContainerColor = MicrobondActiveGreen.copy(alpha = 0.2f),
                                labelColor = MicrobondInactiveGray,
                                selectedLabelColor = MicrobondActiveGreen
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) MicrobondActiveGreen else MicrobondDragHandleDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Growth summary bar
                val totalAmount = growthMeta?.totalSavedFormatted
                    ?: memberData?.totalSavedFormatted
                    ?: "0 $currency"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MicrobondDarkBackground)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Cumulative saved",
                                color = MicrobondInactiveGray,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = totalAmount,
                                color = MicrobondTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        growthMeta?.interestGained?.let { interest ->
                            if (interest > 0.0) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Interest gained",
                                        color = MicrobondInactiveGray,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "+${NumberFormat.getNumberInstance(Locale.US).format(interest)} $currency",
                                        color = MicrobondActiveGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoadingGrowthChart) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MicrobondActiveGreen,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else if (growthPoints.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No savings recorded yet for this period.",
                            color = MicrobondInactiveGray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Text(
                        text = "History breakdown",
                        color = MicrobondInactiveGray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(growthPoints) { point ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MicrobondDarkBackground)
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(MicrobondActiveGreen.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = MicrobondActiveGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = point.label,
                                            color = MicrobondTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Cumulative: ${NumberFormat.getNumberInstance(Locale.US).format(point.cumulativeSaved)} $currency",
                                            color = MicrobondInactiveGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Text(
                                    text = "+${NumberFormat.getNumberInstance(Locale.US).format(point.amountSavedInPeriod)} $currency",
                                    color = MicrobondActiveGreen,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Statement Restricted Alert Dialog ──
    statementRestrictedMsg?.let { reason ->
        AlertDialog(
            onDismissRequest = { statementRestrictedMsg = null },
            containerColor = MicrobondOverlayWidgetDark,
            title = {
                Text(
                    text = "Statement Restricted",
                    color = MicrobondTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = reason,
                    color = MicrobondInactiveGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { statementRestrictedMsg = null }) {
                    Text("OK", color = MicrobondActiveGreen, fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

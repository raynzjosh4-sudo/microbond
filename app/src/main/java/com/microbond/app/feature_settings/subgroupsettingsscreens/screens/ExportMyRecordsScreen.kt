package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ExportMyRecordsScreen(
    groupName: String,
    groupId: String = "",
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var includeChat by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var exportSuccess by remember { mutableStateOf(false) }
    var exportError by remember { mutableStateOf<String?>(null) }

    fun doExport() {
        if (groupId.isBlank()) { exportError = "No group selected."; return }
        scope.launch {
            isExporting = true
            exportSuccess = false
            exportError = null
            try {
                val resp = NetworkClient.apiService.exportMyRecords(groupId, includeChat)
                if (resp.isSuccessful) {
                    val body = resp.body()!!
                    withContext(Dispatchers.IO) {
                        val file = File(context.cacheDir, "microbond_export_${System.currentTimeMillis()}.zip")
                        file.outputStream().use { out -> body.byteStream().copyTo(out) }
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/zip"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share export"))
                    }
                    exportSuccess = true
                } else {
                    exportError = "Export failed (HTTP ${resp.code()}). Please try again."
                }
            } catch (e: Exception) {
                exportError = "Network error: ${e.localizedMessage}"
            }
            isExporting = false
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Export my records",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isExporting) {
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
            WaSettingsSectionHeader("Export options")

            WaSettingsSwitchRow(
                icon = Icons.Default.Email,
                title = "Include chat history",
                subtitle = "Export your financial records alongside your personal chat messages from this group.",
                checked = includeChat,
                onCheckedChange = { includeChat = it }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Share,
                title = if (isExporting) "Preparing export…" else "Export data",
                subtitle = "Generate a secure ZIP file containing your complete activity ledger.",
                onClick = { if (!isExporting) doExport() }
            )

            if (exportSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MicrobondActiveGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export ready! Check your share sheet.", color = MicrobondActiveGreen, style = MaterialTheme.typography.bodySmall)
                }
            }

            exportError?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Text("⚠ $msg", color = Color(0xFFFF4F4F), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 24.dp))
            }
        }
    }
}

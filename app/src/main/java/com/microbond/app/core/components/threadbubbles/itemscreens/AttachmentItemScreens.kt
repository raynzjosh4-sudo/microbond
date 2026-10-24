package com.microbond.app.core.components.threadbubbles.itemscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.data_classes_and_enums.AttachmentOption
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.AiImagesScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.CameraScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.ContactSendItem
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens.selectscreens.ContactScreen
import com.microbond.app.core.data_classes_and_enums.DocumentData
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.documentscreens.DocumentScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.EventScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.GalleryScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.LocationScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.PollScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.RulesScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.SaveReportScreen
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.StatisticsScreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground

private val panelColor = Color(0xFF1F2C34)
private val primaryText = Color(0xFFE9EDEF)
private val secondaryText = Color(0xFF9EADB5)

@Composable
fun SaveReportAttachmentScreen(
    onBack: () -> Unit,
    onCreateRequest: (includedSections: Set<String>, period: String, selectedDate: String) -> Unit
) = SaveReportScreen(onBack, onCreateRequest)

@Composable
fun LocationAttachmentScreen(onBack: () -> Unit, onPreview: (String) -> Unit) =
    LocationScreen(onBack, onPreview)

@Composable
fun ContactAttachmentScreen(
    onBack: () -> Unit,
    initiallySelected: List<ContactSendItem>,
    onPreview: (List<ContactSendItem>) -> Unit
) = ContactScreen(onBack, initiallySelected, onPreview)

@Composable
fun DocumentAttachmentScreen(
    onBack: () -> Unit,
    onPreview: (DocumentData) -> Unit
) = DocumentScreen(onBack, onPreview)

@Composable
fun PollAttachmentScreen(
    onBack: () -> Unit,
    onSend: (question: String, options: List<String>, allowMultipleAnswers: Boolean) -> Unit
) = PollScreen(onBack, onSend)

@Composable
fun RulesAttachmentScreen(
    onBack: () -> Unit,
    groupName: String,
    onSend: (selectedRules: List<String>, singleRuleText: String, singleRuleStatus: String?) -> Unit
) = RulesScreen(onBack, onSend, groupName)

@Composable
fun StatisticsAttachmentScreen(onBack: () -> Unit, onPreview: (String) -> Unit) =
    StatisticsScreen(onBack, onPreview)

@Composable
fun AdditionalAttachmentScreen(
    option: AttachmentOption,
    onBack: () -> Unit,
    onPreview: (String) -> Unit
) {
    when (option) {
        AttachmentOption.DEPOSIT_STATUS -> Unit
        AttachmentOption.SAVE_REPORT -> SaveReportScreen(onBack) { sections, period, selectedDate ->
            onPreview("${sections.joinToString(", ")} · $period · $selectedDate")
        }
        AttachmentOption.LOCATION -> LocationScreen(onBack, onPreview)
        AttachmentOption.CONTACT -> ContactScreen(
            onBack = onBack,
            onPreview = { contacts -> onPreview(contacts.joinToString("\n") { "${it.name} · ${it.phone}" }) }
        )
        AttachmentOption.DOCUMENT -> DocumentScreen(
            onBack = onBack,
            onPreview = { document -> onPreview(document.fileName) }
        )
        AttachmentOption.POLL -> PollScreen(onBack) { question, options, allowMultipleAnswers ->
            onPreview(
                (listOf(question) + options.map { "• $it" } +
                    "Multiple answers: ${if (allowMultipleAnswers) "allowed" else "not allowed"}")
                    .joinToString("\n")
            )
        }
        AttachmentOption.RULES -> RulesScreen(onBack, { rules, singleRule, status ->
            onPreview((rules + singleRule.takeIf(String::isNotBlank).orEmpty() + status.orEmpty()).joinToString("\n"))
        })
        AttachmentOption.STATISTICS -> StatisticsScreen(onBack, onPreview)
        AttachmentOption.GALLERY -> GalleryScreen(onBack, onPreview)
        AttachmentOption.CAMERA -> CameraScreen(onBack, onPreview)
        AttachmentOption.EVENT -> EventScreen(onBack, onPreview)
        AttachmentOption.AI_IMAGES -> AiImagesScreen(onBack, onPreview)
    }
}

@Composable
fun AttachmentPreviewScreen(
    option: AttachmentOption,
    summary: String,
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MicrobondDarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryText)
            }
            Text("Preview", color = primaryText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = panelColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(option.screenTitle(), color = primaryText, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Text(summary, color = secondaryText)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Edit") }
            Button(onClick = onClose, modifier = Modifier.weight(1f)) { Text("Done") }
        }
    }
}

private fun AttachmentOption.screenTitle(): String = when (this) {
    AttachmentOption.DEPOSIT_STATUS -> "Deposit status"
    AttachmentOption.SAVE_REPORT -> "Save report"
    AttachmentOption.LOCATION -> "Location"
    AttachmentOption.CONTACT -> "Contact"
    AttachmentOption.DOCUMENT -> "Document"
    AttachmentOption.POLL -> "Poll"
    AttachmentOption.RULES -> "Rules"
    AttachmentOption.STATISTICS -> "Statistics"
    else -> name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
}
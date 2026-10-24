package com.microbond.app.core.components.threadbubbles.itemscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

private val PollFieldBackground = AttachmentPanelColor
private val PollFieldBorder = Color(0xFF455156)

@Composable
fun PollScreen(
    onBack: () -> Unit,
    onSend: (question: String, options: List<String>, allowMultipleAnswers: Boolean) -> Unit
) {
    var question by remember { mutableStateOf("") }
    val options = remember { mutableStateListOf("", "") }
    var allowMultipleAnswers by remember { mutableStateOf(true) }
    val readyToSend = question.isNotBlank() &&
        options.count { it.isNotBlank() } >= 2 &&
        options.dropLast(1).all { it.isNotBlank() }

    BackHandler(onBack = onBack)

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MicrobondTextPrimary
                    )
                }
                Text("Create poll", color = MicrobondTextPrimary, fontSize = 18.sp)
            }
        },
        bottomBar = {
            Surface(color = MicrobondDarkBackground) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            if (readyToSend) {
                                onSend(
                                    question.trim(),
                                    options.map(String::trim).filter(String::isNotBlank),
                                    allowMultipleAnswers
                                )
                            }
                        },
                        enabled = readyToSend,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (readyToSend) MicrobondActiveGreen else PollFieldBorder,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send poll",
                            tint = Color(0xFF07130F)
                        )
                    }
                }
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Question", color = Color(0xFF9EADB5), fontSize = 13.sp)
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ask question", color = Color(0xFF8696A0)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = RoundedCornerShape(6.dp),
                colors = pollFieldColors()
            )

            Spacer(Modifier.height(2.dp))
            Text("Options", color = Color(0xFF9EADB5), fontSize = 13.sp)
            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = option,
                        onValueChange = { value ->
                            options[index] = value
                            if (
                                index == options.lastIndex &&
                                value.isNotBlank() &&
                                options.size < 10
                            ) {
                                options.add("")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Add", color = Color(0xFF8696A0)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(6.dp),
                        colors = pollFieldColors()
                    )
                    if (options.size > 2) {
                        IconButton(onClick = { options.removeAt(index) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove option",
                                tint = Color(0xFF9EADB5)
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Allow multiple answers", color = MicrobondTextPrimary, fontSize = 14.sp)
                Switch(
                    checked = allowMultipleAnswers,
                    onCheckedChange = { allowMultipleAnswers = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MicrobondActiveGreen
                    )
                )
            }
        }
    }
}

@Composable
private fun pollFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = PollFieldBackground,
    unfocusedContainerColor = PollFieldBackground,
    focusedTextColor = MicrobondTextPrimary,
    unfocusedTextColor = MicrobondTextPrimary,
    focusedIndicatorColor = MicrobondActiveGreen,
    unfocusedIndicatorColor = PollFieldBorder,
    cursorColor = MicrobondActiveGreen
)

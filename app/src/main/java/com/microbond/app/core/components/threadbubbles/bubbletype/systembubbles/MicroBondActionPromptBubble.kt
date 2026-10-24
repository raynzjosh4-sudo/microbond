package com.microbond.app.core.components.threadbubbles.bubbletype.systembubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondSystemBg
import com.microbond.app.core.ui.theme.MicrobondActiveGreen


@Composable
fun MicroBondActionPromptBubble(
    message: String,
    actionText: String? = null,
    onActionClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        SelectionContainer {
            val mdRegex = Regex("\\*\\*(.*?)\\*\\*|\\*[\\d\\*]+#")
            val matches = mdRegex.findAll(message).toList()

            if (matches.isNotEmpty()) {
                val annotatedString = buildAnnotatedString {
                    var currentIndex = 0
                    for (match in matches) {
                        val startIndex = match.range.first
                        val endIndex = match.range.last + 1
                        
                        // Append normal text before the match
                        append(message.substring(currentIndex, startIndex))
                        
                        // Append the bolded text in green
                        val innerText = if (match.value.startsWith("**")) match.groupValues[1] else match.value
                        pushStringAnnotation(tag = "ACTION", annotation = innerText)
                        withStyle(style = SpanStyle(
                            color = MicrobondActiveGreen,
                            fontWeight = FontWeight.SemiBold
                        )) {
                            append(innerText)
                        }
                        pop()
                        
                        currentIndex = endIndex
                    }
                    // Append remaining text
                    append(message.substring(currentIndex))
                }
                ClickableText(
                    text = annotatedString,
                    style = TextStyle(
                        color = Color(0xFFE9EDEF),
                        fontSize = 12.sp, // Reduced font size
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .background(MicroBondSystemBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(tag = "ACTION", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                onActionClick?.invoke(annotation.item)
                            }
                    }
                )
            } else {
                Text(
                    text = message,
                    color = Color(0xFFE9EDEF),
                    fontSize = 12.sp, // Reduced font size
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(MicroBondSystemBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

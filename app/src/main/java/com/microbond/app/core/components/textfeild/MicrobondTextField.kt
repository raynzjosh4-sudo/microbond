package com.microbond.app.core.components.textfeild

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * WhatsApp-accurate underline-only text field.
 *
 * - No border box — only a green bottom line when focused (via [showDivider])
 * - Hint text shown when [value] is empty
 * - Supports single and multi-line modes
 * - Optional [label] shown above the field as a small caption
 * - Forwards [keyboardOptions] and [keyboardActions] for flexibility
 */
@Composable
fun MicrobondTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    showDivider: Boolean = true,
    textStyle: TextStyle = TextStyle(
        color = MicrobondTextPrimary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal
    ),
    hintStyle: TextStyle = TextStyle(
        color = MicrobondInactiveGray,
        fontSize = 17.sp
    )
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                color = MicrobondActiveGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = textStyle,
            cursorBrush = SolidColor(MicrobondActiveGreen),
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = capitalization,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = hintStyle
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 6.dp),
                color = if (value.isNotEmpty()) MicrobondActiveGreen else MicrobondDividerDark,
                thickness = 1.dp
            )
        }
    }
}

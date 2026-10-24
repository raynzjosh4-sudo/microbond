package com.microbond.app.core.components.reusablemicrobuttonbig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondButtonDisabledBackground
import com.microbond.app.core.ui.theme.MicrobondButtonDisabledText
import com.microbond.app.core.ui.theme.microBondTextPrimaryLight

/**
 * Reusable full-width pill button styled for WhatsApp-like flows.
 *
 * Supports both enabled (vivid green with dark label) and disabled
 * (muted surface with gray label) states as per design specifications.
 */
@Composable
fun ReusableMicroButtonBig(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    containerColor: Color = MicrobondActiveGreen,
    contentColor: Color = microBondTextPrimaryLight,
    disabledContainerColor: Color = MicrobondButtonDisabledBackground,
    disabledContentColor: Color = MicrobondButtonDisabledText
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (enabled) contentColor else disabledContentColor
                    )
                )

                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun ReusableMicroButtonBigEnabledPreview() {
    ReusableMicroButtonBig(
        text = "Agree and continue",
        onClick = {},
        enabled = true
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun ReusableMicroButtonBigDisabledPreview() {
    ReusableMicroButtonBig(
        text = "Next",
        onClick = {},
        enabled = false
    )
}

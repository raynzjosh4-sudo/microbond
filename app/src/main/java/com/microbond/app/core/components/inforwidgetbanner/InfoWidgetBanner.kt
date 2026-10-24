package com.microbond.app.core.components.inforwidgetbanner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondInfoBannerBorder
import com.microbond.app.core.ui.theme.MicrobondInfoBannerButtonContent
import com.microbond.app.core.ui.theme.MicrobondInfoBannerContainer
import com.microbond.app.core.ui.theme.MicrobondInfoBannerIconBg
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Authentic WhatsApp-style promotional / informative banner card widget.
 *
 * Design features:
 * - Direct, vibrant leading icon / image on the left without clumsy extra containers.
 * - Bold title header and clear descriptive text with inline clickable link support.
 * - ALL buttons placed strictly on the right side.
 * - Bold, crisp 'X' dismiss button with rounded stroke caps in the top-right corner.
 * - Physical elevation with ambient and spot drop shadows plus perimeter border highlight.
 * - WhatsApp-style active green pill button with elevation and high contrast typography.
 */
@Composable
fun InfoWidgetBanner(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    // Leading image / icon options
    leadingImage: (@Composable () -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    leadingPainter: Painter? = null,
    leadingIconContentDescription: String? = null,
    useIconContainer: Boolean = false,
    // Link options
    linkText: String? = null,
    onLinkClick: (() -> Unit)? = null,
    // Button options (ALL placed strictly on the right)
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    actionButton: (@Composable () -> Unit)? = null,
    isButtonOnSide: Boolean = true,
    // Dismiss / Close options
    showDismissButton: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    closeIconTint: Color = MicrobondTextPrimary,
    closeIconStrokeWidth: Dp = 3.2.dp,
    // Entire card click
    onClick: (() -> Unit)? = null,
    // Styling & Theme tokens
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 6.dp,
    containerColor: Color = MicrobondInfoBannerContainer,
    leadingIconContainerColor: Color = MicrobondInfoBannerIconBg,
    contentColor: Color = MicrobondTextPrimary,
    accentColor: Color = MicrobondActiveGreen,
    buttonContentColor: Color = MicrobondInfoBannerButtonContent,
    border: BorderStroke? = null
) {
    val hasDismiss = showDismissButton && onDismiss != null
    val hasActionButton = actionButton != null || !buttonText.isNullOrBlank()
    val hasRightActions = hasDismiss || hasActionButton

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.65f),
                spotColor = Color.Black.copy(alpha = 0.85f)
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = elevation,
        tonalElevation = 4.dp,
        border = border ?: BorderStroke(1.dp, MicrobondInfoBannerBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ── Left: Leading Image / Icon Slot ──
            if (leadingImage != null) {
                Box(modifier = Modifier.padding(top = 2.dp, end = 12.dp)) {
                    leadingImage()
                }
            } else if (leadingPainter != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp, end = 12.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = leadingPainter,
                        contentDescription = leadingIconContentDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else if (leadingIcon != null) {
                if (useIconContainer) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 12.dp)
                            .size(38.dp)
                            .background(
                                color = leadingIconContainerColor,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = leadingIconContentDescription,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    // Direct WhatsApp style leading icon
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 12.dp)
                            .size(34.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = leadingIconContentDescription,
                            tint = accentColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // ── Center: Title, Description, and Link ──
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = if (hasRightActions) 8.dp else 0.dp),
                verticalArrangement = Arrangement.Top
            ) {
                if (!title.isNullOrBlank()) {
                    Text(
                        text = title,
                        color = contentColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    )
                }

                if (!description.isNullOrBlank() || !linkText.isNullOrBlank()) {
                    if (!title.isNullOrBlank()) {
                        Spacer(Modifier.height(3.dp))
                    }

                    val annotatedText = buildAnnotatedString {
                        if (!description.isNullOrBlank()) {
                            append(description)
                        }

                        if (!linkText.isNullOrBlank()) {
                            if (!description.isNullOrBlank()) {
                                append(" ")
                            }

                            val linkStyles = TextLinkStyles(
                                style = SpanStyle(
                                    color = accentColor,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = TextDecoration.None
                                )
                            )

                            withLink(
                                LinkAnnotation.Clickable(
                                    tag = "LINK_TAG",
                                    styles = linkStyles,
                                    linkInteractionListener = {
                                        onLinkClick?.invoke()
                                    }
                                )
                            ) {
                                append(linkText)
                            }
                        }
                    }

                    Text(
                        text = annotatedText,
                        color = MicrobondInactiveGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // ── Right: ALL Buttons Placed Cleanly on the Right Side ──
            if (hasRightActions) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(start = 2.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Top-right: Bold 'X' Dismiss Button
                    if (hasDismiss) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onDismiss!!),
                            contentAlignment = Alignment.Center
                        ) {
                            BoldCloseIcon(
                                tint = closeIconTint,
                                strokeWidth = closeIconStrokeWidth,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Action Button(s) on the right side
                    if (hasActionButton) {
                        if (hasDismiss) {
                            Spacer(Modifier.height(8.dp))
                        }

                        if (actionButton != null) {
                            actionButton()
                        } else if (!buttonText.isNullOrBlank()) {
                            Button(
                                onClick = { onButtonClick?.invoke() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentColor,
                                    contentColor = buttonContentColor
                                ),
                                shape = RoundedCornerShape(18.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 3.dp,
                                    pressedElevation = 1.dp
                                ),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = buttonText,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.2.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Authentic WhatsApp-style bold 'X' close/dismiss icon with crisp round caps.
 */
@Composable
fun BoldCloseIcon(
    modifier: Modifier = Modifier,
    tint: Color = MicrobondTextPrimary,
    strokeWidth: Dp = 3.2.dp
) {
    Canvas(modifier = modifier) {
        val strokePx = strokeWidth.toPx()
        val pad = strokePx / 2f
        // First diagonal: top-left to bottom-right
        drawLine(
            color = tint,
            start = Offset(pad, pad),
            end = Offset(size.width - pad, size.height - pad),
            strokeWidth = strokePx,
            cap = StrokeCap.Round
        )
        // Second diagonal: top-right to bottom-left
        drawLine(
            color = tint,
            start = Offset(size.width - pad, pad),
            end = Offset(pad, size.height - pad),
            strokeWidth = strokePx,
            cap = StrokeCap.Round
        )
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun InfoWidgetBannerUsernamesPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
            .padding(16.dp)
    ) {
        InfoWidgetBanner(
            title = "Usernames are coming soon",
            description = "Keep your phone number private with a username.",
            linkText = "Reserve yours",
            leadingIcon = Icons.Default.AlternateEmail,
            onLinkClick = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun InfoWidgetBannerWithButtonPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
            .padding(16.dp)
    ) {
        InfoWidgetBanner(
            title = "Identity verification required",
            description = "Complete KYC Tier 2 verification to unlock group archetypes and start pooling funds.",
            linkText = "Learn more",
            buttonText = "Verify",
            leadingIcon = Icons.Default.Lock,
            elevation = 6.dp,
            onButtonClick = {},
            onLinkClick = {},
            onDismiss = {}
        )
    }
}

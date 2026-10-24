package com.microbond.app.core.components.threadbubbles

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── WhatsApp-accurate dark-theme palette ──
val MicroBondInk = Color(0xFFE9EDEF)           // Primary text on bubbles
val MicroBondMuted = Color(0xFF8696A0)          // Secondary text / timestamps
val MicroBondIncoming = Color(0xFF202C33)       // Incoming bubble background
val MicroBondOutgoing = Color(0xFF005C4B)       // Outgoing bubble background
val MicroBondCanvas = Color(0xFF0B141A)         // Chat background
val MicroBondAccent = Color(0xFF00A884)         // Accent green (WhatsApp teal)
val MicroBondReplyBg = Color(0xFF1D282F)        // Reply quote background (incoming)
val MicroBondReplyBgOutgoing = Color(0xFF025144) // Reply quote background (outgoing)
val MicroBondTickBlue = Color(0xFF53BDEB)       // Blue read ticks
val MicroBondDatePill = Color(0xFF182229)       // Date divider pill background
val MicroBondSystemBg = Color(0xFF182229)       // System message background

@Composable
fun MicroBondBubbleTheme(content: @Composable () -> Unit) {
    val scheme = darkColorScheme(
        background = MicroBondCanvas,
        surface = MicroBondIncoming,
        primary = MicroBondAccent,
        onSurface = MicroBondInk,
        onBackground = MicroBondInk,
    )
    MaterialTheme(colorScheme = scheme, content = content)
}

 package com.microbond.app.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Deterministic color picker for chat bubble senders and member avatars.
 */
object AvatarColorHelper {
    private val palette = listOf(
        Color(0xFF00A884),
        Color(0xFF527E65),
        Color(0xFF8D765F),
        Color(0xFF9D746A),
        Color(0xFF617F91),
        Color(0xFF96665C),
        Color(0xFF765A8D),
        Color(0xFF527B91),
        Color(0xFF8A795A),
        Color(0xFF557D70),
        Color(0xFF3E8C6C),
        Color(0xFF2F7D6C)
    )

    fun getColor(seed: String?): Color {
        if (seed.isNullOrBlank()) return palette.first()
        val index = (seed.hashCode() and 0x7FFFFFFF) % palette.size
        return palette[index]
    }
}

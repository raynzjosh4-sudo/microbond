package com.microbond.app.core.components.countrypicker

import androidx.annotation.DrawableRes

/**
 * Represents a country with its official English name, native localized name,
 * international telephone dialing code, ISO 3166-1 alpha-2 code, drawable flag, and fallback emoji.
 */
data class Country(
    val name: String,
    val nativeName: String? = null,
    val dialCode: String,
    val isoCode: String,
    @DrawableRes val flagResId: Int = 0,
    val flagEmoji: String = isoToFlagEmoji(isoCode)
)

/**
 * Computes a standard Unicode flag emoji directly from a 2-letter ISO country code.
 */
fun isoToFlagEmoji(isoCode: String): String {
    if (isoCode.length != 2) return "🌐"
    val codeUpper = isoCode.uppercase()
    val firstChar = Character.codePointAt(codeUpper, 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(codeUpper, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}

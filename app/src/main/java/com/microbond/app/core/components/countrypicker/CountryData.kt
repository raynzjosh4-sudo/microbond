package com.microbond.app.core.components.countrypicker

import com.hbb20.CCPCountry
import java.util.Locale

/**
 * Country repository leveraging the CCP telephony metadata library to provide
 * all world countries, authentic drawable flags, dialing codes, and localized native names
 * without hardcoded lists.
 */
object CountryData {

    /**
     * Dynamically loads all countries and their bundled drawable flag assets
     * from the CCP library and the Android Locale engine.
     */
    val allCountries: List<Country> by lazy {
        try {
            val ccpList = CCPCountry.getLibraryMasterCountriesEnglish()
            ccpList.map { ccp ->
                val iso = ccp.nameCode.uppercase()
                val native = try {
                    val loc = Locale(iso.lowercase(), iso)
                    val display = loc.getDisplayCountry(loc)
                    if (display.isNotBlank() && !display.equals(ccp.name, ignoreCase = true)) display else null
                } catch (e: Exception) {
                    null
                }
                Country(
                    name = ccp.name,
                    nativeName = native,
                    dialCode = ccp.phoneCode,
                    isoCode = iso,
                    flagResId = ccp.flagID
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Featured/popular countries matching WhatsApp's initial list order.
     */
    val popularCountries: List<Country> by lazy {
        val popularIsos = listOf("IN", "PK", "ZA", "GB", "US", "UG", "KE", "TZ", "NG")
        popularIsos.mapNotNull { iso -> allCountries.find { it.isoCode == iso } }
    }

    /**
     * Resolves a country by dialing code (checks popular first, then all).
     */
    fun findByDialCode(dialCode: String): Country? {
        val clean = dialCode.removePrefix("+").trim()
        if (clean.isEmpty()) return null
        return popularCountries.find { it.dialCode == clean }
            ?: allCountries.find { it.dialCode == clean }
    }

    /**
     * Resolves a country by 2-letter ISO code.
     */
    fun findByIso(iso: String): Country? {
        return allCountries.find { it.isoCode.equals(iso.trim(), ignoreCase = true) }
    }

    /**
     * Filters countries in real-time by English name, native name, dial code, or ISO code.
     */
    fun search(query: String): List<Country> {
        if (query.isBlank()) return allCountries
        val q = query.trim().lowercase()
        val numQuery = q.removePrefix("+")
        return allCountries.filter {
            it.name.lowercase().contains(q) ||
            (it.nativeName?.lowercase()?.contains(q) == true) ||
            it.dialCode.startsWith(numQuery) ||
            it.isoCode.lowercase() == q
        }
    }
}

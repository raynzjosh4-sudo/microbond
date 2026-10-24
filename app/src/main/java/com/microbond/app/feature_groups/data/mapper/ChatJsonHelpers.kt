package com.microbond.app.feature_groups.data.mapper

import com.google.gson.JsonElement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatJsonHelpers {

    fun jsonElementToMap(element: JsonElement?): Map<String, Any?> {
        if (element == null || element.isJsonNull) return emptyMap()
        if (element.isJsonObject) {
            val map = mutableMapOf<String, Any?>()
            val obj = element.asJsonObject
            for ((key, value) in obj.entrySet()) {
                map[key] = jsonElementToAny(value)
            }
            return map
        }
        if (element.isJsonArray) {
            val array = element.asJsonArray
            if (array.size() > 0 && array[0].isJsonObject) {
                return jsonElementToMap(array[0])
            }
        }
        return emptyMap()
    }

    fun jsonElementToList(element: JsonElement?): List<Map<String, Any?>> {
        if (element == null || element.isJsonNull || !element.isJsonArray) return emptyList()
        val list = mutableListOf<Map<String, Any?>>()
        for (item in element.asJsonArray) {
            if (item.isJsonObject) {
                list.add(jsonElementToMap(item))
            }
        }
        return list
    }

    fun jsonElementToAny(element: JsonElement): Any? {
        return when {
            element.isJsonNull -> null
            element.isJsonPrimitive -> {
                val prim = element.asJsonPrimitive
                when {
                    prim.isBoolean -> prim.asBoolean
                    prim.isNumber -> prim.asNumber
                    else -> prim.asString
                }
            }
            element.isJsonObject -> jsonElementToMap(element)
            element.isJsonArray -> element.asJsonArray.map { jsonElementToAny(it) }
            else -> null
        }
    }

    fun toMillis(timestamp: Long): Long {
        if (timestamp <= 0L) return System.currentTimeMillis()
        return if (timestamp < 10_000_000_000L) timestamp * 1000L else timestamp
    }

    /**
     * Formats the exact time of day (e.g., "8:17 PM") for message and chart bubbles.
     */
    fun formatTimeOfDay(timestamp: Long): String {
        val millis = toMillis(timestamp)
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
    }

    /**
     * Backward-compatible facade: always returns the time of day so bubbles display
     * the actual time they were sent, not a truncated date.
     */
    fun formatTime(timestamp: Long): String {
        return formatTimeOfDay(timestamp)
    }

    /**
     * Formats a human-readable calendar date divider label:
     * - "Today" if sent today
     * - "Yesterday" if sent yesterday
     * - Day of week (e.g. "Monday") if sent within the past 6 days
     * - Full date (e.g. "October 11, 2026") otherwise
     */
    fun formatDateDivider(timestamp: Long): String {
        val millis = toMillis(timestamp)
        val date = Date(millis)
        val now = Date()

        val dayFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val msgDay = dayFormat.format(date).toIntOrNull() ?: 0
        val nowDay = dayFormat.format(now).toIntOrNull() ?: 0

        if (msgDay == nowDay) return "Today"
        if (nowDay - msgDay == 1) return "Yesterday"

        val diffDays = (now.time - date.time) / (1000 * 60 * 60 * 24)
        return if (diffDays in 2..6) {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(date)
        }
    }

    /**
     * Returns a stable calendar day key (yyyy-MM-dd) to detect date boundaries between messages.
     */
    fun getDayKey(timestamp: Long): String {
        val millis = toMillis(timestamp)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
    }
}


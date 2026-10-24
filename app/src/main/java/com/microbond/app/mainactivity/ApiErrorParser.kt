package com.microbond.app.mainactivity

import org.json.JSONArray
import org.json.JSONObject

/**
 * Parses HTTP error response bodies into concise, user-friendly messages.
 * Handles FastAPI/Pydantic validation schemas, standard JSON, and HTML error pages.
 */
fun parseApiError(errorBody: String?, fallback: String): String {
    if (errorBody.isNullOrBlank()) return fallback
    val trimmed = errorBody.trim()
    if (trimmed.startsWith("<") || trimmed.contains("<!DOCTYPE", ignoreCase = true) || trimmed.contains("<html", ignoreCase = true)) {
        return fallback
    }
    return try {
        val json = JSONObject(trimmed)
        when {
            json.has("detail") -> {
                val detailObj = json.get("detail")
                when (detailObj) {
                    is String -> detailObj
                    is JSONArray -> {
                        val first = detailObj.optJSONObject(0)
                        first?.optString("msg") ?: fallback
                    }
                    else -> detailObj.toString()
                }
            }
            json.has("message") -> json.getString("message")
            json.has("error") -> json.getString("error")
            else -> fallback
        }
    } catch (_: Exception) {
        fallback
    }
}

package com.microbond.app.feature_groups.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class TrustConnection(
    val name: String,
    val phoneNumber: String
)

object TrustConnectionRepository {
    private const val preferencesName = "trust_connections"
    private const val connectionsKey = "connections"

    fun load(context: Context): List<TrustConnection> {
        val rawConnections = context
            .getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
            .getString(connectionsKey, "[]")
            ?: "[]"

        return runCatching {
            val jsonArray = JSONArray(rawConnections)
            List(jsonArray.length()) { index ->
                val item = jsonArray.getJSONObject(index)
                TrustConnection(
                    name = item.getString("name"),
                    phoneNumber = item.getString("phoneNumber")
                )
            }
        }.getOrDefault(emptyList())
    }

    fun add(context: Context, connection: TrustConnection): List<TrustConnection> {
        val existing = load(context)
        val normalizedNumber = connection.phoneNumber.filter(Char::isDigit)
        val updated = if (existing.any { it.phoneNumber.filter(Char::isDigit) == normalizedNumber }) {
            existing.map {
                if (it.phoneNumber.filter(Char::isDigit) == normalizedNumber) connection else it
            }
        } else {
            existing + connection
        }
        val jsonArray = JSONArray()
        updated.forEach { savedConnection ->
            jsonArray.put(
                JSONObject()
                    .put("name", savedConnection.name)
                    .put("phoneNumber", savedConnection.phoneNumber)
            )
        }
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
            .edit()
            .putString(connectionsKey, jsonArray.toString())
            .apply()
        return updated
    }
}
package com.microbond.app.core.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared in-memory registry of groups discovered from device contacts or dashboard summaries.
 * Allows ThreadScreen and its widgets to bind to real API group metadata rather than sample fallbacks.
 */
object DiscoveredGroupsRepository {
    private val _groups = MutableStateFlow<List<DiscoverableContactGroup>>(emptyList())
    val groups: StateFlow<List<DiscoverableContactGroup>> = _groups.asStateFlow()

    fun update(newGroups: List<DiscoverableContactGroup>) {
        if (newGroups.isNotEmpty()) {
            val existing = _groups.value.associateBy { it.name.trim().lowercase() }.toMutableMap()
            newGroups.forEach { g ->
                existing[g.name.trim().lowercase()] = g
            }
            _groups.value = existing.values.toList()
        }
    }

    fun findByName(name: String): DiscoverableContactGroup? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null
        return _groups.value.firstOrNull { it.name.trim().equals(trimmed, ignoreCase = true) }
    }

    fun findById(groupId: String): DiscoverableContactGroup? {
        if (groupId.isBlank()) return null
        return _groups.value.firstOrNull { it.id == groupId || it.groupId == groupId }
    }
}

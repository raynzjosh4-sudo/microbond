package com.microbond.app.core.components.threadbubbles.threads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

class ThreadSelectionState {
    private val _selectedIds = mutableStateListOf<Int>()
    val selectedIds: List<Int> get() = _selectedIds
    
    val isSelectionMode: Boolean
        get() = _selectedIds.isNotEmpty()
        
    val selectedCount: Int
        get() = _selectedIds.size

    fun toggleSelection(id: Int) {
        if (_selectedIds.contains(id)) {
            _selectedIds.remove(id)
        } else {
            _selectedIds.add(id)
        }
    }

    fun isSelected(id: Int): Boolean {
        return _selectedIds.contains(id)
    }

    fun clearSelection() {
        _selectedIds.clear()
    }
}

@Composable
fun rememberThreadSelectionState(): ThreadSelectionState {
    return remember { ThreadSelectionState() }
}

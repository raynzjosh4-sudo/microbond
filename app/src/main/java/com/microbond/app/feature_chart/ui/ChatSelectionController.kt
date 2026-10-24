package com.microbond.app.feature_chart.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.dummydata.SampleGroup
import com.microbond.app.feature_groups.data.UserGroupsRepository

@Stable
class ChatSelectionController(initialChats: List<SampleGroup> = emptyList()) {
    var chats by mutableStateOf(initialChats)
         private set

    var selectedChatNames by mutableStateOf(emptySet<String>())
        private set

    private var pinnedChatNames by mutableStateOf(emptySet<String>())
    private var mutedChatNames by mutableStateOf(emptySet<String>())
    private var lockedChatNames by mutableStateOf(emptySet<String>())
    private var blockedChatNames by mutableStateOf(emptySet<String>())

    val hasSelection: Boolean
        get() = selectedChatNames.isNotEmpty()

    val selectedCount: Int
        get() = selectedChatNames.size

    val areSelectedPinned: Boolean
        get() = hasSelection && pinnedChatNames.containsAll(selectedChatNames)

    val areSelectedMuted: Boolean
        get() = hasSelection && mutedChatNames.containsAll(selectedChatNames)

    fun isSelected(chatName: String): Boolean = chatName in selectedChatNames

    fun isPinned(chatName: String): Boolean = chatName in pinnedChatNames

    fun select(chatName: String) {
        selectedChatNames = selectedChatNames + chatName
    }

    fun toggleSelection(chatName: String) {
        selectedChatNames = if (chatName in selectedChatNames) {
            selectedChatNames - chatName
        } else {
            selectedChatNames + chatName
        }
    }

    fun clearSelection() {
        selectedChatNames = emptySet()
    }

    fun togglePinned() {
        pinnedChatNames = toggleNames(pinnedChatNames)
    }

    fun toggleMuted() {
        mutedChatNames = toggleNames(mutedChatNames)
    }

    fun markAllAsRead() {
        chats = chats.map { it.copy(unreadCount = 0) }
    }

    fun deleteSelected() {
        chats = chats.filterNot { it.name in selectedChatNames }
        clearSelection()
    }

    fun archiveSelected(onMenuAction: (String) -> Unit) {
        chats = chats.filterNot { it.name in selectedChatNames }
        clearSelection()
        onMenuAction("Archive")
    }

    fun selectionMenuActions(
        visibleChatNames: Set<String>,
        onMenuAction: (String) -> Unit
    ): List<MicrobondMenuAction> {
        val selectedGroups = chats.filter { it.name in selectedChatNames }
        val allSelectedUnread = selectedGroups.isNotEmpty() && selectedGroups.all { it.unreadCount > 0 }
        val allSelectedLocked = selectedChatNames.isNotEmpty() && lockedChatNames.containsAll(selectedChatNames)
        val allSelectedBlocked = selectedGroups.filterNot { it.isGroup }
            .let { it.isNotEmpty() && blockedChatNames.containsAll(it.map(SampleGroup::name)) }

        return buildList {
            add(
                MicrobondMenuAction(
                    title = if (allSelectedUnread) "Mark as read" else "Mark as unread",
                    onClick = {
                        chats = chats.map { group ->
                            if (group.name in selectedChatNames) {
                                group.copy(unreadCount = if (allSelectedUnread) 0 else 1)
                            } else group
                        }
                        if (allSelectedUnread) {
                            selectedGroups.forEach { grp ->
                                UserGroupsRepository.markGroupAsRead(grp.name, grp.id)
                            }
                        }
                    }
                )
            )
            add(
                MicrobondMenuAction(
                    title = if (selectedChatNames.containsAll(visibleChatNames)) "Deselect all" else "Select all",
                    onClick = {
                        selectedChatNames = if (selectedChatNames.containsAll(visibleChatNames)) {
                            selectedChatNames - visibleChatNames
                        } else {
                            selectedChatNames + visibleChatNames
                        }
                    }
                )
            )
            add(MicrobondMenuAction("Add chat shortcut", { onMenuAction("Add chat shortcut") }))

            if (selectedGroups.size == 1) {
                val group = selectedGroups.single()
                val action = if (group.isGroup) "View group info" else "View contact"
                add(MicrobondMenuAction(action, { onMenuAction(action) }))
            }

            if (selectedGroups.any { it.isGroup }) {
                add(
                    MicrobondMenuAction("Exit group", {
                        val selectedGroupNames = selectedGroups.filter { it.isGroup }.map { it.name }.toSet()
                        chats = chats.filterNot { it.name in selectedGroupNames }
                        selectedChatNames = selectedChatNames - selectedGroupNames
                        onMenuAction("Exit group")
                    })
                )
            }

            if (selectedGroups.any { !it.isGroup }) {
                add(
                    MicrobondMenuAction(
                        title = if (allSelectedBlocked) "Unblock" else "Block",
                        onClick = {
                            val personalChatNames = selectedGroups.filterNot { it.isGroup }.map { it.name }.toSet()
                            blockedChatNames = if (allSelectedBlocked) {
                                blockedChatNames - personalChatNames
                            } else {
                                blockedChatNames + personalChatNames
                            }
                            onMenuAction(if (allSelectedBlocked) "Unblock" else "Block")
                        }
                    )
                )
            }

            add(
                MicrobondMenuAction(
                    title = if (allSelectedLocked) "Unlock chat" else "Lock chat",
                    onClick = {
                        lockedChatNames = if (allSelectedLocked) {
                            lockedChatNames - selectedChatNames
                        } else {
                            lockedChatNames + selectedChatNames
                        }
                    }
                )
            )
        }
    }

    fun updateChats(newChats: List<SampleGroup>) {
        chats = newChats
    }

    fun addChat(group: SampleGroup) {
        if (chats.none { it.name == group.name }) {
            chats = listOf(group) + chats
        }
    }

    private fun toggleNames(currentNames: Set<String>): Set<String> =
        if (currentNames.containsAll(selectedChatNames)) {
            currentNames - selectedChatNames
        } else {
            currentNames + selectedChatNames
        }
}

@Composable
fun rememberChatSelectionController(
    initialChats: List<SampleGroup> = emptyList()
): ChatSelectionController =
    remember { ChatSelectionController(initialChats) }
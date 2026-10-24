package com.microbond.app.screens.threadscreen.threadscreenwidgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.microbond.app.core.components.threadbubbles.threads.ThreadSelectionState
import com.microbond.app.core.components.threadbubbles.threads.ThreadSelectionTopBar
import com.microbond.app.core.components.threadbubbles.threads.ThreadTopBar
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction

@Composable
fun ThreadScreenTopBar(
    entityName: String,
    selectionState: ThreadSelectionState,
    memberAvatar: Painter?,
    groupIconUri: String? = null,
    groupSubtitle: String = "Savings Group",
    onNavigateBack: () -> Unit,
    onReplyFromSelection: (Int?) -> Unit,
    onShowGroupSettings: () -> Unit,
) {
    if (selectionState.isSelectionMode) {
        ThreadSelectionTopBar(
            selectedCount = selectionState.selectedCount,
            onBackClick = { selectionState.clearSelection() },
            onReplyClick = {
                onReplyFromSelection(selectionState.selectedIds.firstOrNull())
                selectionState.clearSelection()
            },
            onStarClick = { /* Handle star */ },
            onDeleteClick = { /* Handle delete */ },
            onCopyClick = { /* Handle copy */ },
            onForwardClick = { /* Handle forward */ },
            onMoreClick = { /* Handle options */ }
        )
    } else {
        ThreadTopBar(
            title = entityName,
            subtitle = groupSubtitle,
            onBackClick = onNavigateBack,
            onMoreClick = { /* Handle options */ },
            onTitleClick = onShowGroupSettings,
            avatarPainter = memberAvatar,
            avatarUri = groupIconUri,
            menuActions = listOf(
                MicrobondMenuAction("Deposit status", onClick = { }),
                MicrobondMenuAction("Save report", onClick = { }),
                MicrobondMenuAction("Location", onClick = { }),
                MicrobondMenuAction("Document", onClick = { }),
                MicrobondMenuAction("Rules", onClick = { }),
                MicrobondMenuAction("Statistics", onClick = { }),
                MicrobondMenuAction("Group setting", onClick = onShowGroupSettings)
            )
        )
    }
}

package com.microbond.app.core.components.groupintrocard.rotating

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.microbond.app.core.components.groupintrocard.GroupIntroCard

/**
 * Convenience composite widget binding the WhatsApp-accurate GroupIntroCard shell
 * with the Rotating Group configuration derived directly from the API data.
 */
@Composable
fun RotatingGroupIntroCard(
    state: RotatingGroupPreviewUiState,
    modifier: Modifier = Modifier,
    iconUri: String? = null,
    onOpenDetailsSheet: () -> Unit = {}
) {
    GroupIntroCard(
        name = state.name,
        memberCountText = state.cardMemberText,
        createdDateText = state.createdDateText,
        description = state.description,
        iconUri = iconUri ?: state.avatarUrl,
        joinButtonText = "Join Group",
        isJoinEnabled = true,
        onJoinClick = onOpenDetailsSheet,
        modifier = modifier
    )
}

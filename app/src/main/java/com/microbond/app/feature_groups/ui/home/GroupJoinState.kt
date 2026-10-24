package com.microbond.app.feature_groups.ui.home

import com.microbond.app.core.network.DiscoverableContactGroup

/**
 * Lifecycle states of an individual group card in the discovery feed.
 */
sealed interface JoinState {
    object IDLE : JoinState
    object LOADING : JoinState
    object PENDING_APPROVAL : JoinState
    data class PENDING_GUARANTOR(val guarantorName: String) : JoinState
    object JOINED : JoinState
}

/**
 * UI representation of a discoverable group card with its isolated join state.
 */
data class DiscoveredGroupUiItem(
    val group: DiscoverableContactGroup,
    val joinState: JoinState = JoinState.IDLE
)

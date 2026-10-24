package com.microbond.app.authentication_features.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * AppLockLifecycleObserver: Monitors application foreground/background state.
 * If the user places the app in the background for more than idleTimeoutMs (default: 60s),
 * it triggers onLockRequired to overlay SessionUnlockPinScreen.
 */
class AppLockLifecycleObserver(
    private val idleTimeoutMs: Long = 60_000L,
    private val onLockRequired: () -> Unit
) : DefaultLifecycleObserver {

    private var backgroundTimestamp: Long = 0L

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        backgroundTimestamp = System.currentTimeMillis()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (backgroundTimestamp > 0L) {
            val elapsed = System.currentTimeMillis() - backgroundTimestamp
            if (elapsed >= idleTimeoutMs) {
                onLockRequired()
            }
            backgroundTimestamp = 0L
        }
    }
}

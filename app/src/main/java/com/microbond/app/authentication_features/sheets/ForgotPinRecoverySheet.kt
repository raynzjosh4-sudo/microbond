package com.microbond.app.authentication_features.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.microbond.app.authentication_features.screens.ForgotPinRecoveryScreen

/**
 * Notice: Microbond requires all recovery flows to be full-screen workflows.
 * Please use [ForgotPinRecoveryScreen] directly.
 */
@Deprecated(
    message = "Recovery flows in Microbond must be full screens. Use ForgotPinRecoveryScreen instead.",
    replaceWith = ReplaceWith(
        "ForgotPinRecoveryScreen(onNavigateBack = onDismissRequest, onPinResetReady = onPinResetReady, modifier = modifier)",
        "com.microbond.app.authentication_features.screens.ForgotPinRecoveryScreen"
    )
)
@Composable
fun ForgotPinRecoverySheet(
    onDismissRequest: () -> Unit,
    onPinResetReady: (recoveryToken: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    ForgotPinRecoveryScreen(
        onNavigateBack = onDismissRequest,
        onPinResetReady = onPinResetReady,
        modifier = modifier
    )
}

package com.microbond.app.mainactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.data_classes_and_enums.WallpaperManager
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondTheme
import com.microbond.app.feature_groups.data.UserGroupsRepository

/**
 * Main application entry activity.
 * Initializes core services, theme, and delegates UI orchestration to MicrobondRootNavHost.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WallpaperManager.load(this)
        AuthSessionManager.load(this)
        UserGroupsRepository.load(this)

        // Inject dynamic JWT access token into the Retrofit client
        NetworkClient.tokenProvider = { AuthSessionManager.accessToken.value }

        setContent {
            MicrobondTheme {
                MicrobondRootNavHost(
                    onFinish = { finish() }
                )
            }
        }
    }
}

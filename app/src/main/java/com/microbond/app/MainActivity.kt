package com.microbond.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.microbond.app.core.data_classes_and_enums.WallpaperManager
import com.microbond.app.core.ui.theme.MicrobondTheme
import com.microbond.app.main.MicrobondApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WallpaperManager.load(this)
        setContent {
            MicrobondTheme {
                MicrobondApp()
            }
        }
    }
}

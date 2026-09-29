package com.microbond.app.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.microbond.app.R
import com.microbond.app.core.ui.theme.MicrobondDarkBackground

@Composable
fun MicrobondLaunchSplash() {
    val isDarkTheme = isSystemInDarkTheme()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) MicrobondDarkBackground else Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = if (isDarkTheme) R.drawable.splash_dark else R.drawable.splash_light
            ),
            contentDescription = "Microbond",
            modifier = Modifier.size(168.dp),
            contentScale = ContentScale.Fit
        )
    }
}
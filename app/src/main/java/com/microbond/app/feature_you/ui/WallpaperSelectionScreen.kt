package com.microbond.app.feature_you.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.R
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.data_classes_and_enums.WallpaperManager
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@Composable
fun WallpaperSelectionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val selectedWallpaper by WallpaperManager.selectedWallpaper.collectAsState()

    val wallpapers = listOf(
        null, // Default doodle
        R.drawable.wp_4698,
        R.drawable.wp_784,
        R.drawable.wp_oqecwt0,
        R.drawable.wp_curls,
        R.drawable.wp_diagonal_squares,
        R.drawable.wp_double_bubble_dark,
        R.drawable.wp_fancy_cushion,
        R.drawable.wp_wheat
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Wallpapers",
                showBackButton = true,
                onBackClick = onBack,
                showMoreIcon = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MicrobondDarkBackground)
                .padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(wallpapers) { resId ->
                    val isSelected = selectedWallpaper == resId
                    Box(
                        modifier = Modifier
                            .aspectRatio(9f / 16f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (resId == null) Color(0xFF1B2B33) else Color.Transparent)
                            .clickable {
                                WallpaperManager.setWallpaper(context, resId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (resId != null) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Default", color = MicrobondTextPrimary, fontSize = 14.sp)
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MicrobondActiveGreen.copy(alpha = 0.3f))
                            )
                        }
                    }
                }
            }
        }
    }
}

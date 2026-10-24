package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.R
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondGalleryTopBarDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Mocks the WhatsApp "Recents" gallery picker grid.
 */
@Composable
fun GalleryPickerScreen(
    onBack: () -> Unit,
    onImageSelected: (String) -> Unit
) {
    // Mock image resources available in the project
    val images = listOf(
        R.drawable.img_1c491759b92447ed62d8bcef1b763040,
        R.drawable.img_808185095673193834,
        R.drawable.img_896005288366686515,
        R.drawable.img_964896288911385882,
        R.drawable.img_965037026417633943,
        R.drawable.img_download,
        R.drawable.img_fidelis,
        R.drawable.wp_oqecwt0,
        R.drawable.img_1086634216382568373
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = { GalleryPickerTopBar(onBack = onBack) }
    ) { insets ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(2.dp)
        ) {
            items(images) { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { onImageSelected(resId.toString()) }
                )
            }
        }
    }
}

@Composable
private fun GalleryPickerTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MicrobondGalleryTopBarDark), // Darker top bar for gallery
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = MicrobondTextPrimary
            )
        }
        
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recents",
                color = MicrobondTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Folders",
                tint = MicrobondTextPrimary
            )
        }
    }
}

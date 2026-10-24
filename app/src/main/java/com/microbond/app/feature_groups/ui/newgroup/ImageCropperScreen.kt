package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropRotate
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
import com.microbond.app.core.ui.theme.MicrobondActiveGreen

/**
 * Mocks the WhatsApp image cropper screen.
 */
@Composable
fun ImageCropperScreen(
    imageUri: String,
    onCancel: () -> Unit,
    onDone: (String) -> Unit
) {
    val resId = imageUri.toIntOrNull() ?: 0

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Black,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color.Black)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cancel",
                    color = MicrobondActiveGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onCancel() }
                )
                
                IconButton(onClick = { /* rotate */ }) {
                    Icon(
                        Icons.Default.CropRotate,
                        contentDescription = "Rotate",
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Done",
                    color = MicrobondActiveGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onDone(imageUri) }
                )
            }
        }
    ) { insets ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            contentAlignment = Alignment.Center
        ) {
            if (resId != 0) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Crop overlay mock
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f)
                        .border(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    // Grid lines
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.5f)))
                        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.5f)))
                    }
                    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Spacer(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color.White.copy(alpha = 0.5f)))
                        Spacer(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color.White.copy(alpha = 0.5f)))
                    }
                    // Corners
                    Box(Modifier.align(Alignment.TopStart).size(24.dp).border(2.dp, Color.White))
                    Box(Modifier.align(Alignment.TopEnd).size(24.dp).border(2.dp, Color.White))
                    Box(Modifier.align(Alignment.BottomStart).size(24.dp).border(2.dp, Color.White))
                    Box(Modifier.align(Alignment.BottomEnd).size(24.dp).border(2.dp, Color.White))
                }
            }
        }
    }
}

package com.microbond.app.core.components.groupIconImage.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondGroupColorBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MicrobondGroupImageAvatar(
    modifier: Modifier = Modifier,
    @DrawableRes groupImageRes: Int? = null,
    imageUri: String? = null,
    avatarSize: Dp = 56.dp,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    iconTint: Color = Color(0xFF8696A0),
    backgroundColor: Color = Color(0x4A0E651F),
    showinSetttings: Boolean = false
) {
    val clickableModifier = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = onLongClick
        )
    } else {
        Modifier
    }

    val asyncPainter = if (!imageUri.isNullOrBlank()) {
        com.microbond.app.core.components.avatar.rememberAsyncImagePainter(imageUri)
    } else null

    Box(
        modifier = modifier
            .size(avatarSize)
            .then(clickableModifier),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MicrobondDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            if (groupImageRes != null) {
                Image(
                    painter = painterResource(id = groupImageRes),
                    contentDescription = "Group picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (asyncPainter != null) {
                Image(
                    painter = asyncPainter,
                    contentDescription = "Group picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (showinSetttings) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(avatarSize / 2)
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.Group,
                    contentDescription = "Default Group Icon",
                    tint = iconTint,
                    modifier = Modifier.size(avatarSize / 2)
                )
            }
        }

        // Camera badge when in settings
        if (showinSetttings) {
            val badgeSize = (avatarSize * 0.28f).coerceIn(28.dp, 44.dp)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(Color(0xFF00A884)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change photo",
                    tint = Color.White,
                    modifier = Modifier.size(badgeSize * 0.55f)
                )
            }
        }
    }
}

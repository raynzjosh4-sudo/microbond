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
import androidx.compose.material.icons.filled.Group
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
import com.microbond.app.core.ui.theme.MicrobondDarkBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MicrobondGroupImageAvatar(
    modifier: Modifier = Modifier,
    @DrawableRes groupImageRes: Int? = null,
    avatarSize: Dp = 56.dp,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    iconTint: Color = Color(0xFF8696A0)
) {
    val clickableModifier = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = onLongClick
        )
    } else {
        Modifier
    }

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
            } else {
                Icon(
                    imageVector = Icons.Filled.Group,
                    contentDescription = "Default Group Icon",
                    tint = iconTint,
                    modifier = Modifier.size(avatarSize / 2)
                )
            }
        }
    }
}

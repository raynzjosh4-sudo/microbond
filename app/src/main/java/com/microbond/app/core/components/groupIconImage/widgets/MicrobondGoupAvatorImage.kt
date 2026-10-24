package com.microbond.app.core.components.groupIconImage

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.microbond.app.core.components.groupIconImage.widgets.MicrobondGroupImageAvatar
import com.microbond.app.core.data_classes_and_enums.MemberSaveStatus
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MicrobondGroupAvatar(
    statuses: List<MemberSaveStatus> = emptyList(),
    modifier: Modifier = Modifier,
    showStatusRing: Boolean = true,
    @DrawableRes groupImageRes: Int? = null,
    imageUri: String? = null,
    avatarSize: Dp = 56.dp,
    ringThickness: Dp = 3.5.dp,
    ringGapAngle: Float = 6f,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    val hasActiveRing = showStatusRing && statuses.isNotEmpty() && statuses.any { it != MemberSaveStatus.PENDING }
    if (!hasActiveRing) {
        MicrobondGroupImageAvatar(
            modifier = modifier,
            groupImageRes = groupImageRes,
            imageUri = imageUri,
            avatarSize = avatarSize,
            onClick = onClick,
            onLongClick = onLongClick
        )
        return
    }

    val totalMembers = statuses.size.coerceAtLeast(1)
    val sweepAnglePerMember = (360f / totalMembers) - ringGapAngle

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
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidthPx = ringThickness.toPx()
            val arcSize = size.width - strokeWidthPx

            statuses.forEachIndexed { index, status ->
                val startAngle = -90f + (index * (360f / totalMembers)) + (ringGapAngle / 2f)

                val segmentColor = when (status) {
                    MemberSaveStatus.SAVED -> MicrobondActiveGreen
                    MemberSaveStatus.MISSED -> Color(0xFFE53935)
                    MemberSaveStatus.PENDING -> MicrobondDividerDark
                }
                drawArc(
                    color = segmentColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAnglePerMember,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                    size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                    topLeft = androidx.compose.ui.geometry.Offset(strokeWidthPx / 2f, strokeWidthPx / 2f)
                )
            }
        }

        val asyncPainter = if (!imageUri.isNullOrBlank()) {
            com.microbond.app.core.components.avatar.rememberAsyncImagePainter(imageUri)
        } else null

        Box(
            modifier = Modifier
                .padding(ringThickness + 2.dp)
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
            } else {
                Icon(
                    imageVector = Icons.Filled.Group,
                    contentDescription = "Default Group Icon",
                    tint = Color(0xFF8696A0),
                    modifier = Modifier.size(avatarSize / 2)
                )
            }
        }
    }
}



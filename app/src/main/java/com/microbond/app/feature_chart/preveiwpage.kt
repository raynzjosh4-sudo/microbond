package com.microbond.app.feature_chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.groupIconImage.MicrobondGroupAvatar
import com.microbond.app.core.data_classes_and_enums.MemberSaveStatus
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

private data class ExampleAvatar(
    val name: String,
    val statuses: List<MemberSaveStatus>
)

enum class PreviewMode {
    STATUS,
    IMAGE
}

@Composable
fun PreviewPage(
    modifier: Modifier = Modifier,
    initialMode: PreviewMode = PreviewMode.STATUS,
    onBack: () -> Unit = {}
) {
    var currentMode by remember { mutableStateOf(initialMode) }

    val avatars = listOf(
        ExampleAvatar("Priscilla", listOf(
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED
        )),
        ExampleAvatar("Joey", listOf(
            MemberSaveStatus.SAVED,
            MemberSaveStatus.PENDING,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED
        )),
        ExampleAvatar("Asha", listOf(
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED,
            MemberSaveStatus.PENDING
        )),
        ExampleAvatar("Nia", listOf(
            MemberSaveStatus.PENDING,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED
        ))
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0B1013)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentMode) {
                PreviewMode.STATUS -> StatusPreviewContent(avatars = avatars, onStoryClick = { currentMode = PreviewMode.STATUS }, onImageClick = { currentMode = PreviewMode.IMAGE })
                PreviewMode.IMAGE -> ImagePreviewContent(avatars = avatars, onStoryClick = { currentMode = PreviewMode.STATUS }, onImageClick = { currentMode = PreviewMode.IMAGE })
            }
        }
    }
}

@Composable
private fun StatusPreviewContent(
    avatars: List<ExampleAvatar>,
    onStoryClick: () -> Unit,
    onImageClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050909))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onStoryClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB08058)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Priscilla",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "21h • Reshared",
                            color = Color(0xFFBFC8CF),
                            fontSize = 12.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 0.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2E4052),
                                Color(0xFF1A2431),
                                Color(0xFF0D1417)
                            )
                        ),
                        size = size
                    )

                    val baseY = size.height * 0.68f
                    val buildingWidth = size.width / 8f
                    for (i in 0..7) {
                        val x = i * buildingWidth
                        val y = baseY - (i % 3) * 18f
                        drawRect(
                            color = Color(0xFF1A1F26),
                            topLeft = Offset(x + 8f, y),
                            size = Size(buildingWidth - 16f, size.height - y)
                        )
                    }

                    for (i in 0..15) {
                        val rectX = (i * 26) % size.width
                        val rectY = 80f + (i * 17 % 220)
                        drawRect(
                            color = Color(0xFFEA9E2F),
                            topLeft = Offset(rectX + 2f, rectY),
                            size = Size(12f, 14f)
                        )
                    }

                    drawRect(
                        color = Color(0xFF212C2E),
                        topLeft = Offset(0f, size.height * 0.78f),
                        size = Size(size.width, size.height * 0.22f)
                    )

                    drawPath(
                        path = Path().apply {
                            moveTo(0f, size.height * 0.86f)
                            lineTo(size.width * 0.2f, size.height * 0.74f)
                            lineTo(size.width * 0.35f, size.height * 0.82f)
                            lineTo(size.width * 0.58f, size.height * 0.72f)
                            lineTo(size.width * 0.82f, size.height * 0.84f)
                            lineTo(size.width, size.height * 0.8f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        },
                        color = Color(0xFF0F1A1D)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0E1114))
                    .padding(top = 12.dp, start = 14.dp, end = 14.dp, bottom = 14.dp)
            ) {
                Column {
                    Text(
                        text = "I feel like meeting him oOoOO shit",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 18.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReactionChip(label = "👍", tint = Color(0xFFB4D0FF))
                        ReactionChip(label = "😍", tint = Color(0xFFF5C7D8))
                        ReactionChip(label = "😂", tint = Color(0xFFF1D27A))
                        ReactionChip(label = "💬", tint = Color(0xFFB8B8B8))
                        ReactionChip(label = "♥", tint = Color(0xFFFF6B8A))
                    }
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(avatars) { avatar ->
                    MicrobondGroupAvatar(
                        statuses = avatar.statuses,
                        avatarSize = 54.dp,
                        ringThickness = 3.dp,
                        showStatusRing = true,
                        onClick = onStoryClick,
                        onLongClick = { onImageClick() }
                    )
                }
            }

            BottomStoryNavBar()
        }
    }
}

@Composable
private fun ReactionChip(label: String, tint: Color) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xFF191E22))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = tint
        )
    }
}

@Composable
private fun ImagePreviewContent(
    avatars: List<ExampleAvatar>,
    onStoryClick: () -> Unit,
    onImageClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4AA96C))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onStoryClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "You",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFFEAF0EA)),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(28.dp))

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3A6EA5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "◔",
                            color = Color.White,
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "raynzjosh",
                        color = Color(0xFF121212),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "WhatsApp contact",
                        color = Color(0xFF596269),
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    QRCodeBox()

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Scan this code using the WhatsApp camera to get my number",
                        color = Color(0xFF1E1E1E),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )

                    Text(
                        text = "Add me as a contact on WhatsApp.",
                        color = Color(0xFF1E1E1E),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Text(
                        text = "https://wa.me/qr/TFU2RQHHT5EDK1",
                        color = Color(0xFF1E1E1E),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallActionBubble(text = "♥", tint = Color(0xFFFF5F77))
                Spacer(modifier = Modifier.width(16.dp))
                SmallActionBubble(text = "😊", tint = Color(0xFFFFC62E))
                Spacer(modifier = Modifier.width(16.dp))
                SmallActionBubble(text = "✉", tint = Color(0xFF6B7A8F))
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(avatars) { avatar ->
                    MicrobondGroupAvatar(
                        statuses = avatar.statuses,
                        avatarSize = 54.dp,
                        ringThickness = 3.dp,
                        showStatusRing = true,
                        onClick = onStoryClick,
                        onLongClick = { onImageClick() }
                    )
                }
            }

            BottomStoryNavBar()
        }
    }
}

@Composable
private fun BottomStoryNavBar() {
    val items = listOf(
        "Messages" to Icons.Default.ChatBubbleOutline,
        "Updates" to Icons.Default.BarChart,
        "Services" to Icons.Default.Build,
        "Notify" to Icons.Default.Notifications,
        "You" to Icons.Default.AccountCircle
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0E1114))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (label, icon) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (label == "Messages") Color.White else Color(0xFFB9C2CA),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    color = if (label == "Messages") Color.White else Color(0xFFB9C2CA),
                    fontSize = 10.sp,
                    fontWeight = if (label == "Messages") FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun SmallActionBubble(text: String, tint: Color) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color(0xFF111712)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = tint, fontSize = 20.sp)
    }
}

@Composable
private fun QRCodeBox() {
    Box(
        modifier = Modifier
            .size(width = 220.dp, height = 220.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cell = size.width / 9f

            for (row in 0..8) {
                for (col in 0..8) {
                    val shouldFill = when {
                        row in listOf(0, 1, 2, 6, 7, 8) && col in listOf(0, 1, 2, 6, 7, 8) -> true
                        row in listOf(0, 1, 2, 6, 7, 8) && col in listOf(3, 4, 5) -> false
                        row in listOf(3, 4, 5) && col in listOf(0, 1, 2, 6, 7, 8) -> false
                        row in listOf(3, 4, 5) && col in listOf(3, 4, 5) -> true
                        else -> false
                    }

                    if (shouldFill) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(col * cell, row * cell),
                            size = Size(cell, cell)
                        )
                    }
                }
            }

            drawRoundRect(
                color = Color(0xFFB8C4D1),
                topLeft = Offset(60f, 60f),
                size = Size(80f, 80f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f),
                style = Stroke(width = 4f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101214)
@Composable
private fun PreviewPagePreview() {
    PreviewPage()
}

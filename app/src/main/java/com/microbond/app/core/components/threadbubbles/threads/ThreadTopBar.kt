package com.microbond.app.core.components.threadbubbles.threads

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.components.groupIconImage.widgets.MicrobondGroupImageAvatar
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction

@Composable
fun ThreadTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onTitleClick: () -> Unit = {},
    avatarPainter: Painter? = null,
    avatarUri: String? = null,
    menuActions: List<MicrobondMenuAction> = emptyList(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MicrobondDarkBackground)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate Back",
                tint = MicrobondTextPrimary
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onTitleClick)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2329)),
                contentAlignment = Alignment.Center
            ) {
                MicrobondGroupImageAvatar(
                    avatarSize = 50.dp,
                    imageUri = avatarUri
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }

        MicrobondOverflowMenu(
            actions = menuActions,
            onClickWhenEmpty = onMoreClick,
            modifier = Modifier.size(40.dp)
        )
    }
}

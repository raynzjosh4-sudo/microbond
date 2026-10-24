package com.microbond.app.core.components

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicrobondTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    showCameraIcon: Boolean = false,
    showSearchIcon: Boolean = false,
    showMoreIcon: Boolean = true,
    showSearchBarWigdet: Boolean = false,
    showFilterChip: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onSearchIconClick: () -> Unit = {},
    onSearchBarClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    menuActions: List<MicrobondMenuAction> = emptyList(),
    appIconRes: Int? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
            .statusBarsPadding()
            .padding(top = 4.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (showBackButton) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = MicrobondTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    if (title.isNotBlank() || appIconRes != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                if (appIconRes != null) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = appIconRes),
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        fontSize = if (showBackButton) 20.sp else 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MicrobondTextPrimary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showCameraIcon) {
                    IconButton(
                        onClick = onCameraClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = "Camera Scanner",
                            tint = MicrobondTextPrimary
                        )
                    }
                }

                if (showSearchIcon) {
                    IconButton(
                        onClick = onSearchIconClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "search",
                            tint = MicrobondTextPrimary
                        )
                    }
                }

                if (showMoreIcon) {
                    MicrobondOverflowMenu(
                        actions = menuActions,
                        onClickWhenEmpty = onMoreClick,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        if (showSearchBarWigdet) {
            MicrobondSearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange
            )
        }
    }
}
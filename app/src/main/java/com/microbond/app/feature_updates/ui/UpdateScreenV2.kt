package com.microbond.app.feature_updates.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.groupIconImage.groupListTile.MicrobondGroupListTile
import com.microbond.app.core.components.floatingbutton.MicrobondTextUpdateFab
import com.microbond.app.core.components.updates.AddStatusCard
import com.microbond.app.core.components.updates.FindGroupsSection
import com.microbond.app.core.components.updates.GroupActionOutlinedButton
import com.microbond.app.core.components.updates.GroupIconAction
import com.microbond.app.core.components.updates.GroupsHeader
import com.microbond.app.core.components.updates.UserStatusCard
import com.microbond.app.core.data_classes_and_enums.ChartPreview
import com.microbond.app.core.data_classes_and_enums.GroupSavingsType
import com.microbond.app.core.dummydata.sampleSavingsGroups
import com.microbond.app.core.dummydata.sampleStatuses
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

data class StatusItem(
    val id: Int,
    val name: String,
    val bgColor: Color
)

@Composable
fun UpdateScreenV2(
    onNavigateToCamera: () -> Unit = {},
    onNavigateToTextUpdate: () -> Unit = {}
) {
    var groupsExpanded by remember { mutableStateOf(true) }

    val statusList = listOf(
        StatusItem(1, "Muzeyi K", Color(0xFF203E37)),
        StatusItem(2, "Shammy", Color(0xFF3A2F24)),
        StatusItem(3, "Julius C...", Color(0xFF2B3D4A))
    )

    val groupCards = sampleSavingsGroups.map { group ->
        ChartPreview(
            name = group.name,
            initials = group.initials,
            message = group.latestMessage,
            time = group.time,
            unreadCount = group.unreadCount,
            isOutgoingMessage = group.isOutgoingMessage,
            isFavorite = group.isFavorite,
            isGroup = group.isGroup,
            avatarColor = group.avatarColor,
            messageStatus = group.messageStatus
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            com.microbond.app.core.components.MicrobondTopAppBar(
                title = "Updates",
                showCameraIcon = false,
                showSearchIcon = true,
                showMoreIcon = true,
                onCameraClick = onNavigateToCamera,
                onSearchIconClick = { },
                onMoreClick = { },
                menuActions = listOf(
                    com.microbond.app.core.components.menu.MicrobondMenuAction("Refresh", { }),
                    com.microbond.app.core.components.menu.MicrobondMenuAction("Settings", { })
                )
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MicrobondTextUpdateFab(onClick = onNavigateToTextUpdate)

                Spacer(modifier = Modifier.height(4.dp))

                FloatingActionButton(
                    onClick = onNavigateToCamera,
                    containerColor = MicrobondActiveGreen,
                    contentColor = Color(0xFF111B21),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera Update")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MicrobondDarkBackground)
                .padding(paddingValues)
        ) {
            item {
                Text(
                    text = "Status",
                    color = MicrobondTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        AddStatusCard(onClick = onNavigateToCamera)
                    }
                    items(statusList) { status ->
                        UserStatusCard(
                            name = status.name,
                            bgColor = status.bgColor,
                            onClick = { }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                GroupsHeader(onExploreClick = { })
            }

            item {
                FindGroupsSection(
                    expanded = groupsExpanded,
                    onToggle = { groupsExpanded = !groupsExpanded }
                )
            }

            item {
                AnimatedVisibility(
                    visible = groupsExpanded,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(durationMillis = 220)
                    ) + fadeIn(animationSpec = tween(durationMillis = 140)),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(durationMillis = 180)
                    ) + fadeOut(animationSpec = tween(durationMillis = 100))
                ) {
                    Column {
                        groupCards.take(3).forEach { chart ->
                            val group = sampleSavingsGroups.first { it.name == chart.name }
                            MicrobondGroupListTile(
                                chart = chart,
                                memberStatuses = sampleStatuses,
                                groupType = group.groupType,
                                showStatusRing = false,
                                messageType = if (group.isPublic) "Public group" else "Private group",
                                showBadge = false,
                                onClick = {},
                                onLongClick = null
                            )
                        }

                    }
                }
            }

            item {
                GroupActionOutlinedButton(
                    text = "Explore more groups",
                    icon = { GroupIconAction(modifier = Modifier.size(20.dp)) },
                    onClick = { }
                )
            }

            item {
                GroupActionOutlinedButton(
                    text = "Create group",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MicrobondActiveGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = { }
                )
            }

        }
    }
}

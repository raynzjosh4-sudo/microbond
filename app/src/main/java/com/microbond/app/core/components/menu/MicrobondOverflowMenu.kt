package com.microbond.app.core.components.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds






@Composable
fun MicrobondOverflowMenu(
    actions: List<MicrobondMenuAction>,
    modifier: Modifier = Modifier,
    onClickWhenEmpty: () -> Unit = {},
    contentDescription: String = "More options",
    triggerIcon: ImageVector = Icons.Outlined.MoreVert
) {
    var expanded by remember { mutableStateOf(false) }
    var popupVisible by remember { mutableStateOf(false) }
    var anchorHeight by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    LaunchedEffect(expanded) {
        if (!expanded) {
            delay(100.milliseconds)
            popupVisible = false
        }
    }

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            anchorHeight = coordinates.size.height
        }
    ) {
        IconButton(
            onClick = {
                if (actions.isEmpty()) {
                    onClickWhenEmpty()
                } else {
                    popupVisible = true
                    expanded = true
                }
            }
        ) {
            Icon(
                imageVector = triggerIcon,
                contentDescription = contentDescription,
                tint = Color.White
            )
        }

        if (popupVisible) {
            Popup(
                alignment = Alignment.TopEnd,
                offset = with(density) {
                    IntOffset(x = -12.dp.roundToPx(), y = anchorHeight + 4.dp.roundToPx())
                },
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = expanded)
            ) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(240, easing = FastOutSlowInEasing)
                    ) + slideInVertically(
                        initialOffsetY = { -it / 6 },
                        animationSpec = tween(260, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(180)),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(180, easing = FastOutSlowInEasing)
                    ) + slideOutVertically(
                        targetOffsetY = { -it / 8 },
                        animationSpec = tween(180, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(140))
                ) {
                    Surface(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .widthIn(min = 160.dp, max = 250.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MicrobondOverlayWidgetDark,
                        contentColor = MicrobondTextPrimary,
                        shadowElevation = 8.dp
                    ) {
                        Column {
                            actions.forEach { action ->
                                val itemColor = if (action.enabled) MicrobondTextPrimary else MicrobondInactiveGray
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 40.dp)
                                        .clickable(enabled = action.enabled) {
                                            expanded = false
                                            action.onClick()
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    action.icon?.let { icon ->
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = itemColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                    }
                                    Text(
                                        text = action.title,
                                        color = itemColor,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
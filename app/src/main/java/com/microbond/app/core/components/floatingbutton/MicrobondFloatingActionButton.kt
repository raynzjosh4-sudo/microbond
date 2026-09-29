package com.microbond.app.core.components.floatingbutton


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark


@Composable
fun MicrobondFloatingActionButton(
    onclick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector  = Icons.Filled.AddComment,
    contentDescription: String = "Start new saving group",
    secondaryIcon: ImageVector? = null,
    onsecondaryClick: (()-> Unit)? = null,
    secondarycontentDescription: String? = null
) {
    Column(
        modifier = modifier.padding(bottom = 8.dp, end = 8.dp),
        horizontalAlignment =Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (secondaryIcon  != null && onsecondaryClick != null){
            SmallFloatingActionButton(
                onClick = onsecondaryClick,
                shape = RoundedCornerShape(12.dp) ,
                containerColor = MicrobondDividerDark,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)

            ) {
                Icon(
                    imageVector = secondaryIcon,
                    contentDescription = secondarycontentDescription,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        FloatingActionButton(
            onClick = onclick,
            shape = RoundedCornerShape(16.dp),
            containerColor = MicrobondActiveGreen,
            contentColor = MicrobondDarkBackground,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp
            )
        ) { Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )}
    }
}
package com.microbond.app.core.components.floatingbutton

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@Composable
fun MicrobondTextUpdateFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF2B343B),
        contentColor = MicrobondTextPrimary,
        shape = CircleShape,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Text Update",
            modifier = Modifier.size(18.dp)
        )
    }
}
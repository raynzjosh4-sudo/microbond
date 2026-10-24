package com.microbond.app.feature_groups.ui.addmember.widgets

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Top action header containing the admin permission notice and the quick actions:
 * - "New contact"
 * - "Invite via link or QR code"
 */
@Composable
fun AddMemberActionHeader(
    onEditPermissionsClick: () -> Unit,
    onNewContactClick: () -> Unit,
    onInviteViaLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
    canInviteViaLink: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Admin permission notice banner
        val noticeText = buildAnnotatedString {
            withStyle(SpanStyle(color = MicrobondInactiveGray, fontSize = 13.5.sp)) {
                append("Only admins are able to add others to this group. ")
            }
            pushStringAnnotation(tag = "EDIT_PERMISSIONS", annotation = "edit")
            withStyle(
                SpanStyle(
                    color = MicrobondActiveGreen,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("Edit group permissions.")
            }
            pop()
        }

        Text(
            text = noticeText,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .clickable { onEditPermissionsClick() }
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 1. "New contact" row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNewContactClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MicrobondActiveGreen,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "New contact",
                    tint = Color.Black,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "New contact",
                color = MicrobondTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // 2. "Invite via link or QR code" row
        if (canInviteViaLink) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onInviteViaLinkClick() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MicrobondActiveGreen,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "Invite via link or QR code",
                        tint = Color.Black,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Invite via link or QR code",
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

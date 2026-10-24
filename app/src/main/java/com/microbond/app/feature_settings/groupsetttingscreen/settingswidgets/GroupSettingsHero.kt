package com.microbond.app.screens.groupsetttingscreen.settingswidgets


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.rememberAsyncImagePainter
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondSurfaceDark


/**
 * Computes the top-bar title and docking alpha from [scrollState].
 */
@Composable
fun rememberAvatarAlpha(scrollState: ScrollState): Float {
    val density = LocalDensity.current
    val startPx = with(density) { 70.dp.toPx() }
    val endPx   = with(density) { 150.dp.toPx() }
    val alpha by remember {
        derivedStateOf {
            ((scrollState.value - startPx) / (endPx - startPx)).coerceIn(0f, 1f)
        }
    }
    return alpha
}

/**
 * WhatsApp-style top bar: back + reserved space for docking avatar + fading title + QR + more
 */
@Composable
fun GroupSettingsTopBar(
    groupName: String,
    avatarAlpha: Float,
    onBack: () -> Unit,
    onQrClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    canInviteViaLink: Boolean = true
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MicrobondTextPrimary
                )
            }

            // Reserved slot where the animated avatar docks when collapsed
            Spacer(Modifier.width(42.dp))

            Text(
                text = groupName,
                color = MicrobondTextPrimary,
                fontSize = 18.5.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { alpha = avatarAlpha }
            )

            // QR code icon
            IconButton(onClick = {
                if (canInviteViaLink) {
                    onQrClick()
                } else {
                    Toast.makeText(context, "Only group admins can share group QR codes.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "QR Code",
                    tint = MicrobondTextPrimary
                )
            }

            // More options menu icon
            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MicrobondTextPrimary
                )
            }
        }
    }

    
}

/**
 * WhatsApp-accurate animated avatar overlay that moves and scales from the large
 * hero center (110dp) directly into the docked position (36dp) in the top bar as
 * the user scrolls up.
 */
@Composable
fun WhatsAppGroupAvatarOverlay(
    groupIconUri: String?,
    groupName: String,
    scrollState: ScrollState,
    onClick: () -> Unit
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val scrollPx = scrollState.value.toFloat()

    // Collapse distance from top to fully docked
    val collapseThresholdPx = with(density) { 140.dp.toPx() }
    val collapseFraction = (scrollPx / collapseThresholdPx).coerceIn(0f, 1f)

    // Center coordinates
    val heroCenterX = screenWidthDp / 2
    val heroCenterY = 56.dp + 16.dp + 55.dp // TopBar(56) + TopPad(16) + HalfAvatar(55) = 127dp
    val dockedCenterX = 48.dp + 6.dp + 18.dp // BackBtn(48) + Pad(6) + HalfAvatar(18) = 72dp
    val dockedCenterY = 28.dp // Center of 56dp TopBar

    val currentSize: Dp = lerp(110.dp, 36.dp, collapseFraction)
    val currentCenterX: Dp = lerp(heroCenterX, dockedCenterX, collapseFraction)

    // Natural scroll moves heroCenterY upwards by scroll offset, smoothly converging to dockedCenterY
    val naturalCenterY = heroCenterY - with(density) { scrollPx.toDp() }
    val currentCenterY: Dp = lerp(naturalCenterY, dockedCenterY, collapseFraction)

    val currentLeft = currentCenterX - (currentSize / 2)
    val currentTop = currentCenterY - (currentSize / 2)

    val painter = groupIconUri?.takeIf { it.isNotBlank() }?.let { rememberAsyncImagePainter(it) }

    Box(
        modifier = Modifier
            .offset(x = currentLeft, y = currentTop)
            .size(currentSize)
            .clip(CircleShape)
            .background(Color(0xFF182229), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = groupName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A3C35), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = "Default Group Icon",
                    tint = MicrobondActiveGreen,
                    modifier = Modifier.size(currentSize * 0.52f)
                )
            }
        }
    }
}

/**
 * Large WhatsApp settings hero: reserves space for moving avatar, renders group name,
 * member count, description, and the 4 WhatsApp action buttons (Voice, Video, Share, Search).
 */
@Composable
fun GroupSettingsHero(
    groupName: String,
    memberCount: Int,
    groupType: SavingsGroupType,
    description: String,
    onEditName: () -> Unit,
    onAddDescription: () -> Unit,
    canEditSettings: Boolean = true,
    canInviteViaLink: Boolean = true,
    onVoiceClick: () -> Unit = {},
    onVideoClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val handleEditName = {
        if (canEditSettings) {
            onEditName()
        } else {
            Toast.makeText(context, "Only group admins can edit this group's info.", Toast.LENGTH_SHORT).show()
        }
    }
    val handleEditDescription = {
        if (canEditSettings) {
            onAddDescription()
        } else {
            Toast.makeText(context, "Only group admins can edit this group's info.", Toast.LENGTH_SHORT).show()
        }
    }
    val handleShare = {
        if (canInviteViaLink) {
            onShareClick()
        } else {
            Toast.makeText(context, "Only group admins can share invite links.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Space reserved for the animated avatar overlay
        Spacer(Modifier.height(116.dp))

        // Group name (clickable to edit)
        Text(
            text = groupName,
            color = MicrobondTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable(onClick = handleEditName)
                .padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(4.dp))

        // Subtitle: "Group · X members"
        Text(
            text = "Group · $memberCount members",
            color = MicrobondInactiveGray,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(8.dp))

        // Description or "Add group description" prompt
        if (description.isBlank()) {
            Text(
                text = "Add group description",
                color = MicrobondActiveGreen,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable(onClick = handleEditDescription)
                    .padding(horizontal = 24.dp, vertical = 2.dp)
            )
        } else {
            Text(
                text = description,
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .clickable(onClick = handleEditDescription)
            )
        }

        Spacer(Modifier.height(18.dp))

        // ── 4 WhatsApp Action Buttons: Voice, Video, Share, Search ───────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WhatsAppHeroActionButton(
                icon = Icons.Default.Call,
                label = "Voice",
                onClick = onVoiceClick
            )
            WhatsAppHeroActionButton(
                icon = Icons.Default.Videocam,
                label = "Video",
                onClick = onVideoClick
            )
            WhatsAppHeroActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                onClick = handleShare
            )
            WhatsAppHeroActionButton(
                icon = Icons.Default.Search,
                label = "Search",
                onClick = onSearchClick
            )
        }
    }
}

@Composable
fun WhatsAppHeroActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(microBondSurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            color = MicrobondTextPrimary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * WhatsApp tab selector: "Members" and "Settings" with green active indicator bar.
 */
@Composable
fun WhatsAppGroupTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Members" Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected("Members") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Members",
                    color = if (selectedTab == "Members") Color.White else MicrobondInactiveGray,
                    fontSize = 15.sp,
                    fontWeight = if (selectedTab == "Members") FontWeight.SemiBold else FontWeight.Normal
                )
            }

            // "Settings" Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected("Settings") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Settings",
                    color = if (selectedTab == "Settings") Color.White else MicrobondInactiveGray,
                    fontSize = 15.sp,
                    fontWeight = if (selectedTab == "Settings") FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }

        // Green indicator line
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .padding(horizontal = 24.dp)
                    .background(
                        if (selectedTab == "Members") MicrobondActiveGreen else Color.Transparent,
                        RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                    )
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .padding(horizontal = 24.dp)
                    .background(
                        if (selectedTab == "Settings") MicrobondActiveGreen else Color.Transparent,
                        RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                    )
            )
        }

        // Subtle bottom divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(Color(0xFF1F2C34))
        )
    }
}

package com.microbond.app.core.components.threadbubbles.bubbletype.rulesbubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.SecurityRulesData

private val SecAccent     = Color(0xFF4ADE80)
private val SecBadgeBg    = Color(0xFF1A3D30)
private val SecDivider    = Color.White.copy(alpha = 0.08f)
private val SecMuted      = Color(0xFF8696A0)

/**
 * SECURITY_RULES bubble — displays pinned group rules.
 * Naming convention: MicroBondSecurity[Type]Bubble
 * Siblings planned: MicroBondSecurity[Type2..4]Bubble
 */
@Composable
fun MicroBondSecurityRulesBubble(
    meta: MicroBondMeta,
    data: SecurityRulesData,
    onViewAllClick: (() -> Unit)? = null,
    onRuleClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    _root_ide_package_.com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        authorType = _root_ide_package_.com.microbond.app.core.components.threadbubbles.BubbleAuthorType.USER,
        showTimestamp = false,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── HEADER ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = null,
                    tint = SecAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = data.title,
                    color = SecAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = SecDivider, thickness = 0.6.dp)

            // ── RULES LIST ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                data.rules.forEachIndexed { index, rule ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRuleClick?.invoke(index) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Number badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(SecBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = SecAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = rule,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                    // Divider between rules (not after last)
                    if (index < data.rules.lastIndex) {
                        HorizontalDivider(
                            color = SecDivider,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            // ── FOOTER ACTIONS ───────────────────────────────────
            HorizontalDivider(color = SecDivider, thickness = 0.6.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.pinnedLabel,
                    color = SecAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = data.viewAllLabel,
                    color = SecAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onViewAllClick?.invoke() }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = meta.time,
                    color = SecMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

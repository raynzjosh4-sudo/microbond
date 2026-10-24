package com.microbond.app.feature_groups.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondInfoBannerBorder
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondTextPrimaryLight

/**
 * Item Card in the discovery feed that manages its individual join lifecycle state:
 * - IDLE: [ Join ] or [ Request to Join ]
 * - LOADING: CircularProgressIndicator
 * - PENDING_APPROVAL: [ ⏳ Requested ]
 * - PENDING_GUARANTOR: [ 🤝 Awaiting Name's Vouch ]
 * - JOINED: [ ✓ Joined ]
 */
@Composable
fun DiscoveredGroupCard(
    item: DiscoveredGroupUiItem,
    onJoinClick: () -> Unit,
    onStatusClick: () -> Unit,
    onOpenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val group = item.group

    Surface(
        color = Color(0xFF131D20),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MicrobondInfoBannerBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Group Avatar Circle
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B382B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = MicrobondActiveGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.name,
                        color = MicrobondTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Mutual contacts info
                    val contactNames = group.contactMembers.map { it.profileName.trim() }.filter { it.isNotBlank() }
                    val mutualText = when {
                        contactNames.isNotEmpty() -> "${contactNames.size} mutual contacts (${contactNames.take(3).joinToString(", ")})"
                        group.mutualContactsCount > 0 -> "${group.mutualContactsCount} mutual contacts"
                        else -> "Verified circle"
                    }

                    Text(
                        text = mutualText,
                        color = MicrobondActiveGreen,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Commitments line
            val formattedType = group.type.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
            val financialCommitmentText = if (group.contributionAmount != null) {
                val formattedAmount = if (group.contributionAmount >= 1000) {
                    String.format("%,d", group.contributionAmount.toLong())
                } else {
                    group.contributionAmount.toLong().toString()
                }
                val freqPart = if (!group.contributionFrequency.isNullOrBlank()) {
                    " / ${group.contributionFrequency.lowercase()}"
                } else ""
                "$formattedAmount ${group.currency}$freqPart • $formattedType"
            } else {
                "$formattedType • ${group.memberCount} members"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = financialCommitmentText,
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Item-Level Join Button State
                when (val state = item.joinState) {
                    JoinState.IDLE -> {
                        val actionText = if (group.requiresAdminApproval || group.requireGuarantors) {
                            "Request to Join"
                        } else {
                            "Join"
                        }
                        Button(
                            onClick = onJoinClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MicrobondActiveGreen,
                                contentColor = microBondTextPrimaryLight
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(actionText, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    JoinState.LOADING -> {
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MicrobondActiveGreen,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    JoinState.PENDING_APPROVAL -> {
                        Surface(
                            onClick = onStatusClick,
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF282212),
                            border = BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Requested",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    is JoinState.PENDING_GUARANTOR -> {
                        Surface(
                            onClick = onStatusClick,
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF132B27),
                            border = BorderStroke(1.dp, Color(0xFF2DD4BF).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = Color(0xFF2DD4BF),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Awaiting Vouch",
                                    color = Color(0xFF2DD4BF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    JoinState.JOINED -> {
                        Surface(
                            onClick = onOpenClick,
                            shape = RoundedCornerShape(12.dp),
                            color = MicrobondActiveGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, MicrobondActiveGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MicrobondActiveGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Joined",
                                    color = MicrobondActiveGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

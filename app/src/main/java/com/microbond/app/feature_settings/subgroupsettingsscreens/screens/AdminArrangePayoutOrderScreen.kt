package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar

data class OrderMember(val id: String, val name: String, val initials: String)

@Composable
fun AdminArrangePayoutOrderScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var members by remember {
        mutableStateOf(
            listOf(
                OrderMember("1", "Joshua Raynz", "JR"),
                OrderMember("2", "Alice Smith", "AS"),
                OrderMember("3", "Bob Johnson", "BJ"),
                OrderMember("4", "Carol Williams", "CW"),
                OrderMember("5", "David Brown", "DB")
            )
        )
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Arrange Payout Order",
                subtitle = groupName,
                onBack = onBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onBack,
                containerColor = MicrobondActiveGreen,
                contentColor = MicrobondDarkBackground
            ) {
                Text("Save Order", fontWeight = FontWeight.Bold)
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            Text(
                text = "Use the arrows to manually set the exact order members will receive their payouts in the cycle.",
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(members) { index, member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Position Number
                        Text(
                            text = "${index + 1}",
                            color = MicrobondActiveGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(32.dp)
                        )

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MicrobondInactiveGray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.initials,
                                color = MicrobondTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Name
                        Text(
                            text = member.name,
                            color = MicrobondTextPrimary,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )

                        // Up Arrow
                        IconButton(
                            onClick = {
                                if (index > 0) {
                                    val mutableList = members.toMutableList()
                                    val temp = mutableList[index]
                                    mutableList[index] = mutableList[index - 1]
                                    mutableList[index - 1] = temp
                                    members = mutableList
                                }
                            },
                            enabled = index > 0
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Move Up",
                                tint = if (index > 0) MicrobondTextPrimary else MicrobondInactiveGray.copy(alpha = 0.3f)
                            )
                        }

                        // Down Arrow
                        IconButton(
                            onClick = {
                                if (index < members.size - 1) {
                                    val mutableList = members.toMutableList()
                                    val temp = mutableList[index]
                                    mutableList[index] = mutableList[index + 1]
                                    mutableList[index + 1] = temp
                                    members = mutableList
                                }
                            },
                            enabled = index < members.size - 1
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Move Down",
                                tint = if (index < members.size - 1) MicrobondTextPrimary else MicrobondInactiveGray.copy(alpha = 0.3f)
                            )
                        }
                    }
                    HorizontalDivider(color = MicrobondDividerDark, thickness = 0.5.dp)
                }
            }
        }
    }
}

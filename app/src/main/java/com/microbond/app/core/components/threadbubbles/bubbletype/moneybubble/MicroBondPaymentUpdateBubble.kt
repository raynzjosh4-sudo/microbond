package com.microbond.app.core.components.threadbubbles.bubbletype.moneybubble

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

/**
 * Payment update bubble matching the WhatsApp card design.
 * Displays deposited vs. required amounts, a balance reminder with date,
 * and an action button to view details or receipt.
 */
@Composable
fun MicroBondPaymentUpdateBubble(
    meta: MicroBondMeta,
    depositedAmountText: String = "UGX 80,000",
    requiredAmountText: String? = "UGX 100,000",
    reminderText: String = "Reminder: UGX 20,000",
    dateText: String = "Deposited on 30 Sep 2026",
    title: String = "Payment update",
    paymentPhoneText: String? = null,
    paymentMethodText: String? = null,
    onViewClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    _root_ide_package_.com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        showTimestamp = false,
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Header Row: Green Check Circle + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!paymentPhoneText.isNullOrBlank()) {
                Text(
                    text = "Paid to $paymentPhoneText",
                    color = Color(0xFF8696A0),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 26.dp, top = 2.dp)
                )
            }

            if (!paymentMethodText.isNullOrBlank()) {
                Text(
                    text = "Method: $paymentMethodText",
                    color = Color(0xFF8696A0),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 26.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Split Amounts Row: Deposited | Required
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Deposited",
                        color = Color(0xFF8696A0),
                        fontSize = 12.sp
                    )
                    Text(
                        text = depositedAmountText,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                if (requiredAmountText != null) {
                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color.White.copy(alpha = 0.12f))
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Required",
                            color = Color(0xFF8696A0),
                            fontSize = 12.sp
                        )
                        Text(
                            text = requiredText(requiredAmountText),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            // Subtle Horizontal Divider
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f),
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Bottom Row: [!] Reminder & Date + "View" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .border(1.5.dp, Color(0xFF4ADE80), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "!",
                            color = Color(0xFF4ADE80),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 12.sp
                        )
                    }

                    Column {
                        Text(
                            text = reminderText,
                            color = Color(0xFF4ADE80),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            text = dateText,
                            color = Color(0xFF8696A0),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF4ADE80), RoundedCornerShape(6.dp))
                        .clickable { onViewClick?.invoke() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View",
                        color = Color(0xFF4ADE80),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun requiredText(text: String): String = text.ifBlank { "UGX 0" }

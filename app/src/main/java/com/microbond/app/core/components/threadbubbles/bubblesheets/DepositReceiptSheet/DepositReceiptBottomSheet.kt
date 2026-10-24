package com.microbond.app.core.components.threadbubbles.bubblesheets.DepositReceiptSheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.R
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositReceiptBottomSheet(
    event: ThreadEvent,
    groupName: String,
    isContribution: Boolean = false,
    onDismiss: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val telecomLower = (event.paymentMethod ?: "").lowercase()
    val isMtn = telecomLower.contains("mtn")
    val isAirtel = telecomLower.contains("airtel")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E2B32),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF5E6C74))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MicrobondTextPrimary
                    )
                }
                Text(
                    text = "Deposit Receipt",
                    color = MicrobondTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Telecom Avatar or Success Circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isMtn -> Color(0xFFFFCC00).copy(alpha = 0.2f)
                            isAirtel -> Color(0xFFE53935).copy(alpha = 0.2f)
                            else -> Color(0xFF103629)
                        }
                    )
                    .border(
                        2.dp,
                        when {
                            isMtn -> Color(0xFFFFCC00)
                            isAirtel -> Color(0xFFE53935)
                            else -> MicrobondActiveGreen
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isMtn -> Image(
                        painter = painterResource(id = R.drawable.ic_mtn),
                        contentDescription = "MTN",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                    )
                    isAirtel -> Image(
                        painter = painterResource(id = R.drawable.ic_airtel),
                        contentDescription = "Airtel",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                    )
                    else -> Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MicrobondActiveGreen,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large Amount Display
            Text(
                text = event.amount.orEmpty().ifBlank { "UGX 0" },
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            // Full Date Display
            Text(
                text = event.fullDate ?: "${event.time} · Completed",
                color = MicrobondInactiveGray,
                fontSize = 12.5.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Detailed Breakdown Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF111B21))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ReceiptDetailRow(
                    label = "Deposited By",
                    value = event.senderName ?: (if (event.isCurrentUser) "You" else "Member"),
                    subValue = event.senderPhone
                )

                HorizontalDivider(color = Color(0xFF1F2C34), thickness = 0.5.dp)

                ReceiptDetailRow(
                    label = "Destination",
                    value = groupName
                )

                HorizontalDivider(color = Color(0xFF1F2C34), thickness = 0.5.dp)

                ReceiptDetailRow(
                    label = "Payment Method",
                    value = event.paymentMethod.orEmpty().ifBlank { "Mobile Money" }
                )

                HorizontalDivider(color = Color(0xFF1F2C34), thickness = 0.5.dp)

                ReceiptDetailRow(
                    label = "Date & Time",
                    value = event.fullDate ?: event.time
                )

                HorizontalDivider(color = Color(0xFF1F2C34), thickness = 0.5.dp)

                ReceiptDetailRow(
                    label = "Reference ID",
                    value = event.referenceId.orEmpty().ifBlank { "REF: #DEP-94821" }
                )

                if (!event.cycleNote.isNullOrBlank()) {
                    HorizontalDivider(color = Color(0xFF1F2C34), thickness = 0.5.dp)
                    ReceiptDetailRow(
                        label = "Note",
                        value = event.cycleNote
                    )
                }

                HorizontalDivider(color = Color(0xFF1F2C34), thickness = 0.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Status", color = MicrobondInactiveGray, fontSize = 13.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MicrobondActiveGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Verified & Saved",
                            color = MicrobondActiveGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MicrobondActiveGreen,
                    contentColor = MicrobondDarkBackground
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Receipt",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Done",
                    color = MicrobondInactiveGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ReceiptDetailRow(
    label: String,
    value: String,
    subValue: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = MicrobondInactiveGray,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(1.5f)
        ) {
            Text(
                text = value,
                color = MicrobondTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            if (!subValue.isNullOrBlank()) {
                Text(
                    text = subValue,
                    color = MicrobondInactiveGray,
                    fontSize = 11.5.sp
                )
            }
        }
    }
}

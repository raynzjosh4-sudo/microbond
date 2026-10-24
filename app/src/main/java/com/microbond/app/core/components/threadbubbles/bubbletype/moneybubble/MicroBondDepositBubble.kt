package com.microbond.app.core.components.threadbubbles.bubbletype.moneybubble

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.microbond.app.core.components.threadbubbles.bubbletype.moneybubble.MicroBondPaymentUpdateBubble
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondDepositBubble(
    requiredAmount: Long,
    depositedAmount: Long,
    fullDate: String?,
    amount: String,
    paymentMethod: String,
    referenceId: String,
    cycleNote: String,
    isVerified: Boolean,
    meta: MicroBondMeta,
    onViewReceiptClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val depositedText = when {
        depositedAmount > 0L -> "UGX %,d".format(depositedAmount)
        amount.isNotBlank() -> amount
        else -> "UGX 0"
    }

    val requiredText = when {
        requiredAmount > 0L && requiredAmount != depositedAmount -> "UGX %,d".format(requiredAmount)
        else -> null
    }

    val reminderText = when {
        requiredAmount > depositedAmount && depositedAmount > 0L -> {
            "Reminder: UGX %,d".format(requiredAmount - depositedAmount)
        }
        requiredAmount > 0L && depositedAmount == 0L -> {
            "Reminder: UGX %,d".format(requiredAmount)
        }
        depositedAmount >= requiredAmount && requiredAmount > 0L -> {
            "Payment complete"
        }
        else -> "Reminder: UGX 20,000"
    }

    val cleanDate = fullDate?.let { dateStr ->
        val datePart = dateStr.split("·").firstOrNull()?.trim() ?: dateStr
        if (datePart.contains(",")) datePart.substringAfter(",").trim() else datePart
    } ?: "30 Sep 2026"

    MicroBondPaymentUpdateBubble(
        meta = meta,
        depositedAmountText = depositedText,
        requiredAmountText = requiredText,
        reminderText = reminderText,
        dateText = "Deposited on $cleanDate",
        title = "Payment update",
        onViewClick = onViewReceiptClick,
        modifier = modifier
    )
}

package com.microbond.app.screens.threadscreen.threadscreenwidgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.microbond.app.core.components.threadbubbles.model.MicroBondReply
import com.microbond.app.core.components.threadbubbles.threads.ThreadInputBar
import com.microbond.app.core.data_classes_and_enums.AttachmentOption
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.PaymentUpdateData
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.payment.PaymentContext
import com.microbond.app.core.payment.PaymentFlowManager
import com.microbond.app.core.payment.PaymentFlowState
import com.microbond.app.core.payment.PaymentMethod
import com.microbond.app.core.payment.PaymentStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ThreadInputSection(
    inputValue: String,
    paymentState: PaymentFlowState,
    paymentContext: PaymentContext,
    replyingToEvent: ThreadEvent?,
    isVoiceRecording: Boolean,
    isVoiceRecordingLocked: Boolean,
    recordingDurationSeconds: Int,
    threadEvents: List<ThreadEvent>,
    coroutineScope: CoroutineScope,
    onInputValueChange: (String) -> Unit,
    onPaymentStateChange: (PaymentFlowState) -> Unit,
    onAppendOutgoingEvent: (ThreadEvent) -> Unit,
    onAppendPaymentPrompts: (List<ThreadEvent>) -> Unit,
    onScrollToBottom: () -> Unit,
    onCancelReply: () -> Unit,
    onAttachmentPickerToggle: () -> Unit,
    onAttachmentPickerDismiss: () -> Unit,
    onRecordingStateChange: (Boolean) -> Unit,
    onRecordingStart: () -> Unit,
    onRecordingFinish: (Boolean) -> Unit,
    onOptionSelected: (AttachmentOption) -> Unit,
    onImagesSelected: (List<android.net.Uri>, String) -> Unit,
) {
    fun nextId() = (threadEvents.maxOfOrNull { it.id } ?: 0) + 1
    fun now() = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

    ThreadInputBar(
        inputValue = inputValue,
        paymentAmountChip = paymentState.amountChipDisplay.takeIf { it.isNotBlank() },
        isInPaymentMode = paymentState.step == PaymentStep.AWAITING_AMOUNT ||
                paymentState.step == PaymentStep.AWAITING_NUMBER,
        onValueChange = { newValue ->
            val filteredValue = if (
                paymentState.step == PaymentStep.AWAITING_AMOUNT ||
                paymentState.step == PaymentStep.AWAITING_NUMBER
            ) newValue.filter(Char::isDigit) else newValue

            onInputValueChange(filteredValue)

            if (paymentState.step == PaymentStep.IDLE &&
                PaymentFlowManager.isSlashPayCommand(filteredValue)
            ) {
                val started = PaymentFlowManager.onSlashPayTyped()
                onAppendPaymentPrompts(started.promptBubbles)
                onPaymentStateChange(started.copy(promptBubbles = emptyList()))
                onInputValueChange("")
            } else if (paymentState.step != PaymentStep.IDLE) {
                onPaymentStateChange(
                    PaymentFlowManager.onInputChanged(filteredValue, paymentState, paymentContext)
                )
            }
        },
        onSubmitClick = {
            when (paymentState.step) {
                PaymentStep.AWAITING_AMOUNT -> {
                    val next = PaymentFlowManager.confirmAmount(inputValue, paymentState, paymentContext)
                    if (next != null) {
                        onAppendPaymentPrompts(next.promptBubbles)
                        if (next.step == PaymentStep.AWAITING_NUMBER) {
                            onPaymentStateChange(next.copy(promptBubbles = emptyList()))
                            onInputValueChange(paymentContext.registeredPhone)
                        } else {
                            onAppendOutgoingEvent(
                                ThreadEvent(
                                    id = nextId(), text = "", time = now(),
                                    isCurrentUser = true, status = MessageStatus.SENT,
                                    type = ThreadBubbleType.PAYMENT_UPDATE, senderName = "You",
                                    paymentUpdate = PaymentUpdateData(
                                        depositedAmountText = next.amountChipDisplay,
                                        requiredAmountText = paymentContext.amountDueDisplay,
                                        reminderText = "Demo only — no payment was processed",
                                        dateText = "Payment method selected",
                                        title = "Demo payment",
                                        paymentMethodText = next.method?.displayName
                                    )
                                )
                            )
                            onScrollToBottom()
                            onPaymentStateChange(PaymentFlowManager.cancel())
                            onInputValueChange("")
                        }
                    } else {
                        val invalid = PaymentFlowManager.invalidAmountPrompt(paymentState, paymentContext)
                        onAppendPaymentPrompts(invalid.promptBubbles.drop(paymentState.promptBubbles.size))
                        onPaymentStateChange(invalid.copy(promptBubbles = emptyList()))
                        onInputValueChange(if (paymentContext.partialAllowed) "" else paymentContext.amountDue.toString())
                    }
                }
                PaymentStep.AWAITING_NUMBER -> {
                    if (!PaymentFlowManager.isValidPhoneNumber(inputValue)) {
                        val invalid = PaymentFlowManager.invalidPhonePrompt(paymentState)
                        onAppendPaymentPrompts(invalid.promptBubbles.drop(paymentState.promptBubbles.size))
                        onPaymentStateChange(invalid.copy(promptBubbles = emptyList()))
                        return@ThreadInputBar
                    }
                    var updated = PaymentFlowManager.onInputChanged(inputValue, paymentState, paymentContext)
                    updated = PaymentFlowManager.confirmNumber(updated)
                    onAppendOutgoingEvent(
                        ThreadEvent(
                            id = nextId(), text = "", time = now(),
                            isCurrentUser = true, status = MessageStatus.SENT,
                            type = ThreadBubbleType.PAYMENT_UPDATE, senderName = "You",
                            senderPhone = updated.resolvedPhone,
                            paymentUpdate = PaymentUpdateData(
                                depositedAmountText = updated.amountChipDisplay,
                                requiredAmountText = paymentContext.amountDueDisplay,
                                reminderText = "Demo only — no payment was processed",
                                dateText = "Payment method selected",
                                title = "Demo payment",
                                paymentPhoneText = updated.resolvedPhone,
                                paymentMethodText = updated.method?.displayName
                            )
                        )
                    )
                    onScrollToBottom()
                    onPaymentStateChange(PaymentFlowManager.cancel())
                    onInputValueChange("")
                }
                PaymentStep.READY -> {
                    onPaymentStateChange(PaymentFlowManager.cancel())
                    // sendTextMessage delegated back via inputValue + submit
                    val messageText = inputValue.trim()
                    if (messageText.isNotEmpty()) {
                        onAppendOutgoingEvent(
                            ThreadEvent(
                                id = nextId(), text = messageText, time = now(),
                                isCurrentUser = true, status = MessageStatus.SENT,
                                type = ThreadBubbleType.TEXT, senderName = "You",
                                senderInitials = "Y", senderColor = Color(0xFF00A884)
                            )
                        )
                        onInputValueChange("")
                        onScrollToBottom()
                    }
                }
                else -> {
                    val messageText = inputValue.trim()
                    if (messageText.isNotEmpty()) {
                        onAppendOutgoingEvent(
                            ThreadEvent(
                                id = nextId(), text = messageText, time = now(),
                                isCurrentUser = true, status = MessageStatus.SENT,
                                type = ThreadBubbleType.TEXT, senderName = "You",
                                senderInitials = "Y", senderColor = Color(0xFF00A884)
                            )
                        )
                        onInputValueChange("")
                        onScrollToBottom()
                    }
                }
            }
        },
        onAttachClick = onAttachmentPickerToggle,
        onInputFocus = {
            onAttachmentPickerDismiss()
        },
        onMicClick = {},
        onRecordingStateChange = onRecordingStateChange,
        isRecording = isVoiceRecording,
        isRecordingLocked = isVoiceRecordingLocked,
        recordingDuration = "%d:%02d".format(recordingDurationSeconds / 60, recordingDurationSeconds % 60),
        onRecordingStart = onRecordingStart,
        onRecordingFinish = onRecordingFinish,
        onRecordingLock = { /* handled in ThreadScreen */ },
        onPaymentClick = {
            if (paymentState.step == PaymentStep.IDLE) {
                val started = PaymentFlowManager.onMoneyIconTapped()
                onAppendPaymentPrompts(started.promptBubbles)
                onPaymentStateChange(started.copy(promptBubbles = emptyList()))
                onInputValueChange("")
            } else {
                onPaymentStateChange(PaymentFlowManager.cancel())
                onInputValueChange("")
            }
        },
        replyTo = replyingToEvent?.let { event ->
            MicroBondReply(
                author = if (event.isCurrentUser) "You" else (event.senderName ?: "Unknown"),
                preview = event.replyPreviewText(),
                accent = if (event.isCurrentUser) Color(0xFF71D7C2) else event.senderColor
            )
        },
        onCancelReply = onCancelReply
    )
}

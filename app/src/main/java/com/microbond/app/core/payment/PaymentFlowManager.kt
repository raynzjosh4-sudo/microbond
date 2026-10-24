package com.microbond.app.core.payment

import com.microbond.app.core.data_classes_and_enums.ActionPromptData
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import androidx.compose.ui.graphics.Color

// ─── Enums & State ──────────────────────────────────────────────────────────

/**
 * Which step of the inline payment flow the user is currently in.
 */
enum class PaymentStep {
    /** No active payment flow */
    IDLE,
    /** Waiting for the user to choose a payment method */
    AWAITING_METHOD,
    /** Waiting for the user to type/confirm the amount */
    AWAITING_AMOUNT,
    /** Amount confirmed (shown as chip), waiting for phone number confirmation */
    AWAITING_NUMBER,
    /** Both amount and number are captured — ready to trigger STK push */
    READY
}

enum class PaymentMethod(val displayName: String) {
    MOBILE_MONEY("Mobile Money"),
    CARD("Card"),
    BANK_TRANSFER("Bank transfer")
}

/**
 * All the information the app knows about the current group's payment context.
 * Passed in from the ViewModel / ThreadScreen.
 */
data class PaymentContext(
    /** How much the user owes for this cycle (in smallest currency unit, e.g. UGX) */
    val amountDue: Long,
    /** Display string of the due amount, e.g. "UGX 10,000" */
    val amountDueDisplay: String,
    /** Whether the group admin has enabled partial payments */
    val partialAllowed: Boolean,
    /** The user's registered Mobile Money number */
    val registeredPhone: String,
    /** Masked version for display, e.g. "MTN +256 77...1234" */
    val registeredPhoneDisplay: String
)

/**
 * Live state of the payment flow at any moment.
 */
data class PaymentFlowState(
    val step: PaymentStep = PaymentStep.IDLE,
    val method: PaymentMethod? = null,
    /** Raw amount the user typed, e.g. "10000" */
    val enteredAmount: String = "",
    /** Formatted display for the chip above the input bar, e.g. "UGX 10,000" */
    val amountChipDisplay: String = "",
    /** Final resolved phone number to charge */
    val resolvedPhone: String = "",
    /** Prompt bubbles injected into the thread for this session */
    val promptBubbles: List<ThreadEvent> = emptyList()
)

// ─── Manager ────────────────────────────────────────────────────────────────

object PaymentFlowManager {

    private var nextBubbleId = 9000

    private fun nextId() = nextBubbleId++

    /**
     * Called when the user taps the money ($) icon.
     * Returns the new PaymentFlowState with prompt bubbles to inject into the thread.
     */
    fun onMoneyIconTapped(): PaymentFlowState {
        return PaymentFlowState(
            step = PaymentStep.AWAITING_METHOD,
            promptBubbles = buildPaymentMethodPrompt()
        )
    }

    /**
     * Called when the user types /pay in the input field.
     * Returns the new PaymentFlowState with prompt bubbles to inject into the thread.
     */
    fun onSlashPayTyped(): PaymentFlowState {
        return PaymentFlowState(
            step = PaymentStep.AWAITING_METHOD,
            promptBubbles = buildPaymentMethodPrompt()
        )
    }

    fun selectMethod(method: PaymentMethod, context: PaymentContext): PaymentFlowState {
        val prompt = when (method) {
            PaymentMethod.MOBILE_MONEY ->
                "You owe **${context.amountDueDisplay}**. Full payment is required. " +
                    "Mobile Money will use **${context.registeredPhoneDisplay}**; you can edit the number after confirming the amount."
            PaymentMethod.CARD ->
                "You owe **${context.amountDueDisplay}**. Full payment is required. " +
                    "This demo will not ask for or process card details."
            PaymentMethod.BANK_TRANSFER ->
                "You owe **${context.amountDueDisplay}**. Full payment is required. " +
                    "This demo will not ask for or process bank account details."
        }
        return PaymentFlowState(
            step = PaymentStep.AWAITING_AMOUNT,
            method = method,
            resolvedPhone = context.registeredPhone,
            promptBubbles = listOf(systemPromptBubble(nextId(), prompt))
        )
    }

    /**
     * Called on every keystroke while in payment mode.
     * Tracks what the user is typing (amount or phone number).
     */
    fun onInputChanged(
        input: String,
        currentState: PaymentFlowState,
        context: PaymentContext
    ): PaymentFlowState {
        if (currentState.step == PaymentStep.IDLE) return currentState

        return when (currentState.step) {
            PaymentStep.AWAITING_METHOD -> currentState
            PaymentStep.AWAITING_AMOUNT -> currentState.copy(enteredAmount = input)
            PaymentStep.AWAITING_NUMBER -> {
                val cleaned = input.replace(" ", "").replace("+", "").trim()
                currentState.copy(resolvedPhone = cleaned)
            }
            else -> currentState
        }
    }

    /**
     * Called when the user presses send while in AWAITING_AMOUNT step.
     * Validates the entered amount and moves to AWAITING_NUMBER.
     * Returns null if validation fails.
     */
    fun confirmAmount(
        rawInput: String,
        currentState: PaymentFlowState,
        context: PaymentContext
    ): PaymentFlowState? {
        val cleaned = rawInput.replace(",", "").replace(" ", "").trim()
        val amount = cleaned.toLongOrNull() ?: return null
        if (amount <= 0) return null
        if (!context.partialAllowed && amount != context.amountDue) return null

        val displayAmount = formatAmount(amount)
        val mobileMoney = currentState.method == PaymentMethod.MOBILE_MONEY
        val nextStep = if (mobileMoney) PaymentStep.AWAITING_NUMBER else PaymentStep.READY
        val prompt = if (mobileMoney) {
            systemPromptBubble(
                nextId(),
                "Paying to **${context.registeredPhoneDisplay}**. Confirm or edit the phone number below, then press Send."
            )
        } else {
            systemPromptBubble(
                nextId(),
                "This is a demo selection only. No payment will be processed."
            )
        }

        return currentState.copy(
            step = nextStep,
            enteredAmount = cleaned,
            amountChipDisplay = displayAmount,
            resolvedPhone = context.registeredPhone,
            promptBubbles = currentState.promptBubbles + prompt
        )
    }

    fun invalidAmountPrompt(currentState: PaymentFlowState, context: PaymentContext): PaymentFlowState =
        currentState.copy(
            promptBubbles = currentState.promptBubbles + systemPromptBubble(
                nextId(),
                "This group requires the full ${context.amountDueDisplay} daily deposit. Partial payments are not allowed."
            )
        )

    /**
     * Called when the user is satisfied with the number and taps Send.
     * Moves state to READY so the caller can trigger the STK push.
     */
    fun confirmNumber(currentState: PaymentFlowState): PaymentFlowState {
        return currentState.copy(step = PaymentStep.READY)
    }

    fun isValidPhoneNumber(input: String): Boolean {
        val cleaned = input.filter(Char::isDigit)
        return isPhoneNumber(cleaned)
    }

    fun invalidPhonePrompt(currentState: PaymentFlowState): PaymentFlowState =
        currentState.copy(
            promptBubbles = currentState.promptBubbles + systemPromptBubble(
                nextId(),
                "Enter a valid mobile money phone number before sending the payment."
            )
        )

    private fun buildPaymentMethodPrompt(): List<ThreadEvent> =
        listOf(
            systemPromptBubble(
                nextId(),
                "Choose a payment method: **Mobile Money** · **Card** · **Bank transfer**"
            )
        )

    /** Resets the flow completely back to IDLE. */
    fun cancel(): PaymentFlowState = PaymentFlowState()

    /**
     * Detects /pay command prefix in the input field (case-insensitive).
     * Used by the input bar to auto-trigger the payment flow.
     */
    fun isSlashPayCommand(input: String): Boolean =
        input.trim().lowercase().startsWith("/pay")

    // ─── Private helpers ────────────────────────────────────────────────────

    private fun systemPromptBubble(id: Int, message: String) = ThreadEvent(
        id = id,
        text = "",
        time = "now",
        isCurrentUser = false,
        type = ThreadBubbleType.ACTION_PROMPT,
        actionPromptData = ActionPromptData(message = message)
    )

    private fun isPhoneNumber(input: String): Boolean =
        input.length in 9..13 && input.all { it.isDigit() }

    private fun formatAmount(amount: Long): String =
        "UGX %,d".format(amount)
}

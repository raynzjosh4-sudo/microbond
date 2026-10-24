package com.microbond.app.models.usermodel

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.microbond.app.core.payment.PaymentMethod

/**
 * Represents a global user in the Microbond platform.
 * Group-specific details (like roles and contribution statuses) should be maintained
 * in group-specific models or relationships (e.g., GroupMemberSummary).
 */
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String, // Primary identifier for a contact app
    @DrawableRes val avatarRes: Int? = null,
    val avatarColor: Color = Color.Gray,
    val profilePictureUrl: String? = null, // Cloud URL for avatar
    val aboutStatus: String = "Hey there! I am using Microbond.", // WhatsApp-style status
    val defaultCurrency: String = "UGX",
    val kycStatus: KycVerificationStatus = KycVerificationStatus.UNVERIFIED,
    val trustScore: Int? = null, // Global trust score across all groups
    val isVerified: Boolean = false,
    val fcmToken: String? = null, // For automated bot messaging/push notifications
    val isOnline: Boolean = false,
    val lastSeenAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val joinedDate: String? = null,
    val paymentAccounts: List<PaymentAccount> = emptyList(), // Accounts to receive payouts
    val paymentMethods: List<SavedPaymentMethod> = emptyList(), // Methods to pay contributions
    val inviteAndEarn: InviteAndEarn = InviteAndEarn(),
    val preferences: UserPreferences = UserPreferences()
) {
    val fullName: String
        get() = "$firstName $lastName".trim()

    val initials: String
        get() = "${firstName.take(1)}${lastName.take(1)}".uppercase()
}

/**
 * An account where the user receives payouts from the group.
 * Examples: Bank account, Mobile Money Wallet.
 */
data class PaymentAccount(
    val id: String,
    val providerName: String, // e.g. "MTN Mobile Money", "Stanbic Bank"
    val accountName: String, // e.g. "Joseph Doe"
    val accountNumber: String, // Account or Phone number
    val type: PaymentMethod,
    val isVerified: Boolean = false
) {
    val maskedAccountNumber: String
        get() {
            if (accountNumber.length <= 4) return accountNumber
            return "•••• " + accountNumber.takeLast(4)
        }
}

/**
 * A method the user uses to make payments into the group.
 */
data class SavedPaymentMethod(
    val id: String,
    val providerName: String, // e.g. "Airtel Money", "Visa"
    val methodType: PaymentMethod,
    val identifier: String, // The phone number or masked card number
    val isDefault: Boolean = false
)

/**
 * Information about the user's referrals and earnings.
 */
data class InviteAndEarn(
    val referralCode: String = "",
    val referredByCode: String? = null,
    val totalReferrals: Int = 0,
    val totalEarned: Long = 0L
)

data class UserPreferences(
    val notificationsMuted: Boolean = false,
    val language: String = "en",
    val theme: String = "system",
    val autoPayEnabled: Boolean = false
)

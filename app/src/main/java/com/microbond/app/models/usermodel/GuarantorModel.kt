package com.microbond.app.models.usermodel

import java.util.UUID

/**
 * Represents a guarantee relationship where one user (Guarantor) 
 * backs another user (Borrower/Member) within a group or transaction.
 */
data class GuarantorModel(
    val guaranteeId: String = UUID.randomUUID().toString(),
    val groupId: String,                     // Associated savings/welfare group
    val borrowerUserId: String,              // User receiving the guarantee
    val guarantorUserId: String,             // User providing the guarantee
    val guaranteeType: GuaranteeType,
    
    // Financial Exposure
    val maxGuaranteedAmount: Double,         // Maximum amount the guarantor agrees to cover
    val currentLiableAmount: Double = 0.0,   // Active liability if borrower defaults
    val currency: String = "UGX",
    
    // Status & Approval Workflow
    val status: GuaranteeStatus = GuaranteeStatus.PENDING,
    val rejectionReason: String? = null,
    
    // Verification / Consent Timestamps
    val requestedAt: Long = System.currentTimeMillis(),
    val respondedAt: Long? = null,
    val expiresAt: Long? = null
)

enum class GuaranteeType {
    GROUP_ENTRY_VOUCH,     // Vouching for a user to join an exclusive group
    LOAN_BACKING,          // Backing a direct loan or Merry-Go-Round payout advance
    ROTATIONAL_PLEDGE,     // Guaranteeing monthly/weekly contributions in a Merry-Go-Round
    BILL_SPLIT_COVER       // Agreeing to cover unpaid split shares
}

enum class GuaranteeStatus {
    PENDING,        // Request sent to guarantor, awaiting approval
    ACCEPTED,       // Guarantor accepted liability
    DECLINED,       // Guarantor refused
    REVOKED,        // Borrower or group admin cancelled request
    DISCHARGED,     // Obligation fulfilled successfully without default
    INVOKED         // Borrower defaulted; guarantor funds used to cover debt
}

package com.microbond.app.models.usermodel

import java.util.UUID

/**
 * Represents the complete KYC profile for a user.
 * Supports both traditional ID document verification and SIM card / Mobile Money registration.
 */
data class KycModel(
    val kycId: String = UUID.randomUUID().toString(),
    val userId: String,
    val currentTier: KycTier = KycTier.TIER_0_UNVERIFIED,
    
    // SIM Card / Mobile Money Verification Details
    val simVerification: SimVerificationData? = null,
    
    // Government Document Details (Optional for lower tiers)
    val documentVerification: DocumentVerificationData? = null,
    
    val overallStatus: KycStatus = KycStatus.UNVERIFIED,
    val rejectionReason: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Tiered access system to grant higher financial/transaction limits based on available verification.
 */
enum class KycTier {
    TIER_0_UNVERIFIED,     // Read-only / minimal features
    TIER_1_SIM_VERIFIED,   // SIM/Mobile Money verified (Low transaction limits, e.g., daily peer transfers)
    TIER_2_ID_VERIFIED,    // Formal National ID/Passport verified (High limits, group creation)
    TIER_3_FULL_KYC        // ID + Selfie/Face match + Proof of Address (Unrestricted)
}

enum class KycStatus {
    UNVERIFIED,
    PENDING_REVIEW,
    VERIFIED,
    REJECTED,
    EXPIRED
}

/**
 * SIM Card & Telecom-based identity model for users without formal government ID documents.
 */
data class SimVerificationData(
    val msisdn: String,                      // Verified phone number
    val telecomProvider: TelecomCarrier,    // e.g., MTN, AIRTEL
    val registeredNameOnSim: String? = null, // Name returned by Mobile Money API lookup
    val isMobileMoneyActive: Boolean = false,
    val isSimNameMatchedWithProfile: Boolean = false, // True if MM name matches display name
    val simVerifiedAt: Long? = null
)

enum class TelecomCarrier {
    MTN,
    AIRTEL,
    OTHER
}

/**
 * Formal government document verification model.
 */
data class DocumentVerificationData(
    val documentType: DocumentType,
    val documentNumber: String,               // e.g., National Identification Number (NIN)
    val fullNameOnId: String? = null,
    val dateOfBirth: String? = null,          // YYYY-MM-DD
    val frontImagePath: String? = null,       // Local URI or remote URL
    val backImagePath: String? = null,
    val selfieImagePath: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val verifiedAt: Long? = null,
    val expiryDate: Long? = null
)

enum class DocumentType {
    NATIONAL_ID,
    PASSPORT,
    DRIVING_LICENSE,
    VOTER_CARD,
    REFUGEE_ID
}

data class KycDocumentModel(
    val kycId: String = UUID.randomUUID().toString(),
    val userId: String, // Foreign key linking back to UserModel
    val documentType: DocumentType,
    val documentNumber: String, // e.g., NIN (National Identification Number) or Passport Number
    val frontImagePath: String, // Local URI or Cloud URL
    val backImagePath: String? = null, // Passports may only need one, National IDs need two
    val selfieImagePath: String? = null, // Often required to match the face to the ID
    val extractedFullName: String? = null, // Name exactly as it appears on the document
    val status: VerificationStatus = VerificationStatus.PENDING,
    val rejectionReason: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val verifiedAt: Long? = null,
    val expiryDate: Long? = null
)

enum class VerificationStatus {
    PENDING, APPROVED, REJECTED, EXPIRED
}

enum class KycVerificationStatus {
    UNVERIFIED, PENDING_REVIEW, VERIFIED, REJECTED
}

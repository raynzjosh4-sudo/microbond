package com.microbond.app.models.usermodel

import java.util.UUID

/**
 * Encapsulates a user's social trust score, financial reliability, 
 * and community reputation metrics inside Microbond.
 */
data class UserTrustModel(
    val trustId: String = UUID.randomUUID().toString(),
    val userId: String,
    
    // Calculated Score Engine (e.g., 0 to 1000)
    val overallTrustScore: Int = 500,        // Neutral baseline score for new users
    val trustTier: TrustTier = TrustTier.NEUTRAL,
    
    // Historical Track Record
    val completedCyclesCount: Int = 0,       // Successful Merry-Go-Round / Goal cycles finished
    val totalOnTimePayments: Int = 0,        // Total on-time deposits across all groups
    val totalLatePayments: Int = 0,          // Missed deadlines
    val totalDefaults: Int = 0,              // Hard defaults requiring guarantor invocation
    
    // Peer & Guarantor Signals (Social Collateral)
    val vouchedByCount: Int = 0,              // How many verified users guarantee this user
    val activeGuaranteesGivenCount: Int = 0,  // How many other users this person is currently backing
    val successfulGuaranteesCompleted: Int = 0,// How many backed users paid back without default
    
    // System Badges & Endorsements
    val communityBadges: List<CommunityBadge> = emptyList(),
    val isBlacklisted: Boolean = false,
    
    val lastCalculatedAt: Long = System.currentTimeMillis()
)

enum class TrustTier {
    HIGH_RISK,      // Multiple defaults or late deposits (< 300)
    NEUTRAL,        // New or casual user (300 - 599)
    RELIABLE,       // Consistent on-time group payouts (600 - 799)
    COMMUNITY_STAR  // High social collateral, multiple active guarantees (800+)
}

enum class CommunityBadge {
    EARLY_PAYER,             // Consistently deposits ahead of schedule
    COMMUNITY_ANCHOR,        // Has guaranteed 5+ users successfully
    FOUNDING_GROUP_ADMIN,    // Manages active groups with 0 default rate
    MOBILE_MONEY_VERIFIED    // MoMo identity matches profile details
}

package com.microbond.app.core.network

import com.google.gson.annotations.SerializedName

/**
 * Model representing a contact friend who belongs to a discoverable group.
 */
data class ContactMember(
    @SerializedName("user_id", alternate = ["id"]) val id: String = "",
    @SerializedName("profile_name") val profileName: String = "",
    @SerializedName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("avatar_color") val avatarColor: String? = null
)

/**
 * Model representing a group in the discovery feed that mutual contacts belong to.
 */
data class DiscoverableContactGroup(
    @SerializedName("group_id", alternate = ["id"]) val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("group_image_url") val groupImageUrl: String? = null,
    @SerializedName("mutual_contacts_count") val mutualContactsCount: Int = 1,
    @SerializedName("contact_members") val contactMembers: List<ContactMember> = emptyList(),
    @SerializedName("type") val type: String = "ROTATING",
    @SerializedName("member_count") val memberCount: Int = 1,
    @SerializedName("max_members") val maxMembers: Int = 20,
    @SerializedName("currency") val currency: String = "UGX",
    @SerializedName("contribution_amount") val contributionAmount: Double? = null,
    @SerializedName("contribution_frequency") val contributionFrequency: String? = null,
    @SerializedName("payment_mode") val paymentMode: String? = null,
    @SerializedName("requires_admin_approval") val requiresAdminApproval: Boolean = false,
    @SerializedName("require_guarantors") val requireGuarantors: Boolean = false,
    @SerializedName("required_kyc_tier") val requiredKycTier: String? = null,
    @SerializedName("minimum_trust_score") val minimumTrustScore: Int? = null,

    // Real dynamic parameters from backend config tables:
    @SerializedName("waiting_period_days") val waitingPeriodDays: Int? = null,
    @SerializedName("max_claim_amount") val maxClaimAmount: Double? = null,
    @SerializedName("lock_in_period_days") val lockInPeriodDays: Int? = null,
    @SerializedName("share_unit_price") val shareUnitPrice: Double? = null,
    @SerializedName("target_amount") val targetAmount: Double? = null,
    @SerializedName("target_date") val targetDate: Long? = null,
    @SerializedName("is_locked") val isLocked: Boolean? = null,
    @SerializedName("cycle_duration_days") val cycleDurationDays: Int? = null,
    @SerializedName("enable_prorated_dues") val enableProratedDues: Boolean? = null,
    @SerializedName("split_mode") val splitMode: String? = null,
    @SerializedName("bill_category") val billCategory: String? = null,
    @SerializedName("settlement_deadline") val settlementDeadline: Long? = null,
    @SerializedName("event_date") val eventDate: Long? = null,
    @SerializedName("event_target_budget") val eventTargetBudget: Double? = null,
    @SerializedName("excess_funds_action") val excessFundsAction: String? = null,
    @SerializedName("replenishment_mode") val replenishmentMode: String? = null,
    @SerializedName("spending_signoff_policy") val spendingSignoffPolicy: String? = null,
    @SerializedName("campaign_deadline") val campaignDeadline: Long? = null,
    @SerializedName("beneficiary_name") val beneficiaryName: String? = null
) {
    val groupId: String get() = id
}

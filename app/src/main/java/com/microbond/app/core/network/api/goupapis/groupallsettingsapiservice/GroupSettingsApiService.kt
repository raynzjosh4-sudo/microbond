package com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice

/**
 * Aggregated Group Settings API Service.
 *
 * Inherits all modular settings domain sub-interfaces:
 * - [GroupPrivacyApiService]: Privacy, member total savings, discoverability.
 * - [GoalControlsApiService]: Goal locking, leaderboard visibility.
 * - [GroupGovernanceApiService]: Multi-sig roster, approval quorum, my-authorization, voting requirements.
 * - [GroupAuditApiService]: Active audit status, audit report delivery & compliance statements.
 * - [GroupFinancialApiService]: Financial settings, timelock disbursals, escrow lock, pending disbursals queue & circuit breaker.
 *
 * Note: Never append new settings endpoints directly to this file.
 * Always create or extend the relevant domain sub-file in this directory.
 */
interface GroupSettingsApiService :
    GroupPrivacyApiService,
    GoalControlsApiService,
    GroupGovernanceApiService,
    GroupAuditApiService,
    GroupFinancialApiService,
    GroupTrustApiService


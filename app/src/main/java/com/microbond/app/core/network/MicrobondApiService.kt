package com.microbond.app.core.network

import com.microbond.app.core.network.api.ContactsApiService
import com.microbond.app.core.network.api.authenticationapi.*
import com.microbond.app.core.network.api.goupapis.*
import com.microbond.app.core.network.api.goupapis.chatapiservice.*
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.*
import com.microbond.app.core.network.api.goupapis.rotatinggroupapiservice.*
import com.microbond.app.core.network.api.userapiservice.*

/**
 * Backward-compatible facade for [com.microbond.app.core.network.api.MicrobondApiService].
 *
 * The large monolithic API file has been broken down into modular domain interfaces located in
 * `com.microbond.app.core.network.api`:
 * - [AuthApiService]: Authentication, OTP, PIN management, account recovery, KYC NIN, logout.
 * - [UserApiService]: Profile, avatar upload, account deletion, privacy, linked payout accounts.
 * - [ContactsApiService]: Contact synchronization and circle discovery from address book.
 * - [GroupsApiService]: Group summaries, unread count, creation wizard, permissions, dues.
 * - [RotatingApiService]: Rotating ROSCA group settings, deposit schedules, rotation members, swaps.
 */
typealias MicrobondApiService = com.microbond.app.core.network.api.MicrobondApiService

// ── Financial Obligations ───────────────────────────────────────────────────
typealias RemoteObligation = com.microbond.app.core.network.api.goupapis.RemoteObligation

// ── Contact & Discovery Models ──────────────────────────────────────────────
typealias ContactSyncRequest = com.microbond.app.core.network.api.ContactSyncRequest
typealias SharedGroupInfo = com.microbond.app.core.network.api.SharedGroupInfo
typealias SyncedContactUser = com.microbond.app.core.network.api.SyncedContactUser
typealias MatchedContact = com.microbond.app.core.network.api.MatchedContact
typealias ContactSyncResponse = com.microbond.app.core.network.api.ContactSyncResponse

// ── Group Dashboard & Message Models ────────────────────────────────────────
typealias GroupJoinRequest = com.microbond.app.core.network.api.goupapis.GroupJoinRequest
typealias GroupJoinResponse = com.microbond.app.core.network.api.goupapis.GroupJoinResponse
typealias LastMessageSummaryDto = com.microbond.app.core.network.api.goupapis.LastMessageSummaryDto
typealias RingStatus = com.microbond.app.core.network.api.goupapis.RingStatus
typealias ActionMessage = com.microbond.app.core.network.api.goupapis.ActionMessage
typealias GroupListSummaryResponse = com.microbond.app.core.network.api.goupapis.GroupListSummaryResponse
typealias TotalUnreadCountResponse = com.microbond.app.core.network.api.goupapis.TotalUnreadCountResponse

// ── User Profile & Privacy Models ───────────────────────────────────────────
typealias UpdateUserProfileRequest = com.microbond.app.core.network.api.userapiservice.UpdateUserProfileRequest
typealias UserProfileData = com.microbond.app.core.network.api.userapiservice.UserProfileData
typealias UserProfileResponse = com.microbond.app.core.network.api.userapiservice.UserProfileResponse
typealias DeleteAccountRequest = com.microbond.app.core.network.api.userapiservice.DeleteAccountRequest
typealias DeleteAccountResponse = com.microbond.app.core.network.api.userapiservice.DeleteAccountResponse
typealias UserPrivacySettings = com.microbond.app.core.network.api.userapiservice.UserPrivacySettings
typealias UpdatePrivacySettingsRequest = com.microbond.app.core.network.api.userapiservice.UpdatePrivacySettingsRequest
typealias LinkedAccountResponse = com.microbond.app.core.network.api.userapiservice.LinkedAccountResponse
typealias CreateLinkedAccountRequest = com.microbond.app.core.network.api.userapiservice.CreateLinkedAccountRequest
typealias DeleteLinkedAccountResponse = com.microbond.app.core.network.api.userapiservice.DeleteLinkedAccountResponse

// ── Auth, PIN & Recovery Models ─────────────────────────────────────────────
typealias OtpRequestPayload = com.microbond.app.core.network.api.authenticationapi.OtpRequestPayload
typealias OtpRequestResponse = com.microbond.app.core.network.api.authenticationapi.OtpRequestResponse
typealias OtpVerifyPayload = com.microbond.app.core.network.api.authenticationapi.OtpVerifyPayload
typealias OtpVerifyResponse = com.microbond.app.core.network.api.authenticationapi.OtpVerifyResponse
typealias SetAppPinPayload = com.microbond.app.core.network.api.authenticationapi.SetAppPinPayload
typealias SetAppPinResponse = com.microbond.app.core.network.api.authenticationapi.SetAppPinResponse
typealias VerifyAppPinRequest = com.microbond.app.core.network.api.authenticationapi.VerifyAppPinRequest
typealias VerifyAppPinResponse = com.microbond.app.core.network.api.authenticationapi.VerifyAppPinResponse
typealias ChangeAppPinRequest = com.microbond.app.core.network.api.authenticationapi.ChangeAppPinRequest
typealias ChangeAppPinResponse = com.microbond.app.core.network.api.authenticationapi.ChangeAppPinResponse
typealias RequestForgotPinOTPRequest = com.microbond.app.core.network.api.authenticationapi.RequestForgotPinOTPRequest
typealias RequestForgotPinOTPResponse = com.microbond.app.core.network.api.authenticationapi.RequestForgotPinOTPResponse
typealias VerifyForgotPinOTPRequest = com.microbond.app.core.network.api.authenticationapi.VerifyForgotPinOTPRequest
typealias VerifyForgotPinOTPResponse = com.microbond.app.core.network.api.authenticationapi.VerifyForgotPinOTPResponse
typealias RequestRecoveryOTPRequest = com.microbond.app.core.network.api.authenticationapi.RequestRecoveryOTPRequest
typealias RequestRecoveryOTPResponse = com.microbond.app.core.network.api.authenticationapi.RequestRecoveryOTPResponse
typealias VerifyRecoveryOTPRequest = com.microbond.app.core.network.api.authenticationapi.VerifyRecoveryOTPRequest
typealias VerifyRecoveryOTPResponse = com.microbond.app.core.network.api.authenticationapi.VerifyRecoveryOTPResponse
typealias RequestChangePhoneOTPRequest = com.microbond.app.core.network.api.authenticationapi.RequestChangePhoneOTPRequest
typealias RequestChangePhoneOTPResponse = com.microbond.app.core.network.api.authenticationapi.RequestChangePhoneOTPResponse
typealias VerifyChangePhoneOTPRequest = com.microbond.app.core.network.api.authenticationapi.VerifyChangePhoneOTPRequest
typealias VerifyChangePhoneOTPResponse = com.microbond.app.core.network.api.authenticationapi.VerifyChangePhoneOTPResponse
typealias LinkEmailRequest = com.microbond.app.core.network.api.authenticationapi.LinkEmailRequest
typealias LinkEmailResponse = com.microbond.app.core.network.api.authenticationapi.LinkEmailResponse
typealias VerifyEmailRequest = com.microbond.app.core.network.api.authenticationapi.VerifyEmailRequest
typealias VerifyEmailResponse = com.microbond.app.core.network.api.authenticationapi.VerifyEmailResponse
typealias GetBackupEmailResponse = com.microbond.app.core.network.api.authenticationapi.GetBackupEmailResponse
typealias BackupEmailData = com.microbond.app.core.network.api.authenticationapi.BackupEmailData
typealias RequestEmailRecoveryCodeRequest = com.microbond.app.core.network.api.authenticationapi.RequestEmailRecoveryCodeRequest
typealias RequestEmailRecoveryCodeResponse = com.microbond.app.core.network.api.authenticationapi.RequestEmailRecoveryCodeResponse
typealias VerifyEmailRecoveryCodeRequest = com.microbond.app.core.network.api.authenticationapi.VerifyEmailRecoveryCodeRequest
typealias VerifyEmailRecoveryCodeResponse = com.microbond.app.core.network.api.authenticationapi.VerifyEmailRecoveryCodeResponse
typealias RequestPhoneRecoveryOtpRequest = com.microbond.app.core.network.api.authenticationapi.RequestPhoneRecoveryOtpRequest
typealias RequestPhoneRecoveryOtpResponse = com.microbond.app.core.network.api.authenticationapi.RequestPhoneRecoveryOtpResponse
typealias ConfirmPhoneRecoveryRequest = com.microbond.app.core.network.api.authenticationapi.ConfirmPhoneRecoveryRequest
typealias ConfirmPhoneRecoveryResponse = com.microbond.app.core.network.api.authenticationapi.ConfirmPhoneRecoveryResponse
typealias LogoutRequest = com.microbond.app.core.network.api.authenticationapi.LogoutRequest
typealias LogoutResponse = com.microbond.app.core.network.api.authenticationapi.LogoutResponse
typealias NINSubmissionRequest = com.microbond.app.core.network.api.authenticationapi.NINSubmissionRequest
typealias NINVerificationResponse = com.microbond.app.core.network.api.authenticationapi.NINVerificationResponse

// ── Group Creation Models ───────────────────────────────────────────────────
typealias GroupTypeStatus = com.microbond.app.core.network.api.goupapis.GroupTypeStatus
typealias AllowedGroupsResponse = com.microbond.app.core.network.api.goupapis.AllowedGroupsResponse
typealias UnifiedGroupCreateRequest = com.microbond.app.core.network.api.goupapis.UnifiedGroupCreateRequest
typealias GroupCreatedData = com.microbond.app.core.network.api.goupapis.GroupCreatedData
typealias GroupCreateResponse = com.microbond.app.core.network.api.goupapis.GroupCreateResponse

// ── Rotating Group Models ───────────────────────────────────────────────────
typealias RotatingGroupSettingsSummary = com.microbond.app.core.network.api.goupapis.rotatinggroupapiservice.RotatingGroupSettingsSummary
typealias GroupSettingsSummaryResponse = com.microbond.app.core.network.api.goupapis.rotatinggroupapiservice.GroupSettingsSummaryResponse
typealias RotationSlotMember = com.microbond.app.core.network.api.goupapis.rotatinggroupapiservice.RotationSlotMember
typealias RotatingDepositInfoResponse = com.microbond.app.core.network.api.goupapis.rotatinggroupapiservice.RotatingDepositInfoResponse
typealias TurnSwapRequest = com.microbond.app.core.network.api.goupapis.rotatinggroupapiservice.TurnSwapRequest
typealias TurnSwapResponse = com.microbond.app.core.network.api.goupapis.rotatinggroupapiservice.TurnSwapResponse

// ── Group Chat Models ───────────────────────────────────────────────────────
typealias GroupChatApiService = com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatApiService
typealias GroupChatMessagesResponse = com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatMessagesResponse
typealias GroupChatMessageDto = com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatMessageDto
typealias SendMessageRequest = com.microbond.app.core.network.api.goupapis.chatapiservice.SendMessageRequest
typealias SendMessageResponse = com.microbond.app.core.network.api.goupapis.chatapiservice.SendMessageResponse

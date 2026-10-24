package com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// ── Audit Models ───────────────────────────────────────────────────────────

data class AuditActionResponse(
    val status: String = "success",
    val message: String? = null,
    val data: GovernanceSettingsData? = null
)

data class AuditDeliverySettingsData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("enable_automated_audit_dispatch") val enableAutomatedAuditDispatch: Boolean = false,
    @SerializedName("audit_report_recipient_email") val auditReportRecipientEmail: String? = null,
    @SerializedName("dispatch_monthly_compliance_statement") val dispatchMonthlyComplianceStatement: Boolean = false,
    @SerializedName("export_file_format") val exportFileFormat: String = "PDF",
    @SerializedName("default_admin_email") val defaultAdminEmail: String? = null,
    @SerializedName("available_formats") val availableFormats: List<String> = listOf("PDF", "CSV")
)

data class AuditDeliverySettingsResponse(
    val status: String = "success",
    val message: String? = null,
    val data: AuditDeliverySettingsData? = null
)

data class UpdateAuditDeliverySettingsRequest(
    @SerializedName("enable_automated_audit_dispatch") val enableAutomatedAuditDispatch: Boolean? = null,
    @SerializedName("audit_report_recipient_email") val auditReportRecipientEmail: String? = null,
    @SerializedName("dispatch_monthly_compliance_statement") val dispatchMonthlyComplianceStatement: Boolean? = null,
    @SerializedName("export_file_format") val exportFileFormat: String? = null
)

data class ExportFinancialReportRequest(
    @SerializedName("export_format") val exportFormat: String = "PDF",
    @SerializedName("recipient_email") val recipientEmail: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null
)

data class AuditDeliveryActionResponse(
    val status: String = "success",
    val message: String? = null
)

// ── Group Audit API Service Interface ──────────────────────────────────────

interface GroupAuditApiService {

    @POST("api/v1/groups/{group_id}/audit/start")
    suspend fun startAudit(
        @Path("group_id") groupId: String
    ): Response<AuditActionResponse>

    @POST("api/v1/groups/{group_id}/audit/complete")
    suspend fun completeAudit(
        @Path("group_id") groupId: String
    ): Response<AuditActionResponse>

    // ── Dedicated Audit Report Delivery Settings ──────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/audit-report-delivery")
    suspend fun getAuditReportDeliverySettings(
        @Path("group_id") groupId: String
    ): Response<AuditDeliverySettingsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/audit-report-delivery")
    suspend fun updateAuditReportDeliverySettings(
        @Path("group_id") groupId: String,
        @Body payload: UpdateAuditDeliverySettingsRequest
    ): Response<AuditDeliverySettingsResponse>

    @POST("api/v1/groups/{group_id}/financial-reports/export-and-email")
    suspend fun exportAndEmailFinancialReport(
        @Path("group_id") groupId: String,
        @Body payload: ExportFinancialReportRequest
    ): Response<AuditDeliveryActionResponse>
}

package com.microbond.app.core.network.api.deposits

import android.util.Log
import com.microbond.app.core.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Repository responsible for calling the Microbond Daily Deposit Widget APIs:
 * - GET /api/v1/groups/{groupId}/deposits/daily-widget
 * - GET /api/v1/groups/{groupId}/deposits/daily-widget/members
 * - POST /api/v1/groups/{groupId}/deposits/daily-widget/nudge
 * - PATCH /api/v1/groups/{groupId}/settings/deposit-widget-visibility
 */
object DepositWidgetRepository {

    private const val TAG = "DepositWidgetRepo"

    // Set of userIds nudged in this session to provide instant UI feedback
    private val _nudgedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val nudgedUserIds: StateFlow<Set<String>> = _nudgedUserIds.asStateFlow()

    /**
     * Fetch daily summary with counts for full, partial, and not deposited pills.
     */
    suspend fun fetchDailyWidget(
        groupId: String,
        dateMs: Long? = null
    ): Result<DailyDepositWidgetData> = withContext(Dispatchers.IO) {
        if (groupId.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Group ID must not be blank"))
        }
        try {
            val response = NetworkClient.apiService.getDailyDepositWidget(groupId, dateMs)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Widget fetch failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching daily deposit widget for group $groupId", e)
            Result.failure(e)
        }
    }

    /**
     * Fetch the list of members for drill-down by status filter.
     */
    suspend fun fetchMembers(
        groupId: String,
        status: DepositStatusFilter = DepositStatusFilter.ALL,
        dateMs: Long? = null
    ): Result<List<DepositMemberItem>> = withContext(Dispatchers.IO) {
        if (groupId.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Group ID must not be blank"))
        }
        try {
            val response = NetworkClient.apiService.getDepositWidgetMembers(
                groupId = groupId,
                status = status.apiKey,
                dateMs = dateMs
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data.members)
            } else {
                Result.failure(Exception("Members fetch failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching drill-down members for group $groupId, status=$status", e)
            Result.failure(e)
        }
    }

    /**
     * Send a nudge / reminder to an unpaid or partially paid member.
     */
    suspend fun nudgeMember(
        groupId: String,
        targetUserId: String,
        customMessage: String? = null
    ): Result<NudgeMemberResponse> = withContext(Dispatchers.IO) {
        if (groupId.isBlank() || targetUserId.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Group ID and Target User ID required"))
        }
        try {
            val request = NudgeMemberRequest(
                targetUserId = targetUserId,
                customMessage = customMessage
            )
            val response = NetworkClient.apiService.nudgeMember(groupId, request)
            if (response.isSuccessful && response.body() != null) {
                // Record locally as nudged
                _nudgedUserIds.value = _nudgedUserIds.value + targetUserId
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Nudge failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error nudging user $targetUserId in group $groupId", e)
            Result.failure(e)
        }
    }

    /**
     * Update admin widget visibility settings for chat.
     */
    suspend fun updateVisibility(
        groupId: String,
        visibility: DepositWidgetVisibility
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        if (groupId.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Group ID required"))
        }
        try {
            val request = UpdateDepositWidgetVisibilityRequest(visibility = visibility.apiKey)
            val response = NetworkClient.apiService.updateDepositWidgetVisibility(groupId, request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Visibility update failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating deposit widget visibility in group $groupId", e)
            Result.failure(e)
        }
    }

    /**
     * Clear local nudged state cache (e.g. on group change or day refresh).
     */
    fun clearLocalNudges() {
        _nudgedUserIds.value = emptySet()
    }
}

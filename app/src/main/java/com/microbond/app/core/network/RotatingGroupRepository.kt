package com.microbond.app.core.network

import android.util.Log
import com.microbond.app.core.components.groupintrocard.rotating.RotatingGroupPreviewUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository responsible for fetching rotating group settings summary and live turn roster
 * from the Microbond API:
 * - GET /api/v1/groups/{group_id}/settings/summary
 * - GET /api/v1/groups/{group_id}/members
 */
class RotatingGroupRepository(
    private val apiService: MicrobondApiService = NetworkClient.apiService
) {
    /**
     * Fetches the consolidated group settings summary and member turn roster for the given group,
     * converting raw API payloads into a fully populated [RotatingGroupPreviewUiState].
     */
    suspend fun fetchRotatingGroupDetails(groupId: String): Result<RotatingGroupPreviewUiState> = withContext(Dispatchers.IO) {
        if (groupId.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Group ID must not be blank"))
        }

        try {
            val summaryResponse = apiService.getGroupSettingsSummary(groupId)
            if (!summaryResponse.isSuccessful || summaryResponse.body() == null) {
                return@withContext Result.failure(
                    Exception("Failed to fetch group summary: HTTP ${summaryResponse.code()} ${summaryResponse.message()}")
                )
            }

            val apiData = summaryResponse.body()!!

            // Fetch live turn roster members if available
            val members = try {
                val membersResponse = apiService.getRotatingGroupMembers(groupId)
                if (membersResponse.isSuccessful) membersResponse.body() else null
            } catch (e: Exception) {
                Log.w("RotatingGroupRepo", "Failed to fetch members for group $groupId: ${e.message}")
                null
            }

            val uiState = RotatingGroupPreviewUiState.fromApiData(
                data = apiData,
                members = members
            )
            Result.success(uiState)
        } catch (e: Exception) {
            Log.e("RotatingGroupRepo", "Error loading rotating group summary for $groupId", e)
            Result.failure(e)
        }
    }
}

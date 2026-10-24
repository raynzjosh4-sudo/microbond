package com.microbond.app.feature_groups.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.network.GroupCreatedData
import com.microbond.app.core.network.GroupTypeStatus
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.UnifiedGroupCreateRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages group creation permissions, progressive unlock verification,
 * and unified transactional group provisioning via Microbond Backend APIs.
 */
object GroupCreationManager {

    private val _allowedGroups = MutableStateFlow<Set<String>>(emptySet())
    val allowedGroups: StateFlow<Set<String>> = _allowedGroups.asStateFlow()

    private val _groupReasons = MutableStateFlow<Map<String, String>>(emptyMap())
    val groupReasons: StateFlow<Map<String, String>> = _groupReasons.asStateFlow()

    private val _groupStatuses = MutableStateFlow<List<GroupTypeStatus>>(emptyList())
    val groupStatuses: StateFlow<List<GroupTypeStatus>> = _groupStatuses.asStateFlow()

    private val _hasFetchedAllowed = MutableStateFlow(false)
    val hasFetchedAllowed: StateFlow<Boolean> = _hasFetchedAllowed.asStateFlow()

    private val _isLoadingAllowedGroups = MutableStateFlow(false)
    val isLoadingAllowedGroups: StateFlow<Boolean> = _isLoadingAllowedGroups.asStateFlow()

    private val _allowedGroupsError = MutableStateFlow<String?>(null)
    val allowedGroupsError: StateFlow<String?> = _allowedGroupsError.asStateFlow()

    private val _isCreatingGroup = MutableStateFlow(false)
    val isCreatingGroup: StateFlow<Boolean> = _isCreatingGroup.asStateFlow()

    private val _creationError = MutableStateFlow<String?>(null)
    val creationError: StateFlow<String?> = _creationError.asStateFlow()

    private val _lastCreatedGroup = MutableStateFlow<GroupCreatedData?>(null)
    val lastCreatedGroup: StateFlow<GroupCreatedData?> = _lastCreatedGroup.asStateFlow()

    private val gson = Gson()

    /**
     * Calls GET /api/v1/users/me/allowed-groups to evaluate the user's KYC tier,
     * Trust Score, and group health history against Microbond's progressive unlock matrix.
     */
    suspend fun fetchAllowedGroups(forceRefresh: Boolean = false): Boolean {
        if (_hasFetchedAllowed.value && !forceRefresh && _allowedGroups.value.isNotEmpty()) {
            return true
        }

        _isLoadingAllowedGroups.value = true
        _allowedGroupsError.value = null

        return try {
            val response = NetworkClient.apiService.getAllowedGroups()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                _allowedGroups.value = body.allowedGroups.map { it.uppercase().trim() }.toSet()
                _groupReasons.value = body.reasons.mapKeys { it.key.uppercase().trim() }
                _groupStatuses.value = body.groups
                _hasFetchedAllowed.value = true
                true
            } else {
                val errorMsg = extractErrorMessage(response.errorBody()?.string())
                    ?: "Failed to load group creation permissions (${response.code()})"
                _allowedGroupsError.value = errorMsg
                false
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                currentCoroutineContext().ensureActive()
            }
            _allowedGroupsError.value = e.localizedMessage ?: "Network error checking allowed groups"
            false
        } finally {
            _isLoadingAllowedGroups.value = false
        }
    }

    /**
     * Checks if a given SavingsGroupType is currently permitted for the user.
     * If the server hasn't been fetched yet, standard tiers default to true.
     */
    fun isGroupTypeAllowed(type: SavingsGroupType): Boolean {
        val typeKey = type.name.uppercase()
        if (_hasFetchedAllowed.value) {
            return _allowedGroups.value.contains(typeKey)
        }
        // Conservative fallback before first API response arrives
        return when (type) {
            SavingsGroupType.GOAL_SAVINGS,
            SavingsGroupType.SHARED_WALLET,
            SavingsGroupType.WELFARE_FUND,
            SavingsGroupType.EVENT_COMMITTEE -> true
            else -> false
        }
    }

    /**
     * Retrieves the specific unlock requirement or reason for a group archetype.
     */
    fun getDisallowedReason(type: SavingsGroupType): String {
        val typeKey = type.name.uppercase()
        val customReason = _groupReasons.value[typeKey]
        if (!customReason.isNullOrBlank()) {
            return customReason
        }
        return when (type) {
            SavingsGroupType.MERRY_GO_ROUND ->
                "Requires KYC Tier 3 (Biometric / Proof of Address) and Trust Score of at least 50."
            SavingsGroupType.ASSOCIATION_DUES ->
                "Requires an established communal track record and KYC Tier 2 verification."
            SavingsGroupType.BILL_SPLIT ->
                "Requires active communal membership with Group Health Grade A or higher."
            SavingsGroupType.INVESTMENT_CLUB ->
                "Requires KYC Tier 2 National ID verification."
            SavingsGroupType.FUNDRAISER ->
                "Requires KYC Tier 2 National ID verification."
            else -> "Verification required to unlock this group archetype."
        }
    }

    /**
     * Submits POST /api/v1/groups/create with the unified transactional creation request.
     */
    suspend fun createGroup(
        name: String,
        type: SavingsGroupType,
        description: String = "",
        groupImageUrl: String? = null,
        paymentMode: String = "OBLIGATION",
        visibility: String = "PRIVATE",
        currency: String = "UGX",
        maxMembers: Int? = null,
        enableLateFees: Boolean = false,
        requiresAdminApproval: Boolean = true
    ): Result<GroupCreatedData> {
        _isCreatingGroup.value = true
        _creationError.value = null

        return try {
            val payload = UnifiedGroupCreateRequest(
                name = name.trim(),
                description = description.trim().ifEmpty { null },
                groupImageUrl = groupImageUrl?.trim()?.ifEmpty { null },
                type = type.name,
                paymentMode = paymentMode,
                visibility = visibility,
                currency = currency,
                maxMembers = maxMembers,
                enableLateFees = enableLateFees,
                requiresAdminApproval = requiresAdminApproval
            )

            val response = NetworkClient.apiService.createGroup(payload)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val createdData = body.data ?: GroupCreatedData(
                    groupId = "",
                    inviteCode = "",
                    name = name.trim()
                )
                _lastCreatedGroup.value = createdData
                Result.success(createdData)
            } else {
                val errorMsg = extractErrorMessage(response.errorBody()?.string())
                    ?: "Failed to create group (${response.code()})"
                _creationError.value = errorMsg
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Unexpected error during group creation"
            _creationError.value = errorMsg
            Result.failure(e)
        } finally {
            _isCreatingGroup.value = false
        }
    }

    fun clearErrors() {
        _creationError.value = null
        _allowedGroupsError.value = null
    }

    private fun extractErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val json = gson.fromJson(errorBody, JsonObject::class.java)
            if (json.has("detail")) {
                val detail = json.get("detail")
                if (detail.isJsonPrimitive) {
                    detail.asString
                } else if (detail.isJsonObject && detail.asJsonObject.has("message")) {
                    detail.asJsonObject.get("message").asString
                } else {
                    detail.toString()
                }
            } else if (json.has("message")) {
                json.get("message").asString
            } else {
                null
            }
        } catch (_: Exception) {
            errorBody
        }
    }
}

package com.microbond.app.feature_you.ui.linkedaccounts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.PhoneAndroid
import com.microbond.app.core.network.CreateLinkedAccountRequest
import com.microbond.app.core.network.LinkedAccountResponse
import com.microbond.app.core.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

/**
 * Manages linked payout destinations and payment sources for deposits and withdrawals,
 * communicating with GET, POST, and DELETE /api/v1/users/me/payout-destinations/.
 */
object LinkedAccountsManager {
    private val _accounts = MutableStateFlow<List<LinkedAccount>>(emptyList())
    val accounts: StateFlow<List<LinkedAccount>> = _accounts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    suspend fun fetchLinkedAccounts(): Boolean {
        _isLoading.value = true
        _errorMessage.value = null
        return try {
            val response = NetworkClient.apiService.getLinkedAccounts()
            if (response.isSuccessful && response.body() != null) {
                _accounts.value = response.body()!!.map { it.toUiModel() }
                true
            } else {
                _errorMessage.value = "Failed to load linked accounts (${response.code()})"
                false
            }
        } catch (e: Exception) {
            _errorMessage.value = e.localizedMessage ?: "Failed to connect to server"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addAccount(
        providerName: String,
        accountNumber: String,
        accountType: String? = null,
        bankCode: String? = null,
        isDefault: Boolean = false,
        appPin: String? = null
    ): Result<LinkedAccount> {
        _isLoading.value = true
        _errorMessage.value = null
        return try {
            val payload = CreateLinkedAccountRequest(
                providerName = providerName,
                accountNumber = accountNumber,
                accountType = accountType,
                bankCode = bankCode,
                isDefault = isDefault,
                appPin = appPin
            )
            val response = NetworkClient.apiService.addLinkedAccount(payload)
            try {
                NetworkClient.apiService.addPaymentMethod(payload)
            } catch (_: Exception) {}
            if (response.isSuccessful && response.body() != null) {
                val created = response.body()!!.toUiModel()
                _accounts.value = _accounts.value + created
                Result.success(created)
            } else {
                val rawError = response.errorBody()?.string()
                val message = parseErrorMessage(rawError, "Failed to link account (${response.code()})")
                _errorMessage.value = message
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Network error. Please check your connection."
            _errorMessage.value = msg
            Result.failure(Exception(msg))
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun deleteAccount(destinationId: String): Boolean {
        return try {
            val response = NetworkClient.apiService.deleteLinkedAccount(destinationId)
            if (response.isSuccessful) {
                _accounts.value = _accounts.value.filterNot { it.id == destinationId }
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun deleteAccounts(ids: Set<String>): Int {
        var count = 0
        for (id in ids) {
            if (deleteAccount(id)) {
                count++
            }
        }
        return count
    }

    private fun parseErrorMessage(rawJson: String?, fallback: String): String {
        if (rawJson.isNullOrBlank()) return fallback
        return try {
            val obj = JSONObject(rawJson)
            if (obj.has("detail")) {
                val d = obj.get("detail")
                if (d is String) d else d.toString()
            } else {
                obj.optString("message", fallback)
            }
        } catch (_: Exception) {
            fallback
        }
    }
}

fun LinkedAccountResponse.toUiModel(): LinkedAccount {
    val isBank = accountType.equals("BANK_ACCOUNT", ignoreCase = true)
    return LinkedAccount(
        id = id,
        type = if (isBank) "Bank Account" else "Mobile Money",
        provider = providerName,
        number = maskedNumber.ifBlank { accountNumber },
        registeredName = verifiedName ?: accountName ?: "Verified Account Holder",
        icon = if (isBank) Icons.Default.AccountBalance else Icons.Default.PhoneAndroid,
        isDefault = isDefault,
        isVerified = isVerified
    )
}

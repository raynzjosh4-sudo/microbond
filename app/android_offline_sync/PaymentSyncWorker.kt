package com.microbond.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.microbond.app.core.database.dao.ObligationDao
import com.microbond.app.core.database.dao.OutboxDao
import com.microbond.app.core.database.entities.SyncState
import com.microbond.app.network.MicrobondApiService

class PaymentSyncWorker(
    context: Context,
    params: WorkerParameters,
    private val outboxDao: OutboxDao,
    private val obligationDao: ObligationDao,
    private val apiService: MicrobondApiService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pendingEntries = outboxDao.getPendingEntries()
        
        if (pendingEntries.isEmpty()) {
            return Result.success()
        }

        var hasFailures = false

        for (entry in pendingEntries) {
            try {
                // 1. Attempt to send the queued payment request to the FastAPI backend
                val response = apiService.payObligation(entry.obligationId, entry.payloadJson)
                
                if (response.isSuccessful) {
                    // 2. Success! The server accepted the payment initiation (or webhook finished).
                    // We delete the outbox entry.
                    outboxDao.deleteEntry(entry.id)
                    
                    // The server response should contain the true authoritative state.
                    val remoteObligation = response.body()
                    if (remoteObligation != null) {
                        obligationDao.resolveRemoteConflict(
                            obligationId = entry.obligationId,
                            remoteStatus = remoteObligation.status,
                            remoteAmountPaid = remoteObligation.amountPaid,
                            remoteUpdatedAt = remoteObligation.updatedAt
                        )
                    } else {
                        // If no body returned, we just reset sync state until the next pull sync
                        obligationDao.updateSyncState(entry.obligationId, SyncState.SYNCED)
                    }
                } else {
                    // API rejected it (e.g. 400 Bad Request)
                    hasFailures = true
                    outboxDao.incrementRetry(entry.id)
                }
            } catch (e: Exception) {
                // Network failed mid-flight. WorkManager will retry.
                hasFailures = true
                outboxDao.incrementRetry(entry.id)
            }
        }

        return if (hasFailures) Result.retry() else Result.success()
    }
}

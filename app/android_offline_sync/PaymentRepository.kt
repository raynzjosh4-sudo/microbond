package com.microbond.app.data.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import com.microbond.app.core.database.dao.ObligationDao
import com.microbond.app.core.database.dao.OutboxDao
import com.microbond.app.core.database.entities.SyncOutboxEntry
import com.microbond.app.core.database.entities.SyncState
import com.microbond.app.workers.PaymentSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentRepository(
    private val obligationDao: ObligationDao,
    private val outboxDao: OutboxDao,
    private val workManager: WorkManager
) {
    suspend fun initiatePaymentOffline(obligationId: String, amount: Long, paymentMethod: String) {
        withContext(Dispatchers.IO) {
            // 1. We NEVER update the status to PAID. We only change the SyncState.
            obligationDao.updateSyncState(obligationId, SyncState.PENDING_PAYMENT)
            
            // 2. Queue the exact payload in the Outbox
            val payload = """{"amount": $amount, "method": "$paymentMethod"}"""
            val outboxEntry = SyncOutboxEntry(
                obligationId = obligationId,
                payloadJson = payload,
                actionType = "PAY_OBLIGATION",
                createdAt = System.currentTimeMillis()
            )
            outboxDao.insertOutboxEntry(outboxEntry)
            
            // 3. Enqueue the WorkManager task to run as soon as the network connects
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
                
            val syncWork = OneTimeWorkRequestBuilder<PaymentSyncWorker>()
                .setConstraints(constraints)
                .build()
                
            workManager.enqueue(syncWork)
        }
    }
}

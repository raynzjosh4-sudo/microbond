package com.microbond.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.microbond.app.core.database.entities.LocalObligation
import com.microbond.app.core.database.entities.SyncOutboxEntry
import com.microbond.app.core.database.entities.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface ObligationDao {
    @Query("SELECT * FROM member_obligations WHERE group_id = :groupId")
    fun getObligationsForGroup(groupId: String): Flow<List<LocalObligation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertObligation(obligation: LocalObligation)

    @Query("UPDATE member_obligations SET sync_state = :newState WHERE id = :obligationId")
    suspend fun updateSyncState(obligationId: String, newState: SyncState)
    
    // Conflict resolution: Only overwrite local state if the server's update is newer
    @Query("""
        UPDATE member_obligations 
        SET status = :remoteStatus, amount_paid = :remoteAmountPaid, remote_updated_at = :remoteUpdatedAt, sync_state = 'SYNCED'
        WHERE id = :obligationId AND remote_updated_at < :remoteUpdatedAt
    """)
    suspend fun resolveRemoteConflict(obligationId: String, remoteStatus: String, remoteAmountPaid: Long, remoteUpdatedAt: Long)
}

@Dao
interface OutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutboxEntry(entry: SyncOutboxEntry)

    @Query("SELECT * FROM sync_outbox ORDER BY created_at ASC")
    suspend fun getPendingEntries(): List<SyncOutboxEntry>

    @Query("DELETE FROM sync_outbox WHERE id = :entryId")
    suspend fun deleteEntry(entryId: Long)
    
    @Query("UPDATE sync_outbox SET retry_count = retry_count + 1 WHERE id = :entryId")
    suspend fun incrementRetry(entryId: Long)
}

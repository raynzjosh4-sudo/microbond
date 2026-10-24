package com.microbond.app.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

enum class SyncState {
    SYNCED,
    PENDING_PAYMENT,
    PENDING_UPDATE,
    CONFLICT
}

@Entity(tableName = "member_obligations")
data class LocalObligation(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "group_id") val groupId: String,
    @ColumnInfo(name = "amount_due") val amountDue: Long,
    @ColumnInfo(name = "amount_paid") val amountPaid: Long,
    @ColumnInfo(name = "status") val status: String, // Authoritative: PENDING, LATE, DEFAULTED, PAID
    
    // Sync Metadata
    @ColumnInfo(name = "sync_state") val syncState: SyncState = SyncState.SYNCED,
    @ColumnInfo(name = "remote_updated_at") val remoteUpdatedAt: Long
)

@Entity(tableName = "sync_outbox")
data class SyncOutboxEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "obligation_id") val obligationId: String,
    @ColumnInfo(name = "payload_json") val payloadJson: String, // E.g., {"amount": 50000, "method": "MTN_MOMO"}
    @ColumnInfo(name = "action_type") val actionType: String, // "PAY_OBLIGATION"
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "retry_count") val retryCount: Int = 0
)

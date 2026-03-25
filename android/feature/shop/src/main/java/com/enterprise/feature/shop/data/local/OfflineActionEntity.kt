package com.enterprise.feature.shop.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

/** Room entity for queued offline actions. */
@Entity(tableName = "offline_actions")
data class OfflineActionEntity(
    @PrimaryKey val id: String,
    val actionType: String,
    val payloadJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)

/** DAO for offline action queue. */
@Dao
interface OfflineActionDao {
    @Insert
    suspend fun insert(action: OfflineActionEntity)

    @Query("SELECT * FROM offline_actions WHERE synced = 0 ORDER BY createdAt ASC")
    suspend fun getUnsynced(): List<OfflineActionEntity>

    @Query("UPDATE offline_actions SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM offline_actions WHERE synced = 1")
    suspend fun deleteSynced()
}

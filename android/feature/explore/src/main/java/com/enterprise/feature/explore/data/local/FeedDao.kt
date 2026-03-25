package com.enterprise.feature.explore.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Room DAO for feed items with reactive Flow queries. */
@Dao
interface FeedDao {
    @Query("SELECT * FROM feed_items ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<FeedItemEntity>>

    @Query("SELECT * FROM feed_items ORDER BY sortOrder ASC")
    suspend fun getAll(): List<FeedItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FeedItemEntity>)

    @Query("DELETE FROM feed_items")
    suspend fun deleteAll()

    @Query("SELECT MAX(lastUpdated) FROM feed_items")
    suspend fun getLastUpdatedTimestamp(): Long?
}

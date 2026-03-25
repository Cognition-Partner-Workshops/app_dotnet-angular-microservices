package com.enterprise.feature.explore.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for cached feed items (SSOT pattern).
 * Stores serialized JSON for the component config to remain flexible.
 */
@Entity(tableName = "feed_items")
data class FeedItemEntity(
    @PrimaryKey val id: String,
    val type: String,
    val configJson: String,
    val sortOrder: Int,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /** Returns true if cached data is older than 5 minutes. */
    val isCacheExpired: Boolean
        get() {
            val fiveMinutesMs = 5 * 60 * 1000L
            return System.currentTimeMillis() - lastUpdated > fiveMinutesMs
        }
}

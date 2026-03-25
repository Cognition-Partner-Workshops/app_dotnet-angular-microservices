package com.enterprise.feature.explore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FeedItemEntity::class], version = 1, exportSchema = false)
abstract class FeedDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
}

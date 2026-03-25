package com.enterprise.feature.shop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartItemEntity::class, OfflineActionEntity::class], version = 1, exportSchema = false)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun offlineActionDao(): OfflineActionDao
}

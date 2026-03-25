package com.enterprise.mobile.di

import android.content.Context
import android.content.Intent
import androidx.room.Room
import com.enterprise.core.domain.entities.FeatureFlag
import com.enterprise.core.domain.interfaces.AppPermission
import com.enterprise.core.domain.interfaces.PermissionResult
import com.enterprise.core.domain.interfaces.PermissionService
import com.enterprise.core.domain.interfaces.ShareService
import com.enterprise.core.domain.repositories.AuthRepository
import com.enterprise.core.domain.repositories.CartRepository
import com.enterprise.core.domain.repositories.ChatRepository
import com.enterprise.core.domain.repositories.FeedRepository
import com.enterprise.core.domain.repositories.FeatureFlagRepository
import com.enterprise.core.domain.repositories.RewardsRepository
import com.enterprise.core.network.ApiClient
import com.enterprise.core.network.WebSocketClient
import com.enterprise.feature.aiconnect.data.ChatRepositoryImpl
import com.enterprise.feature.aiconnect.domain.repositories.AIConnectRepository
import com.enterprise.feature.auth.data.AuthRepositoryImpl
import com.enterprise.feature.auth.data.api.AuthApiService
import com.enterprise.feature.auth.domain.repositories.AuthFeatureRepository
import com.enterprise.feature.explore.data.FeedRepositoryImpl
import com.enterprise.feature.explore.data.api.FeedApiService
import com.enterprise.feature.explore.data.local.FeedDao
import com.enterprise.feature.explore.data.local.FeedDatabase
import com.enterprise.feature.explore.domain.repositories.ExploreFeedRepository
import com.enterprise.feature.rewards.data.RewardsRepositoryImpl
import com.enterprise.feature.rewards.data.api.RewardsApiService
import com.enterprise.feature.rewards.domain.repositories.RewardsFeatureRepository
import com.enterprise.feature.shop.data.CartRepositoryImpl
import com.enterprise.feature.shop.data.local.CartDao
import com.enterprise.feature.shop.data.local.CartDatabase
import com.enterprise.feature.shop.data.local.OfflineActionDao
import com.enterprise.feature.shop.domain.repositories.ShopRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ── Bindings Module: Domain interfaces → concrete implementations ──────────

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {

    // Auth
    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthFeatureRepository

    @Binds @Singleton
    abstract fun bindAuthBaseRepository(impl: AuthRepositoryImpl): AuthRepository

    // Explore / Feed
    @Binds @Singleton
    abstract fun bindExploreFeedRepository(impl: FeedRepositoryImpl): ExploreFeedRepository

    @Binds @Singleton
    abstract fun bindFeedRepository(impl: FeedRepositoryImpl): FeedRepository

    // Rewards
    @Binds @Singleton
    abstract fun bindRewardsFeatureRepository(impl: RewardsRepositoryImpl): RewardsFeatureRepository

    @Binds @Singleton
    abstract fun bindRewardsRepository(impl: RewardsRepositoryImpl): RewardsRepository

    // Shop / Cart
    @Binds @Singleton
    abstract fun bindShopRepository(impl: CartRepositoryImpl): ShopRepository

    @Binds @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    // AI Connect / Chat
    @Binds @Singleton
    abstract fun bindAIConnectRepository(impl: ChatRepositoryImpl): AIConnectRepository

    @Binds @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}

// ── Provides Module: Retrofit services, Room DBs, OS services ──────────────

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {

    // ── Retrofit API Services ──────────────────────────────────────────

    @Provides @Singleton
    fun provideAuthApiService(apiClient: ApiClient): AuthApiService {
        return apiClient.createService()
    }

    @Provides @Singleton
    fun provideFeedApiService(apiClient: ApiClient): FeedApiService {
        return apiClient.createService()
    }

    @Provides @Singleton
    fun provideRewardsApiService(apiClient: ApiClient): RewardsApiService {
        return apiClient.createService()
    }

    // ── Room Databases & DAOs ──────────────────────────────────────────

    @Provides @Singleton
    fun provideFeedDatabase(@ApplicationContext context: Context): FeedDatabase {
        return Room.databaseBuilder(context, FeedDatabase::class.java, "feed_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides @Singleton
    fun provideFeedDao(database: FeedDatabase): FeedDao {
        return database.feedDao()
    }

    @Provides @Singleton
    fun provideCartDatabase(@ApplicationContext context: Context): CartDatabase {
        return Room.databaseBuilder(context, CartDatabase::class.java, "cart_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides @Singleton
    fun provideCartDao(database: CartDatabase): CartDao {
        return database.cartDao()
    }

    @Provides @Singleton
    fun provideOfflineActionDao(database: CartDatabase): OfflineActionDao {
        return database.offlineActionDao()
    }

    // ── Feature Flag Repository ────────────────────────────────────────

    @Provides @Singleton
    fun provideFeatureFlagRepository(): FeatureFlagRepository {
        return object : FeatureFlagRepository {
            override suspend fun getFlag(key: String): FeatureFlag =
                FeatureFlag(key = key, variant = "control", isEnabled = false)
            override suspend fun getAllFlags(): List<FeatureFlag> = emptyList()
        }
    }

    // ── OS Services ────────────────────────────────────────────────────

    @Provides @Singleton
    fun provideShareService(@ApplicationContext context: Context): ShareService {
        return object : ShareService {
            override suspend fun shareText(text: String, subject: String?) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                    subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "Share via").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
            override suspend fun shareUrl(url: String, title: String?) = shareText(url, title)
        }
    }

    @Provides @Singleton
    fun providePermissionService(): PermissionService {
        return object : PermissionService {
            override suspend fun requestContactsPermission() = PermissionResult.Granted
            override suspend fun requestCameraPermission() = PermissionResult.Granted
            override suspend fun requestMicrophonePermission() = PermissionResult.Granted
            override suspend fun requestNotificationPermission() = PermissionResult.Granted
            override fun hasPermission(permission: AppPermission) = true
        }
    }
}

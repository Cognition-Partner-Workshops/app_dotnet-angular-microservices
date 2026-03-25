package com.enterprise.mobile.di

import com.enterprise.core.network.ApiClient
import com.enterprise.feature.auth.data.AuthRepositoryImpl
import com.enterprise.feature.auth.data.api.AuthApiService
import com.enterprise.feature.auth.domain.repositories.AuthFeatureRepository
import com.enterprise.feature.explore.data.api.FeedApiService
import com.enterprise.feature.rewards.data.api.RewardsApiService
import com.enterprise.core.domain.repositories.AuthRepository
import com.enterprise.core.domain.repositories.FeedRepository
import com.enterprise.core.domain.repositories.RewardsRepository
import com.enterprise.feature.explore.data.FeedRepositoryImpl
import com.enterprise.feature.explore.domain.repositories.ExploreFeedRepository
import com.enterprise.feature.rewards.data.RewardsRepositoryImpl
import com.enterprise.feature.rewards.domain.repositories.RewardsFeatureRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Root DI module binding Domain interfaces to concrete implementations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthFeatureRepository

    @Binds
    @Singleton
    abstract fun bindAuthBaseRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindExploreFeedRepository(impl: FeedRepositoryImpl): ExploreFeedRepository

    @Binds
    @Singleton
    abstract fun bindFeedRepository(impl: FeedRepositoryImpl): FeedRepository

    @Binds
    @Singleton
    abstract fun bindRewardsFeatureRepository(impl: RewardsRepositoryImpl): RewardsFeatureRepository

    @Binds
    @Singleton
    abstract fun bindRewardsRepository(impl: RewardsRepositoryImpl): RewardsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {

    @Provides
    @Singleton
    fun provideAuthApiService(apiClient: ApiClient): AuthApiService {
        return apiClient.createService()
    }

    @Provides
    @Singleton
    fun provideFeedApiService(apiClient: ApiClient): FeedApiService {
        return apiClient.createService()
    }

    @Provides
    @Singleton
    fun provideRewardsApiService(apiClient: ApiClient): RewardsApiService {
        return apiClient.createService()
    }
}

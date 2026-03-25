package com.enterprise.core.network.di

import com.enterprise.core.network.ApiClient
import com.enterprise.core.network.TokenManager
import com.enterprise.core.network.WebSocketClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(apiClient: ApiClient): OkHttpClient {
        return apiClient.okHttpClient
    }

    @Provides
    @Singleton
    fun provideWebSocketClient(
        okHttpClient: OkHttpClient,
        tokenManager: TokenManager
    ): WebSocketClient {
        return WebSocketClient(okHttpClient, tokenManager)
    }
}

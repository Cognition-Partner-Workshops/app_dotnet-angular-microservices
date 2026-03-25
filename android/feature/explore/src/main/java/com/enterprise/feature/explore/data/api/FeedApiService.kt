package com.enterprise.feature.explore.data.api

import com.enterprise.feature.explore.data.dto.FeedResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/** Retrofit API service for feed endpoints. */
interface FeedApiService {
    @POST("feed/dynamic")
    suspend fun getDynamicFeed(): FeedResponse

    @GET("feed/personalized/{userId}")
    suspend fun getPersonalizedFeed(@Path("userId") userId: String): FeedResponse

    @GET("feed/search")
    suspend fun searchFeed(@Header("X-Search-Query") query: String): FeedResponse
}

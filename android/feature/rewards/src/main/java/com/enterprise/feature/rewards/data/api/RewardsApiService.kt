package com.enterprise.feature.rewards.data.api

import com.enterprise.feature.rewards.data.dto.RedeemRequest
import com.enterprise.feature.rewards.data.dto.RewardsAccountDTO
import com.enterprise.feature.rewards.data.dto.RewardsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** Retrofit API service for rewards endpoints. */
interface RewardsApiService {
    @GET("rewards")
    suspend fun getRewards(): RewardsResponse

    @GET("rewards")
    suspend fun getRewardsByCategory(@Query("category") category: String): RewardsResponse

    @GET("rewards/account")
    suspend fun getAccount(): RewardsAccountDTO

    @POST("rewards/redeem")
    suspend fun redeemReward(@Body request: RedeemRequest): RewardsAccountDTO
}

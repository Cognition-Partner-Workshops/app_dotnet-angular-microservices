package com.enterprise.core.domain.repositories

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.Reward
import com.enterprise.core.domain.entities.RewardsAccount
import kotlinx.coroutines.flow.Flow

/** Rewards program repository contract. */
interface RewardsRepository {
    fun getRewards(): Flow<DomainResult<List<Reward>>>
    suspend fun getAccount(): DomainResult<RewardsAccount>
    suspend fun redeemReward(rewardId: String): DomainResult<RewardsAccount>
}

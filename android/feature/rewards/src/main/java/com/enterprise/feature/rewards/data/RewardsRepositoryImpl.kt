package com.enterprise.feature.rewards.data

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.Reward
import com.enterprise.core.domain.entities.RewardCategory
import com.enterprise.core.domain.entities.RewardsAccount
import com.enterprise.feature.rewards.data.api.RewardsApiService
import com.enterprise.feature.rewards.data.dto.RedeemRequest
import com.enterprise.feature.rewards.domain.repositories.RewardsFeatureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/** Rewards repository with network calls and DTO mapping. */
@Singleton
class RewardsRepositoryImpl @Inject constructor(
    private val rewardsApiService: RewardsApiService
) : RewardsFeatureRepository {

    override fun getRewards(): Flow<DomainResult<List<Reward>>> = flow {
        try {
            val response = rewardsApiService.getRewards()
            val rewards = response.rewards.map { it.toDomain() }
            emit(DomainResult.Success(rewards))
        } catch (e: Exception) {
            emit(DomainResult.Failure(DomainError.NetworkUnavailable))
        }
    }

    override suspend fun getAccount(): DomainResult<RewardsAccount> {
        return try {
            val accountDTO = rewardsApiService.getAccount()
            DomainResult.Success(accountDTO.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.NetworkUnavailable)
        }
    }

    override suspend fun redeemReward(rewardId: String): DomainResult<RewardsAccount> {
        return try {
            val accountDTO = rewardsApiService.redeemReward(RedeemRequest(rewardId))
            DomainResult.Success(accountDTO.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unknown(e.localizedMessage ?: "Redeem failed"))
        }
    }

    override suspend fun getRewardsByCategory(category: RewardCategory): DomainResult<List<Reward>> {
        return try {
            val categoryString = category.name.lowercase()
            val response = rewardsApiService.getRewardsByCategory(categoryString)
            DomainResult.Success(response.rewards.map { it.toDomain() })
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.NetworkUnavailable)
        }
    }
}

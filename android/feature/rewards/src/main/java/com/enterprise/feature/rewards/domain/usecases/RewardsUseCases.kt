package com.enterprise.feature.rewards.domain.usecases

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.Reward
import com.enterprise.core.domain.entities.RewardsAccount
import com.enterprise.core.domain.repositories.RewardsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Gets rewards list as a reactive stream. */
class GetRewardsUseCase @Inject constructor(
    private val rewardsRepository: RewardsRepository
) {
    operator fun invoke(): Flow<DomainResult<List<Reward>>> {
        return rewardsRepository.getRewards()
    }
}

/** Redeems a reward by ID. */
class RedeemRewardUseCase @Inject constructor(
    private val rewardsRepository: RewardsRepository
) {
    suspend operator fun invoke(rewardId: String): DomainResult<RewardsAccount> {
        return rewardsRepository.redeemReward(rewardId)
    }
}

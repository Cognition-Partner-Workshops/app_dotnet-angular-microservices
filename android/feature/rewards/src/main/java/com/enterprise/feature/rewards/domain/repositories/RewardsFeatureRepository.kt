package com.enterprise.feature.rewards.domain.repositories

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.Reward
import com.enterprise.core.domain.entities.RewardCategory
import com.enterprise.core.domain.repositories.RewardsRepository

/** Extended rewards repository with category filtering. */
interface RewardsFeatureRepository : RewardsRepository {
    suspend fun getRewardsByCategory(category: RewardCategory): DomainResult<List<Reward>>
}

package com.enterprise.feature.rewards.domain.entities

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.Reward
import com.enterprise.core.domain.entities.RewardsAccount

/** Rewards state for MVI pattern. */
data class RewardsState(
    val rewards: List<Reward> = emptyList(),
    val account: RewardsAccount? = null,
    val isLoading: Boolean = false,
    val error: DomainError? = null,
    val selectedReward: Reward? = null
)

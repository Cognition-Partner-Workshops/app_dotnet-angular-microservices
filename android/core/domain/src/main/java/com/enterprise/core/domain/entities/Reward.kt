package com.enterprise.core.domain.entities

import java.util.Date

/** Rewards program domain entity. */
data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val pointsCost: Int,
    val imageUrl: String? = null,
    val expirationDate: Date? = null,
    val category: RewardCategory = RewardCategory.GENERAL
)

enum class RewardCategory {
    GENERAL, STREAMING, DINING, TRAVEL, MERCHANDISE
}

/** User's rewards account summary. */
data class RewardsAccount(
    val userId: String,
    val totalPoints: Int,
    val tierLevel: TierLevel,
    val redeemedRewards: List<String> = emptyList()
)

enum class TierLevel {
    BRONZE, SILVER, GOLD, PLATINUM
}

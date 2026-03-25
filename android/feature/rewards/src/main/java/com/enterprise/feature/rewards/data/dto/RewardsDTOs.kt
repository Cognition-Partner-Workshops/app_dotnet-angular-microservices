package com.enterprise.feature.rewards.data.dto

import com.enterprise.core.domain.entities.Reward
import com.enterprise.core.domain.entities.RewardCategory
import com.enterprise.core.domain.entities.RewardsAccount
import com.enterprise.core.domain.entities.TierLevel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class RewardsResponse(
    val rewards: List<RewardDTO>
)

@Serializable
data class RewardDTO(
    val id: String,
    val title: String,
    val description: String,
    @SerialName("points_cost") val pointsCost: Int,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("expiration_date") val expirationDate: Long? = null,
    val category: String? = null
) {
    fun toDomain(): Reward = Reward(
        id = id,
        title = title,
        description = description,
        pointsCost = pointsCost,
        imageUrl = imageUrl,
        expirationDate = expirationDate?.let { Date(it) },
        category = when (category) {
            "streaming" -> RewardCategory.STREAMING
            "dining" -> RewardCategory.DINING
            "travel" -> RewardCategory.TRAVEL
            "merchandise" -> RewardCategory.MERCHANDISE
            else -> RewardCategory.GENERAL
        }
    )
}

@Serializable
data class RewardsAccountDTO(
    @SerialName("user_id") val userId: String,
    @SerialName("total_points") val totalPoints: Int,
    @SerialName("tier_level") val tierLevel: String,
    @SerialName("redeemed_rewards") val redeemedRewards: List<String> = emptyList()
) {
    fun toDomain(): RewardsAccount = RewardsAccount(
        userId = userId,
        totalPoints = totalPoints,
        tierLevel = when (tierLevel) {
            "silver" -> TierLevel.SILVER
            "gold" -> TierLevel.GOLD
            "platinum" -> TierLevel.PLATINUM
            else -> TierLevel.BRONZE
        },
        redeemedRewards = redeemedRewards
    )
}

@Serializable
data class RedeemRequest(
    @SerialName("reward_id") val rewardId: String
)

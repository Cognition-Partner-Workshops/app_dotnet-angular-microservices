package com.enterprise.feature.rewards.presentation

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.Reward
import com.enterprise.core.domain.entities.RewardsAccount
import com.enterprise.core.domain.entities.TierLevel
import com.enterprise.core.domain.repositories.RewardsRepository
import com.enterprise.feature.rewards.domain.usecases.GetRewardsUseCase
import com.enterprise.feature.rewards.domain.usecases.RedeemRewardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RewardsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `rewards load on init`() = runTest {
        val mockRewards = listOf(
            Reward(id = "r-1", title = "Free Movie", description = "Stream a movie", pointsCost = 500),
            Reward(id = "r-2", title = "$5 Gift Card", description = "Dining", pointsCost = 1000)
        )
        val fakeRepo = FakeRewardsRepository(rewards = mockRewards)
        val getRewardsUseCase = GetRewardsUseCase(fakeRepo)
        val redeemRewardUseCase = RedeemRewardUseCase(fakeRepo)

        val viewModel = RewardsViewModel(getRewardsUseCase, redeemRewardUseCase)
        advanceUntilIdle()

        assertEquals(mockRewards, viewModel.state.value.rewards)
    }

    @Test
    fun `redeem reward updates account`() = runTest {
        val updatedAccount = RewardsAccount(
            userId = "u-1", totalPoints = 4500, tierLevel = TierLevel.GOLD,
            redeemedRewards = listOf("r-1")
        )
        val fakeRepo = FakeRewardsRepository(
            rewards = emptyList(),
            redeemResult = DomainResult.Success(updatedAccount)
        )
        val getRewardsUseCase = GetRewardsUseCase(fakeRepo)
        val redeemRewardUseCase = RedeemRewardUseCase(fakeRepo)

        val viewModel = RewardsViewModel(getRewardsUseCase, redeemRewardUseCase)
        advanceUntilIdle()

        viewModel.processIntent(RewardsViewModel.Intent.RedeemTapped("r-1"))
        advanceUntilIdle()

        assertEquals(updatedAccount, viewModel.state.value.account)
        assertNull(viewModel.state.value.selectedReward)
    }
}

// Fake

private class FakeRewardsRepository(
    private val rewards: List<Reward> = emptyList(),
    private val redeemResult: DomainResult<RewardsAccount> = DomainResult.Failure(DomainError.NotFound)
) : RewardsRepository {
    override fun getRewards(): Flow<DomainResult<List<Reward>>> = flowOf(DomainResult.Success(rewards))
    override suspend fun getAccount(): DomainResult<RewardsAccount> = DomainResult.Failure(DomainError.NotFound)
    override suspend fun redeemReward(rewardId: String) = redeemResult
}

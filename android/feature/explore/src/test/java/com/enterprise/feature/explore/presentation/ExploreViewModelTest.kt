package com.enterprise.feature.explore.presentation

import com.enterprise.core.domain.entities.ActionPill
import com.enterprise.core.domain.entities.ActionPillConfig
import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.FeatureFlag
import com.enterprise.core.domain.entities.HeroConfig
import com.enterprise.core.domain.entities.PricingInfo
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.core.domain.repositories.FeatureFlagRepository
import com.enterprise.core.domain.usecases.FeatureFlagUseCase
import com.enterprise.feature.explore.domain.repositories.ExploreFeedRepository
import com.enterprise.feature.explore.domain.usecases.GetExploreFeedUseCase
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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {

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
    fun `feed loads on init`() = runTest {
        val mockComponents = listOf(
            UIComponent.HeroCard(
                HeroConfig(
                    id = "hero-1", title = "Fios", subtitle = "Fast",
                    imageUrl = null, gradientColors = emptyList(),
                    pricing = PricingInfo(BigDecimal("49.99"), "$", "mo"),
                    ctaText = "Shop Now", ctaDeepLink = "app://shop"
                )
            ),
            UIComponent.ActionPillRow(
                ActionPillConfig(
                    id = "pills-1",
                    pills = listOf(ActionPill("Bill", "doc.text", "app://bill"))
                )
            )
        )
        val fakeRepo = FakeExploreFeedRepository(DomainResult.Success(mockComponents))
        val fakeFlags = FakeFeatureFlagRepository()
        val useCase = GetExploreFeedUseCase(fakeRepo, FeatureFlagUseCase(fakeFlags))

        val viewModel = ExploreViewModel(useCase)
        advanceUntilIdle()

        assertEquals(mockComponents, viewModel.state.value.components)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `feed error updates state`() = runTest {
        val fakeRepo = FakeExploreFeedRepository(DomainResult.Failure(DomainError.NetworkUnavailable))
        val fakeFlags = FakeFeatureFlagRepository()
        val useCase = GetExploreFeedUseCase(fakeRepo, FeatureFlagUseCase(fakeFlags))

        val viewModel = ExploreViewModel(useCase)
        advanceUntilIdle()

        assertEquals(DomainError.NetworkUnavailable, viewModel.state.value.error)
    }
}

// Fakes

private class FakeExploreFeedRepository(
    private val result: DomainResult<List<UIComponent>>
) : ExploreFeedRepository {
    override fun getDynamicFeed(): Flow<DomainResult<List<UIComponent>>> = flowOf(result)
    override suspend fun refreshFeed() = result
    override suspend fun getCachedFeed(): List<UIComponent> = emptyList()
    override fun getPersonalizedFeed(userId: String) = flowOf(result)
    override suspend fun searchFeed(query: String) = result
}

private class FakeFeatureFlagRepository : FeatureFlagRepository {
    override suspend fun getFlag(key: String) = FeatureFlag(key = key, variant = "control", isEnabled = false)
    override suspend fun getAllFlags() = emptyList<FeatureFlag>()
}

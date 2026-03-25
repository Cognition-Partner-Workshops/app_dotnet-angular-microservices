package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.HeroConfig
import com.enterprise.core.domain.entities.PricingInfo
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.core.domain.repositories.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetDynamicFeedUseCaseTest {

    @Test
    fun `returns feed components from repository`() = runTest {
        val mockComponents = listOf(
            UIComponent.HeroCard(
                HeroConfig(
                    id = "hero-1",
                    title = "Fios Gigabit",
                    subtitle = "Blazing fast internet",
                    imageUrl = null,
                    gradientColors = listOf("#000000"),
                    pricing = PricingInfo(BigDecimal("49.99"), "$", "mo"),
                    ctaText = "Shop Now",
                    ctaDeepLink = "app://shop/fios"
                )
            )
        )
        val fakeRepo = FakeFeedRepository(DomainResult.Success(mockComponents))
        val useCase = GetDynamicFeedUseCase(fakeRepo)

        val results = useCase().toList()

        assertEquals(1, results.size)
        assertTrue(results[0] is DomainResult.Success)
        assertEquals(mockComponents, (results[0] as DomainResult.Success).data)
    }

    @Test
    fun `empty feed returns failure`() = runTest {
        val fakeRepo = FakeFeedRepository(DomainResult.Failure(DomainError.EmptyData))
        val useCase = GetDynamicFeedUseCase(fakeRepo)

        val results = useCase().toList()

        assertEquals(1, results.size)
        assertTrue(results[0] is DomainResult.Failure)
        assertEquals(DomainError.EmptyData, (results[0] as DomainResult.Failure).error)
    }
}

private class FakeFeedRepository(
    private val result: DomainResult<List<UIComponent>>
) : FeedRepository {
    override fun getDynamicFeed(): Flow<DomainResult<List<UIComponent>>> = flowOf(result)
    override suspend fun refreshFeed(): DomainResult<List<UIComponent>> = result
    override suspend fun getCachedFeed(): List<UIComponent> = emptyList()
}

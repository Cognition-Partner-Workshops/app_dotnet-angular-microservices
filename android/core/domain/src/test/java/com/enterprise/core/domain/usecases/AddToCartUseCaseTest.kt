package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.entities.CartItem
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.interfaces.AnalyticsTracker
import com.enterprise.core.domain.repositories.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AddToCartUseCaseTest {

    @Test
    fun `add to cart returns success and tracks analytics`() = runTest {
        val fakeRepo = FakeCartRepository()
        val fakeTracker = FakeAnalyticsTracker()
        val useCase = AddToCartUseCase(fakeRepo, fakeTracker)

        val item = CartItem(
            id = "item-1",
            productId = "prod-1",
            name = "Fios Gigabit",
            price = BigDecimal("49.99")
        )

        val result = useCase(item)

        assertTrue(result is DomainResult.Success)
        assertTrue(fakeRepo.addedItems.contains(item))
        assertTrue(fakeTracker.trackedEvents.any { it.first == "add_to_cart" })
    }

    @Test
    fun `add to cart tracks product_id in analytics`() = runTest {
        val fakeRepo = FakeCartRepository()
        val fakeTracker = FakeAnalyticsTracker()
        val useCase = AddToCartUseCase(fakeRepo, fakeTracker)

        val item = CartItem(
            id = "item-2",
            productId = "prod-xyz",
            name = "Test Product",
            price = BigDecimal("99.99")
        )

        useCase(item)

        val event = fakeTracker.trackedEvents.find { it.first == "add_to_cart" }
        assertTrue(event != null)
        assertTrue(event!!.second["product_id"] == "prod-xyz")
    }
}

private class FakeCartRepository : CartRepository {
    val addedItems = mutableListOf<CartItem>()

    override fun getCartItems(): Flow<List<CartItem>> = flowOf(addedItems)
    override suspend fun addItem(item: CartItem): DomainResult<Unit> {
        addedItems.add(item)
        return DomainResult.Success(Unit)
    }
    override suspend fun removeItem(id: String): DomainResult<Unit> = DomainResult.Success(Unit)
    override suspend fun updateQuantity(itemId: String, quantity: Int): DomainResult<Unit> = DomainResult.Success(Unit)
    override suspend fun clearCart(): DomainResult<Unit> = DomainResult.Success(Unit)
    override suspend fun getCartItemCount(): Int = addedItems.sumOf { it.quantity }
}

private class FakeAnalyticsTracker : AnalyticsTracker {
    val trackedEvents = mutableListOf<Pair<String, Map<String, Any>>>()
    override fun trackScreen(name: String) {}
    override fun trackEvent(name: String, properties: Map<String, Any>) {
        trackedEvents.add(name to properties)
    }
}

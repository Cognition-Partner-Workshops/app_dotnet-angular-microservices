package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.entities.CartItem
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.interfaces.AnalyticsTracker
import com.enterprise.core.domain.repositories.CartRepository
import javax.inject.Inject

/** Adds item to cart with analytics tracking and offline queue support. */
class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val analyticsTracker: AnalyticsTracker
) {
    suspend operator fun invoke(item: CartItem): DomainResult<Unit> {
        analyticsTracker.trackEvent("add_to_cart", mapOf(
            "product_id" to item.productId,
            "price" to item.price.toString()
        ))
        return cartRepository.addItem(item)
    }
}

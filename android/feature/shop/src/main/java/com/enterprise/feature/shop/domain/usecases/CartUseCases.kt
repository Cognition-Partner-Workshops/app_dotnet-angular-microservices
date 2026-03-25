package com.enterprise.feature.shop.domain.usecases

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.repositories.CartRepository
import javax.inject.Inject

/** Removes item from cart. */
class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(itemId: String): DomainResult<Unit> {
        return cartRepository.removeItem(itemId)
    }
}

/** Updates cart item quantity. */
class UpdateCartQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(itemId: String, quantity: Int): DomainResult<Unit> {
        return if (quantity <= 0) {
            cartRepository.removeItem(itemId)
        } else {
            cartRepository.updateQuantity(itemId, quantity)
        }
    }
}

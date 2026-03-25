package com.enterprise.feature.shop.domain.entities

import com.enterprise.core.domain.entities.CartItem
import com.enterprise.core.domain.entities.DomainError
import java.math.BigDecimal

/** Shopping cart state for MVI pattern. */
data class ShopState(
    val cartItems: List<CartItem> = emptyList(),
    val cartItemCount: Int = 0,
    val isProcessing: Boolean = false,
    val error: DomainError? = null
) {
    val totalPrice: BigDecimal
        get() = cartItems.fold(BigDecimal.ZERO) { acc, item -> acc + item.totalPrice }
}

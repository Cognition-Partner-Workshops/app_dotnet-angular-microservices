package com.enterprise.core.domain.entities

import java.math.BigDecimal

/** Shopping cart item domain entity. */
data class CartItem(
    val id: String,
    val productId: String,
    val name: String,
    val price: BigDecimal,
    val quantity: Int = 1,
    val imageUrl: String? = null
) {
    val totalPrice: BigDecimal get() = price * BigDecimal(quantity)
}

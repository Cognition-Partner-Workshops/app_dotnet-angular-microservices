package com.enterprise.core.domain.repositories

import com.enterprise.core.domain.entities.CartItem
import com.enterprise.core.domain.entities.DomainResult
import kotlinx.coroutines.flow.Flow

/** Shopping cart repository contract with reactive item stream. */
interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addItem(item: CartItem): DomainResult<Unit>
    suspend fun removeItem(id: String): DomainResult<Unit>
    suspend fun updateQuantity(itemId: String, quantity: Int): DomainResult<Unit>
    suspend fun clearCart(): DomainResult<Unit>
    suspend fun getCartItemCount(): Int
}

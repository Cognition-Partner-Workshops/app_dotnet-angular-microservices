package com.enterprise.feature.shop.data

import com.enterprise.core.domain.entities.CartItem
import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.feature.shop.data.local.CartDao
import com.enterprise.feature.shop.data.local.CartItemEntity
import com.enterprise.feature.shop.data.local.OfflineActionDao
import com.enterprise.feature.shop.data.local.OfflineActionEntity
import com.enterprise.feature.shop.domain.repositories.ProductDetail
import com.enterprise.feature.shop.domain.repositories.ProductSummary
import com.enterprise.feature.shop.domain.repositories.ShopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Cart repository with Room persistence, reactive streams, and offline action queue. */
@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val offlineActionDao: OfflineActionDao
) : ShopRepository {

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addItem(item: CartItem): DomainResult<Unit> {
        return try {
            val existing = cartDao.getAll().find { it.productId == item.productId }
            if (existing != null) {
                cartDao.updateQuantity(existing.id, existing.quantity + item.quantity)
            } else {
                cartDao.insert(CartItemEntity.fromDomain(item))
            }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            // Queue as offline action if persistence fails
            offlineActionDao.insert(
                OfflineActionEntity(
                    id = UUID.randomUUID().toString(),
                    actionType = "ADD_TO_CART",
                    payloadJson = """{"productId":"${item.productId}","quantity":${item.quantity}}"""
                )
            )
            DomainResult.Success(Unit) // Optimistic: still report success for offline
        }
    }

    override suspend fun removeItem(id: String): DomainResult<Unit> {
        return try {
            cartDao.deleteById(id)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unknown(e.localizedMessage ?: "Remove failed"))
        }
    }

    override suspend fun updateQuantity(itemId: String, quantity: Int): DomainResult<Unit> {
        return try {
            cartDao.updateQuantity(itemId, quantity)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unknown(e.localizedMessage ?: "Update failed"))
        }
    }

    override suspend fun clearCart(): DomainResult<Unit> {
        return try {
            cartDao.deleteAll()
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unknown(e.localizedMessage ?: "Clear failed"))
        }
    }

    override suspend fun getCartItemCount(): Int {
        return cartDao.getTotalItemCount()
    }

    override suspend fun getProducts(): DomainResult<List<ProductSummary>> {
        // Network call would go here; stubbed for now
        return DomainResult.Success(emptyList())
    }

    override suspend fun getProductDetail(productId: String): DomainResult<ProductDetail> {
        return DomainResult.Failure(DomainError.NotFound)
    }
}

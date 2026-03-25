package com.enterprise.feature.shop.domain.repositories

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.repositories.CartRepository

/** Extended cart repository with product browsing capabilities. */
interface ShopRepository : CartRepository {
    suspend fun getProducts(): DomainResult<List<ProductSummary>>
    suspend fun getProductDetail(productId: String): DomainResult<ProductDetail>
}

data class ProductSummary(
    val id: String,
    val name: String,
    val price: java.math.BigDecimal,
    val imageUrl: String?
)

data class ProductDetail(
    val id: String,
    val name: String,
    val description: String,
    val price: java.math.BigDecimal,
    val imageUrl: String?,
    val features: List<String> = emptyList()
)

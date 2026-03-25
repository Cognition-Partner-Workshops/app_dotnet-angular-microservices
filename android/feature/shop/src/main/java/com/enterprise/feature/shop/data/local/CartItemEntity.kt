package com.enterprise.feature.shop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.enterprise.core.domain.entities.CartItem
import java.math.BigDecimal

/** Room entity for cart items with offline persistence. */
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val name: String,
    val priceAmount: String,
    val quantity: Int,
    val imageUrl: String?
) {
    fun toDomain(): CartItem = CartItem(
        id = id,
        productId = productId,
        name = name,
        price = BigDecimal(priceAmount),
        quantity = quantity,
        imageUrl = imageUrl
    )

    companion object {
        fun fromDomain(item: CartItem): CartItemEntity = CartItemEntity(
            id = item.id,
            productId = item.productId,
            name = item.name,
            priceAmount = item.price.toString(),
            quantity = item.quantity,
            imageUrl = item.imageUrl
        )
    }
}

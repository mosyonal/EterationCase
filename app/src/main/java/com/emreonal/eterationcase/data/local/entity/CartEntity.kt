package com.emreonal.eterationcase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emreonal.eterationcase.domain.model.CartItem

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val price: Double,
    val image: String?,
    val quantity: Int
)

fun CartEntity.toDomain() = CartItem(productId, title, price, image, quantity)
fun CartItem.toEntity() = CartEntity(productId, title, price, image, quantity)

package com.emreonal.eterationcase.domain.model

data class CartItem(
    val productId: String,
    val title: String,
    val price: Double,
    val image: String?,
    val quantity: Int
)

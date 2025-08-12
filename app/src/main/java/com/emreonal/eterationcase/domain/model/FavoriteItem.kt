package com.emreonal.eterationcase.domain.model

import com.emreonal.eterationcase.ui.list.ProductUiModel
import java.time.Instant

data class FavoriteItem(
    val productId: String,
    val title: String,
    val price: Double,
    val image: String,
    val description: String,
    val brand: String,
    val model: String,
    val createdAt: Instant
)

fun FavoriteItem.toProductUiModel(): ProductUiModel =
    ProductUiModel(
        id = productId,
        name = title,
        price = price,
        image = image,
        description = description,
        quantity = 0,
        brand = brand,
        model = model,
        createdAt = createdAt,
        isFavorite = true
    )
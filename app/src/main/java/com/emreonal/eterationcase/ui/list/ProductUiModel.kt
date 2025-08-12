package com.emreonal.eterationcase.ui.list

import android.os.Parcelable
import com.emreonal.eterationcase.domain.model.FavoriteItem
import com.emreonal.eterationcase.domain.model.Product
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class ProductUiModel(
    val id: String,
    val name: String,
    val price: Double,
    val image: String,
    val description: String,
    val brand: String,
    val model: String,
    val createdAt: Instant,
    val quantity: Int,
    val isFavorite: Boolean
): Parcelable

fun ProductUiModel.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        price = price,
        image = image,
        description = description,
        brand = brand,
        model = model,
        createdAt = createdAt
    )
}

fun ProductUiModel.toFavorite(): FavoriteItem = FavoriteItem(
    productId = id,
    title = name,
    price = price,
    image = image,
    description = description,
    brand = brand,
    model = model,
    createdAt = createdAt
)
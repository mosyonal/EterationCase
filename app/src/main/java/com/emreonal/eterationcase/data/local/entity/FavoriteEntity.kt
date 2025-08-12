package com.emreonal.eterationcase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emreonal.eterationcase.domain.model.FavoriteItem
import java.time.Instant

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val price: Double,
    val image: String,          // artık non-null
    val description: String,
    val brand: String,          // eklendi
    val model: String,          // eklendi
    val createdAt: Long         // epoch millis (Instant için)
)

fun FavoriteEntity.toDomain() = FavoriteItem(
    productId = productId,
    title = title,
    price = price,
    image = image,
    description = description,
    brand = brand,
    model = model,
    createdAt = Instant.ofEpochMilli(createdAt)
)

fun FavoriteItem.toEntity() = FavoriteEntity(
    productId = productId,
    title = title,
    price = price,
    image = image,
    description = description,
    brand = brand,
    model = model,
    createdAt = createdAt.toEpochMilli()
)

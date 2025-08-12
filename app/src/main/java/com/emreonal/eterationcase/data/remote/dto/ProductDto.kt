package com.emreonal.eterationcase.data.remote.dto

import com.emreonal.eterationcase.domain.model.Product
import java.time.Instant

data class ProductDto(
    val id: String,
    val name: String,
    val image: String,
    val price: String,
    val description: String,
    val model: String,
    val brand: String,
    val createdAt: String
)

fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    image = image,
    description = description,
    brand = brand,
    model = model,
    createdAt = Instant.parse(createdAt)
)
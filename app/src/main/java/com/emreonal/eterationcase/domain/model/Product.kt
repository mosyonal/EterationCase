package com.emreonal.eterationcase.domain.model

import java.time.Instant

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val image: String?,
    val description: String,
    val brand: String,
    val model: String,
    val createdAt: Instant
)

package com.emreonal.eterationcase.domain.repository

import com.emreonal.eterationcase.core.Result
import com.emreonal.eterationcase.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<Result<List<Product>>>
}

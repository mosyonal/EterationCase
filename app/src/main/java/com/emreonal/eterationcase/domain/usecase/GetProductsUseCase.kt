package com.emreonal.eterationcase.domain.usecase

import com.emreonal.eterationcase.core.Result
import com.emreonal.eterationcase.domain.model.Product
import com.emreonal.eterationcase.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(private val repo: ProductRepository) {
    operator fun invoke(): Flow<Result<List<Product>>> = repo.getProducts()
}
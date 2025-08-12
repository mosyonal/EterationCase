package com.emreonal.eterationcase.data.repository

import com.emreonal.eterationcase.data.remote.ApiService
import com.emreonal.eterationcase.data.remote.dto.toDomain
import com.emreonal.eterationcase.domain.model.Product
import com.emreonal.eterationcase.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import com.emreonal.eterationcase.core.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ProductRepository {

    override fun getProducts(): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading)
            val list = withContext(Dispatchers.IO) {
                api.getProducts().map { it.toDomain() }
            }
            emit(Result.Success(list))
        } catch (t: Throwable) {
            emit(Result.Error(t.message))
        }
    }
}

package com.emreonal.eterationcase.data.repository

import com.emreonal.eterationcase.data.local.dao.CartDao
import com.emreonal.eterationcase.data.local.entity.toDomain
import com.emreonal.eterationcase.data.local.entity.toEntity
import com.emreonal.eterationcase.domain.model.CartItem
import com.emreonal.eterationcase.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val dao: CartDao
) : CartRepository {

    override fun observeCart(): Flow<List<CartItem>> =
        dao.observeCart().map { list -> list.map { it.toDomain() } }

    override suspend fun incOrAdd(item: CartItem) {
        val updated = dao.inc(item.productId)
        if (updated == 0) {
            dao.upsert(item.toEntity())
        }
    }

    override suspend fun inc(productId: String) = dao.inc(productId)

    override suspend fun dec(productId: String) = dao.dec(productId)

    override suspend fun remove(productId: String) = dao.delete(productId)

    override suspend fun clear() = dao.clear()
}

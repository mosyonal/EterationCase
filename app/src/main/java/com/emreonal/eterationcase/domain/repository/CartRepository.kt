package com.emreonal.eterationcase.domain.repository

import com.emreonal.eterationcase.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observeCart(): Flow<List<CartItem>>

    suspend fun incOrAdd(item: CartItem)

    suspend fun inc(productId: String): Int

    suspend fun dec(productId: String)

    suspend fun remove(productId: String)

    suspend fun clear()
}

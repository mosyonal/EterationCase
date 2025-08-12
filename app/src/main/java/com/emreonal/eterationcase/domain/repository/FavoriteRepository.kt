package com.emreonal.eterationcase.domain.repository

import com.emreonal.eterationcase.domain.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<FavoriteItem>>

    suspend fun toggle(favoriteItem: FavoriteItem)

    suspend fun remove(productId: String)

    suspend fun isFavorite(productId: String): Boolean
}

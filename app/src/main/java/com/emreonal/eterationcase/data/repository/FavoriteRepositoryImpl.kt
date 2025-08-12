package com.emreonal.eterationcase.data.repository

import com.emreonal.eterationcase.data.local.dao.FavoriteDao
import com.emreonal.eterationcase.data.local.entity.toDomain
import com.emreonal.eterationcase.data.local.entity.toEntity
import com.emreonal.eterationcase.domain.model.FavoriteItem
import com.emreonal.eterationcase.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoriteDao
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<FavoriteItem>> =
        dao.observe().map { it.map { e -> e.toDomain() } }

    override suspend fun toggle(favoriteItem: FavoriteItem) {
        val isFav = dao.isFavorite(favoriteItem.productId)
        if (isFav) dao.remove(favoriteItem.productId) else dao.add(favoriteItem.toEntity())
    }

    override suspend fun remove(productId: String) {
        dao.remove(productId)
    }

    override suspend fun isFavorite(productId: String): Boolean = dao.isFavorite(productId)
}

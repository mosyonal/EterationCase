package com.emreonal.eterationcase.domain.usecase

import com.emreonal.eterationcase.domain.model.FavoriteItem
import com.emreonal.eterationcase.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val repo: FavoriteRepository
) {
    operator fun invoke(): Flow<List<FavoriteItem>> = repo.observeFavorites()
}
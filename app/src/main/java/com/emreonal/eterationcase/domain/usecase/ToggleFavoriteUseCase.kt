package com.emreonal.eterationcase.domain.usecase

import com.emreonal.eterationcase.domain.model.FavoriteItem
import com.emreonal.eterationcase.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repo: FavoriteRepository
) {
    suspend operator fun invoke(favoriteItem: FavoriteItem) =
        repo.toggle(favoriteItem)
}
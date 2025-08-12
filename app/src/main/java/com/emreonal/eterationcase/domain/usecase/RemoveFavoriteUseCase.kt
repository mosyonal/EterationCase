package com.emreonal.eterationcase.domain.usecase

import com.emreonal.eterationcase.domain.repository.FavoriteRepository
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val repo: FavoriteRepository
) {
    suspend operator fun invoke(productId: String) =
        repo.remove(productId)
}
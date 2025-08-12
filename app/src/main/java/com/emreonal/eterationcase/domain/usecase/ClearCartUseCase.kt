package com.emreonal.eterationcase.domain.usecase


import com.emreonal.eterationcase.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val repo: CartRepository
) {
    suspend operator fun invoke() = repo.clear()
}
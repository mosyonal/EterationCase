package com.emreonal.eterationcase.domain.usecase


import com.emreonal.eterationcase.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val repo: CartRepository
) {
    suspend operator fun invoke(productId: String) = repo.remove(productId)
}
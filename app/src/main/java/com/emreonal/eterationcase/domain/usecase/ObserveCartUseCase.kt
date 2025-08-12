package com.emreonal.eterationcase.domain.usecase

import com.emreonal.eterationcase.domain.model.CartItem
import com.emreonal.eterationcase.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(
    private val repo: CartRepository
) {
    operator fun invoke(): Flow<List<CartItem>> = repo.observeCart()
}
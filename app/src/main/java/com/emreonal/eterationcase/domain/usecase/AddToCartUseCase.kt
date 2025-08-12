package com.emreonal.eterationcase.domain.usecase

import com.emreonal.eterationcase.domain.model.CartItem
import com.emreonal.eterationcase.domain.model.Product
import com.emreonal.eterationcase.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repo: CartRepository
) {
    suspend operator fun invoke(product: Product, quantity: Int = 1) {
        val item = CartItem(
            productId = product.id,
            title = product.name,
            price = product.price,
            image = product.image,
            quantity = quantity
        )
        repo.incOrAdd(item)
    }
}
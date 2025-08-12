package com.emreonal.eterationcase.ui.cart

import com.emreonal.eterationcase.domain.model.CartItem
import com.emreonal.eterationcase.ui.component.UiStatus

data class CartUiState(
    val uiStatus: UiStatus = UiStatus.Loading,
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0
)

package com.emreonal.eterationcase.ui.detail

import com.emreonal.eterationcase.ui.list.ProductUiModel

data class ProductDetailUiState(
    val product: ProductUiModel? = null,
    val isFavorite: Boolean = false
)

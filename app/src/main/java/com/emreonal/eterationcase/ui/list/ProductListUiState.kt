package com.emreonal.eterationcase.ui.list

import com.emreonal.eterationcase.ui.component.UiStatus
import com.emreonal.eterationcase.ui.list.filter.FiltersState

data class ProductListUiState(
    val products: List<ProductUiModel> = emptyList(),
    val uiStatus: UiStatus = UiStatus.Loading,
    val query: String = "",
    val allBrands: List<String> = emptyList(),
    val allModels: List<String> = emptyList(),
    val filters: FiltersState = FiltersState(),
    val filterCount: Int = 0
)
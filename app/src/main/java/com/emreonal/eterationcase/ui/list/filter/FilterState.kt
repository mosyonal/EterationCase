package com.emreonal.eterationcase.ui.list.filter

data class FiltersState(
    val sort: SortOption = SortOption.OldToNew,
    val selectedBrands: Set<String> = emptySet(),
    val selectedModels: Set<String> = emptySet(),
    val brandQuery: String = "",
    val modelQuery: String = ""
)
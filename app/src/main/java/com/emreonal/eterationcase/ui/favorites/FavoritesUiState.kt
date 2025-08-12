package com.emreonal.eterationcase.ui.favorites

import com.emreonal.eterationcase.domain.model.FavoriteItem
import com.emreonal.eterationcase.ui.component.UiStatus

data class FavoritesUiState(
    val uiStatus: UiStatus = UiStatus.Loading,
    val favorites: List<FavoriteItem> = emptyList()
)

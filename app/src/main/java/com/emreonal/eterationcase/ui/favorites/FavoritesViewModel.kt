package com.emreonal.eterationcase.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreonal.eterationcase.domain.model.FavoriteItem
import com.emreonal.eterationcase.domain.model.Product
import com.emreonal.eterationcase.domain.usecase.AddToCartUseCase
import com.emreonal.eterationcase.domain.usecase.ObserveFavoritesUseCase
import com.emreonal.eterationcase.domain.usecase.RemoveFavoriteUseCase
import com.emreonal.eterationcase.ui.component.UiStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavorites: ObserveFavoritesUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        observeJob?.cancel()
        observeJob = observeFavorites()
            .onStart {
                _uiState.update {
                    it.copy(uiStatus = UiStatus.Loading)
                }
            }
            .onEach { list ->
                _uiState.update {
                    it.copy(
                        favorites = list,
                        uiStatus = if (list.isEmpty()) {
                            UiStatus.Empty
                        } else {
                            UiStatus.ShowContent
                        }
                    )
                }
            }
            .catch { e ->
                _uiState.update {
                    it.copy(uiStatus = UiStatus.Error(e.message ?: "Unknown Error"))
                }
            }
            .launchIn(viewModelScope)
    }

    fun remove(productId: String) = viewModelScope.launch {
        removeFavorite(productId)
    }

    fun addToCart(item: FavoriteItem) = viewModelScope.launch {
        addToCartUseCase(
            Product(
                id = item.productId,
                name = item.title,
                price = item.price,
                image = item.image,
                description = item.description,
                brand = item.brand,
                model = item.model,
                createdAt = item.createdAt
            )
        )
    }
}
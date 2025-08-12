package com.emreonal.eterationcase.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreonal.eterationcase.domain.usecase.AddToCartUseCase
import com.emreonal.eterationcase.domain.usecase.ToggleFavoriteUseCase
import com.emreonal.eterationcase.ui.list.ProductUiModel
import com.emreonal.eterationcase.ui.list.toDomain
import com.emreonal.eterationcase.ui.list.toFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val addToCartUseCase: AddToCartUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState

    fun setProduct(p: ProductUiModel) {
        _uiState.value = ProductDetailUiState(product = p, isFavorite = p.isFavorite)
    }

    fun toggleFavorite() = viewModelScope.launch {
        _uiState.value.product?.let {
            toggleFavoriteUseCase(it.toFavorite())
            _uiState.update { state ->
                state.copy(
                    isFavorite = _uiState.value.isFavorite.not())
            }
        }
    }

    fun addToCart() = viewModelScope.launch {
       _uiState.value.product?.let {
           addToCartUseCase(it.toDomain())
       }
    }
}
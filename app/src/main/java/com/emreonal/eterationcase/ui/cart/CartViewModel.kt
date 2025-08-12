package com.emreonal.eterationcase.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreonal.eterationcase.domain.model.CartItem
import com.emreonal.eterationcase.domain.usecase.ClearCartUseCase
import com.emreonal.eterationcase.domain.usecase.DecrementCartItemUseCase
import com.emreonal.eterationcase.domain.usecase.IncrementCartItemUseCase
import com.emreonal.eterationcase.domain.usecase.ObserveCartUseCase
import com.emreonal.eterationcase.domain.usecase.RemoveFromCartUseCase
import com.emreonal.eterationcase.ui.component.UiStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    observeCart: ObserveCartUseCase,
    private val incrementCartItem: IncrementCartItemUseCase,
    private val decrementCartItem: DecrementCartItemUseCase,
    private val clearCart: ClearCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    val totalItemCount: StateFlow<Int> = uiState
        .map { it.items.sumOf { item -> item.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private var observeJob: Job? = null

    init {
        observeJob?.cancel()
        observeJob = observeCart()
            .onStart {
                _uiState.update {
                    it.copy(uiStatus = UiStatus.Loading)
                }
            }
            .onEach { items ->
                val total = items.sumOf { it.price * it.quantity }
                _uiState.update {
                    CartUiState(
                        uiStatus = if (items.isEmpty()) UiStatus.Empty else UiStatus.ShowContent,
                        items = items,
                        totalPrice = total
                    )
                }
            }
            .catch { e -> _uiState.update { it.copy(uiStatus = UiStatus.Error(e.message ?: "Unknown Error")) } }
            .launchIn(viewModelScope)
    }

    fun increment(productId: String) {
        viewModelScope.launch { incrementCartItem(productId) }
    }

    fun decrement(item: CartItem) {
        viewModelScope.launch {
            if (item.quantity <= 1) {
                removeFromCartUseCase(item.productId)
            } else {
                decrementCartItem(item.productId)
            }
        }
    }

    fun clear() {
        viewModelScope.launch { clearCart() }
    }

}
package com.emreonal.eterationcase.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreonal.eterationcase.core.apiCall
import com.emreonal.eterationcase.domain.model.FavoriteItem
import com.emreonal.eterationcase.domain.model.Product
import com.emreonal.eterationcase.domain.usecase.AddToCartUseCase
import com.emreonal.eterationcase.domain.usecase.DecrementCartItemUseCase
import com.emreonal.eterationcase.domain.usecase.GetProductsUseCase
import com.emreonal.eterationcase.domain.usecase.IncrementCartItemUseCase
import com.emreonal.eterationcase.domain.usecase.ObserveCartUseCase
import com.emreonal.eterationcase.domain.usecase.ObserveFavoritesUseCase
import com.emreonal.eterationcase.domain.usecase.RemoveFromCartUseCase
import com.emreonal.eterationcase.domain.usecase.ToggleFavoriteUseCase
import com.emreonal.eterationcase.ui.component.UiStatus
import com.emreonal.eterationcase.ui.list.filter.FiltersState
import com.emreonal.eterationcase.ui.list.filter.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    private val observeCartUseCase: ObserveCartUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val addToCart: AddToCartUseCase,
    private val incrementItem: IncrementCartItemUseCase,
    private val decrementItem: DecrementCartItemUseCase,
    private val removeFromCart: RemoveFromCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private var allProducts: List<ProductUiModel> = emptyList()

    init {
        observeMergedProducts()
        bindSearch()
    }

    @OptIn(FlowPreview::class)
    private fun bindSearch() {
        queryFlow
            .debounce(300)
            .distinctUntilChanged()
            .drop(1)
            .onEach { applyFilter() }
            .launchIn(viewModelScope)
    }

    fun retry() {
        observeMergedProducts()
    }

    private fun observeMergedProducts() {
        val cartFlow = observeCartUseCase()
            .map { cart -> cart.associate { it.productId to it.quantity } }

        val favoritesFlow = observeFavoritesUseCase()
            .map { list -> list.map { it.productId }.toSet() }

        apiCall { getProducts() }
            .onLoading {
                _uiState.update { it.copy(uiStatus = UiStatus.Loading) }
            }
            .onError { msg ->
                _uiState.update { it.copy(uiStatus = UiStatus.Error(msg ?: "Unknown error")) }
            }
            .onSuccess { list ->
                val products = list.map { d ->
                    ProductUiModel(
                        id = d.id,
                        name = d.name,
                        price = d.price,
                        image = d.image.orEmpty(),
                        description = d.description,
                        model = d.model,
                        brand = d.brand,
                        createdAt = d.createdAt,
                        quantity = 0,
                        isFavorite = false
                    )
                }

                cartFlow.combine(favoritesFlow) { cartMap, favSet ->
                    products.map { p ->
                        p.copy(
                            quantity = cartMap[p.id] ?: 0,
                            isFavorite = favSet.contains(p.id)
                        )
                    }
                }.onEach { merged ->
                    allProducts = merged

                    val allBrands = merged.map { it.brand }.filter { it.isNotBlank() }.distinct().sorted()
                    val allModels = merged.map { it.model }.filter { it.isNotBlank() }.distinct().sorted()

                    _uiState.update {
                        it.copy(
                            allBrands = allBrands,
                            allModels = allModels
                        )
                    }
                    applyFilter()
                }.launchIn(viewModelScope)
            }.launch()
    }

    private fun applyFilter() {
        val state = _uiState.value
        val q = state.query.trim()
        val f = state.filters

        var list = allProducts.filter { p ->
            if (q.isEmpty()) true else
                p.name.contains(q, true)
        }

        if (f.selectedBrands.isNotEmpty())
            list = list.filter { it.brand in f.selectedBrands }
        if (f.selectedModels.isNotEmpty())
            list = list.filter { it.model in f.selectedModels }

        list = when (f.sort) {
            SortOption.OldToNew      -> list.sortedBy { it.createdAt }
            SortOption.NewToOld      -> list.sortedByDescending { it.createdAt }
            SortOption.PriceHighToLow-> list.sortedByDescending { it.price }
            SortOption.PriceLowToHigh-> list.sortedBy { it.price }
        }

        _uiState.update {
            it.copy(
                products = list,
                filterCount = f.selectedModels.size + f.selectedBrands.size,
                uiStatus = if (list.isEmpty()) UiStatus.Empty else UiStatus.ShowContent
            )
        }
    }

    fun add(p: ProductUiModel) {
        viewModelScope.launch {
            addToCart(
                Product(
                    id = p.id,
                    name = p.name,
                    price = p.price,
                    image = p.image,
                    model = p.model,
                    brand = p.brand,
                    createdAt = p.createdAt,
                    description = p.description
                )
            )
        }
    }

    fun increment(productId: String) {
        viewModelScope.launch { incrementItem(productId) }
    }

    fun decrement(product: ProductUiModel) {
        viewModelScope.launch {
            if (product.quantity > 1) decrementItem(product.id) else {
                removeFromCart(product.id)
            }
        }
    }

    fun toggleFavorite(p: ProductUiModel) = viewModelScope.launch {
        toggleFavoriteUseCase(
            FavoriteItem(
                productId = p.id,
                title = p.name,
                price = p.price,
                image = p.image,
                brand = p.brand,
                model = p.model,
                createdAt = p.createdAt,
                description = p.description
            )
        )
    }

    fun onQuery(q: String) {
        _uiState.update { it.copy(query = q) }
        queryFlow.value = q
    }

    fun onFilterChange(reducer: (FiltersState) -> FiltersState) {
        _uiState.update { it.copy(filters = reducer(it.filters)) }
    }

    fun onApplyFilters() = applyFilter()
}

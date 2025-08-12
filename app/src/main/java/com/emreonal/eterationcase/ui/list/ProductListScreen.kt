package com.emreonal.eterationcase.ui.list

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emreonal.eterationcase.core.toTl
import com.emreonal.eterationcase.ui.component.EmptyComponent
import com.emreonal.eterationcase.ui.component.ErrorComponent
import com.emreonal.eterationcase.ui.component.EterationAsyncImage
import com.emreonal.eterationcase.ui.component.FavoriteComponent
import com.emreonal.eterationcase.ui.component.LoadingComponent
import com.emreonal.eterationcase.ui.component.UiStatus
import com.emreonal.eterationcase.ui.list.filter.FilterSheetHost
import com.emreonal.eterationcase.ui.theme.Black
import com.emreonal.eterationcase.ui.theme.Gray
import com.emreonal.eterationcase.ui.theme.Main
import com.emreonal.eterationcase.ui.theme.White

@Composable
fun ProductListRootScreen(
    onOpenDetail: (ProductUiModel) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.uiStatus) {
        is UiStatus.Error -> {
            ErrorComponent(
                message = (uiState.uiStatus as UiStatus.Error).message,
                isRetry = true,
                onRetry = viewModel::retry
            )
        }

        UiStatus.Loading -> {
            LoadingComponent()
        }

        UiStatus.ShowContent, UiStatus.Empty -> {
            ProductListScreen(
                uiState = uiState,
                viewModel = viewModel,
                isEmpty = uiState.uiStatus == UiStatus.Empty,
                onOpenDetail = onOpenDetail
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ProductListScreen(
    uiState: ProductListUiState,
    viewModel: ProductListViewModel,
    isEmpty: Boolean,
    onOpenDetail: (ProductUiModel) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()
    var didInitialSnap by rememberSaveable { mutableStateOf(false) }
    var pendingScrollToTop by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.uiStatus) {
        if (!didInitialSnap && uiState.uiStatus == UiStatus.ShowContent) {
            gridState.scrollToItem(0)
            didInitialSnap = true
        }
    }

    LaunchedEffect(uiState.products, pendingScrollToTop) {
        if (pendingScrollToTop && uiState.uiStatus == UiStatus.ShowContent) {
            gridState.scrollToItem(0)
            pendingScrollToTop = false
        }
    }

    if (showFilterSheet) {
        FilterSheetHost(
            uiState = uiState,
            onChange = viewModel::onFilterChange,
            onApply = {
                viewModel.onApplyFilters()
                pendingScrollToTop = true
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            value = uiState.query,
            onValueChange = {
                viewModel.onQuery(it)
                pendingScrollToTop = true
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Filters: ${
                    if (uiState.filterCount > 0) {
                        uiState.filterCount
                    } else "None"
                }",
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = { showFilterSheet = true }) {
                Text(
                    text = "Select Filter",
                    color = Black
                )
            }
        }

        if (isEmpty) {
            EmptyComponent(message = "Product not found.")
        } else {
            Spacer(Modifier.height(12.dp))

            BoxWithConstraints(Modifier.fillMaxSize()) {
                val columns = 2
                val vSpacing = 16.dp
                val hSpacing = 16.dp
                val rowsVisible = 2

                val cellHeight = (maxHeight - vSpacing * (rowsVisible - 1)) / rowsVisible

                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(vSpacing),
                    horizontalArrangement = Arrangement.spacedBy(hSpacing)
                ) {
                    items(uiState.products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            cellHeight = cellHeight,
                            onOpenDetail = onOpenDetail,
                            onAddToCart = viewModel::add,
                            onIncrement = viewModel::increment,
                            onDecrement = viewModel::decrement,
                            onFavoriteToggle = viewModel::toggleFavorite
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductUiModel,
    cellHeight: Dp,
    onOpenDetail: (ProductUiModel) -> Unit,
    onAddToCart: (ProductUiModel) -> Unit,
    onIncrement: (id: String) -> Unit,
    onDecrement: (ProductUiModel) -> Unit,
    onFavoriteToggle: (ProductUiModel) -> Unit
) {
    ElevatedCard(
        onClick = { onOpenDetail(product) },
        modifier = Modifier
            .fillMaxWidth()
            .height(cellHeight)
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.TopEnd
            ) {
                EterationAsyncImage(
                    imageUrl = product.image,
                )

                FavoriteComponent(
                    isFavorite = product.isFavorite,
                    onFavoriteToggle = {
                        onFavoriteToggle(product)
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = product.price.toTl(),
                color = Main,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = product.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.height(8.dp))

            if (product.quantity > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = { onDecrement(product) },
                            contentPadding = PaddingValues(0.dp)
                        ) { Text("-") }
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Main),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(product.quantity.toString(), color = White)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = { onIncrement(product.id) },
                            contentPadding = PaddingValues(0.dp)
                        ) { Text("+") }
                    }
                }
            } else {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Main),
                    onClick = { onAddToCart(product) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Add to Cart")
                }
            }
        }
    }
}

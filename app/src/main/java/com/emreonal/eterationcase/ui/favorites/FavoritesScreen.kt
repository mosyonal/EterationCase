package com.emreonal.eterationcase.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emreonal.eterationcase.core.toTl
import com.emreonal.eterationcase.domain.model.FavoriteItem
import com.emreonal.eterationcase.domain.model.toProductUiModel
import com.emreonal.eterationcase.ui.component.EmptyComponent
import com.emreonal.eterationcase.ui.component.ErrorComponent
import com.emreonal.eterationcase.ui.component.EterationAsyncImage
import com.emreonal.eterationcase.ui.component.LoadingComponent
import com.emreonal.eterationcase.ui.component.UiStatus
import com.emreonal.eterationcase.ui.list.ProductUiModel
import com.emreonal.eterationcase.ui.theme.Main
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onOpenDetail: (ProductUiModel) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    when (uiState.uiStatus) {
        UiStatus.Empty -> {
            EmptyComponent("Favorites is empty.")
        }

        is UiStatus.Error -> {
            ErrorComponent((uiState.uiStatus as UiStatus.Error).message)
        }

        UiStatus.Loading -> LoadingComponent()

        UiStatus.ShowContent -> {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            ) { inner ->
                LazyColumn(
                    modifier = Modifier.padding(inner),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.favorites, key = { it.productId }) { item ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            positionalThreshold = { totalDistance -> totalDistance * 0.40f },
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.remove(item.productId); true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .padding(end = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            },
                            content = {
                                FavoriteRow(
                                    item = item,
                                    onClick = { onOpenDetail(item.toProductUiModel()) },
                                    onAddToCart = {
                                        viewModel.addToCart(item)
                                        scope.launch { snackbarHostState.showSnackbar("Added to cart") }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteRow(
    item: FavoriteItem,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 92.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                EterationAsyncImage(
                    imageUrl = item.image,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(item.price.toTl(), style = MaterialTheme.typography.labelLarge, color = Main)
            }

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .height(40.dp)
                    .defaultMinSize(minWidth = 0.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Main)
            ) {
                Text("Add to Cart")
            }
        }
    }
}

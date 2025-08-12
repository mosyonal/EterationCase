package com.emreonal.eterationcase.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emreonal.eterationcase.core.toTl
import com.emreonal.eterationcase.ui.component.EterationAsyncImage
import com.emreonal.eterationcase.ui.component.FavoriteComponent
import com.emreonal.eterationcase.ui.list.ProductUiModel
import com.emreonal.eterationcase.ui.theme.Black
import com.emreonal.eterationcase.ui.theme.Main
import com.emreonal.eterationcase.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun ProductDetailRootScreen(
    onBack: () -> Unit,
    product: ProductUiModel,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(product.id) {
        viewModel.setProduct(product)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onToggleFavorite = { viewModel.toggleFavorite() },
        onAddToCart = { viewModel.addToCart() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    uiState: ProductDetailUiState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                title = {
                    Text(
                        text = uiState.product?.name.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Main
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Price:", style = MaterialTheme.typography.labelLarge, color = Black)
                        Text(
                            text = uiState.product?.price?.toTl().orEmpty(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Main
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            onAddToCart()
                            scope.launch { snackbarHostState.showSnackbar("Added to cart") }
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .defaultMinSize(minWidth = 160.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Main)
                    ) {
                        Text("Add to Cart")
                    }
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                EterationAsyncImage(
                    imageUrl = uiState.product?.image,
                    modifier = Modifier.fillMaxSize()
                )

                FavoriteComponent(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    isFavorite = uiState.isFavorite,
                    onFavoriteToggle = onToggleFavorite
                )
            }

            Text(
                text = uiState.product?.name.orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = uiState.product?.description.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}


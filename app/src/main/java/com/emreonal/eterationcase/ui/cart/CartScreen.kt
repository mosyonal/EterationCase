package com.emreonal.eterationcase.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.emreonal.eterationcase.R
import com.emreonal.eterationcase.core.toTl
import com.emreonal.eterationcase.domain.model.CartItem
import com.emreonal.eterationcase.ui.component.EmptyComponent
import com.emreonal.eterationcase.ui.component.ErrorComponent
import com.emreonal.eterationcase.ui.component.EterationAsyncImage
import com.emreonal.eterationcase.ui.component.LoadingComponent
import com.emreonal.eterationcase.ui.component.UiStatus
import com.emreonal.eterationcase.ui.theme.Gray
import com.emreonal.eterationcase.ui.theme.Main
import com.emreonal.eterationcase.ui.theme.White

@Composable
fun CartScreen(
    onComplete: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSuccess by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Total:")
                        Text(
                            text = uiState.totalPrice.toTl(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Main
                        )
                    }
                    Button(
                        onClick = {
                            showSuccess = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Main),
                        enabled = uiState.items.isNotEmpty(),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Complete")
                    }
                }
            }
        }
    ) { padding ->
        when(uiState.uiStatus) {
            UiStatus.Empty -> {
                EmptyComponent(
                    "Cart is empty."
                )
            }
            is UiStatus.Error -> {
                ErrorComponent((uiState.uiStatus as UiStatus.Error).message)
            }
            UiStatus.Loading -> {
                LoadingComponent()
            }
            UiStatus.ShowContent -> {
                LazyColumn(Modifier.padding(padding)) {
                    items(uiState.items, key = { it.productId }) { item ->
                        CartRow(
                            item = item,
                            onIncrement = { viewModel.increment(item.productId) },
                            onDecrement = { viewModel.decrement(item) }
                        )
                        HorizontalDivider()
                    }
                    item {
                        if (uiState.items.isNotEmpty()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        viewModel.clear()
                                    })
                                {
                                    Text(text = "Clear cart", color = Main)
                                }
                            }
                        }
                    }
                }

                PurchaseSuccessDialog(
                    visible = showSuccess,
                    onOk = {
                        showSuccess = false
                        onComplete()
                    }
                )
            }
        }
    }
}

@Composable
private fun CartRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EterationAsyncImage(
            imageUrl = item.image,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.title, fontWeight = FontWeight.Medium)
            Text(
                text = item.price.toTl(),
                style = MaterialTheme.typography.labelLarge,
                color = Main
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Gray),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = onDecrement,
                    contentPadding = PaddingValues(0.dp)
                ) { Text("-") }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Main),
                contentAlignment = Alignment.Center
            ) {
                Text(item.quantity.toString(), color = White)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Gray),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = onIncrement,
                    contentPadding = PaddingValues(0.dp)
                ) { Text("+") }
            }
        }
    }
}

@Composable
private fun PurchaseSuccessDialog(
    visible: Boolean,
    onOk: () -> Unit
) {
    if (!visible) return

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.success)
                )

                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = 1
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(160.dp)
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    "Successfully purchased!",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onOk,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = Main)
                ) { Text("OK") }
            }
        }
    }
}
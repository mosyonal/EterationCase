package com.emreonal.eterationcase.ui.list.filter

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emreonal.eterationcase.ui.list.ProductListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheetHost(
    uiState: ProductListUiState,
    onChange: ((FiltersState) -> FiltersState) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState,
        dragHandle = null
    ) {
        FilterSheetContent(
            state = uiState.filters,
            allBrands = uiState.allBrands,
            allModels = uiState.allModels,
            onChange = onChange,
            onApply = {
                onApply()
                onDismiss()
            },
            onClose = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheetContent(
    state: FiltersState,
    allBrands: List<String>,
    allModels: List<String>,
    onChange: ((FiltersState) -> FiltersState) -> Unit,
    onApply: () -> Unit,
    onClose: () -> Unit
) {
    val filteredBrands by remember(allBrands, state.brandQuery) {
        derivedStateOf {
            val q = state.brandQuery.trim()
            if (q.isEmpty()) allBrands else allBrands.filter { it.contains(q, true) }
        }
    }
    val filteredModels by remember(allModels, state.modelQuery) {
        derivedStateOf {
            val q = state.modelQuery.trim()
            if (q.isEmpty()) allModels else allModels.filter { it.contains(q, true) }
        }
    }

    val maxListHeight = (LocalConfiguration.current.screenHeightDp.dp * 0.33f)

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Filters") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onChange { FiltersState() } },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Clear") }
                    Button(
                        onClick = onApply,
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Apply") }
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Sort By", style = MaterialTheme.typography.titleMedium)
            SortOption.entries.forEach { opt ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.sort == opt,
                        onClick = { onChange { it.copy(sort = opt) } }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(opt.title)
                }
            }

            HorizontalDivider()

            Text("Brand", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = state.brandQuery,
                onValueChange = { q -> onChange { it.copy(brandQuery = q) } },
                placeholder = { Text("Search brand") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp, max = maxListHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            ) {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(filteredBrands, key = { it }) { b ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = b in state.selectedBrands,
                                onCheckedChange = { checked ->
                                    onChange {
                                        it.copy(
                                            selectedBrands = if (checked) it.selectedBrands + b else it.selectedBrands - b
                                        )
                                    }
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(b, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            HorizontalDivider()

            Text("Model", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = state.modelQuery,
                onValueChange = { q -> onChange { it.copy(modelQuery = q) } },
                placeholder = { Text("Search model") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp, max = maxListHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            ) {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(filteredModels, key = { it }) { m ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = m in state.selectedModels,
                                onCheckedChange = { checked ->
                                    onChange {
                                        it.copy(
                                            selectedModels = if (checked) it.selectedModels + m else it.selectedModels - m
                                        )
                                    }
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(m, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}


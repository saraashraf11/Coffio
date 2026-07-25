package com.coffeehub.pos.presentation.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brewpoint.pos.R
import com.coffeehub.pos.presentation.components.EmptyState
import com.coffeehub.pos.presentation.components.ProductCard
import com.coffeehub.pos.utils.getGridColumns

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    windowSizeClass: WindowSizeClass,
    onNavigateBack: () -> Unit,
    onAddProduct: (() -> Unit)? = null,
    onEditProduct: ((Int) -> Unit)? = null,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val columns = windowSizeClass.getGridColumns()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.add_product)) },
                icon = { Icon(Icons.Default.Add, null) },
                onClick = { onAddProduct?.invoke() }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text(stringResource(R.string.search_menu)) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.products.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.MenuBook,
                    title = stringResource(R.string.no_products_title),
                    subtitle = stringResource(R.string.no_products_subtitle),
                    actionLabel = stringResource(R.string.add_product),
                    onAction = { onAddProduct?.invoke() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            onAddToCart = { onEditProduct?.invoke(product.id) }
                        )
                    }
                }
            }
        }
    }
}

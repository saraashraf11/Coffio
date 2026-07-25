package com.coffeehub.pos.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.coffeehub.pos.domain.model.OrderStatus
import com.coffeehub.pos.presentation.components.EmptyState
import com.coffeehub.pos.presentation.components.OrderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    windowSizeClass: WindowSizeClass,
    onOrderClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val statusFilters = listOf(null) + OrderStatus.values().toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.orders), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearch,
                placeholder = { Text(stringResource(R.string.search_orders)) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true
            )
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(statusFilters.size) { idx ->
                    val status = statusFilters[idx]
                    FilterChip(
                        selected = uiState.selectedStatus == status,
                        onClick = { viewModel.filterByStatus(status) },
                        label = { Text(status?.name?.replace("_", " ") ?: stringResource(R.string.all)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.orders.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.ReceiptLong,
                    title = stringResource(R.string.no_orders_title),
                    subtitle = stringResource(R.string.no_orders_subtitle),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.orders) { order ->
                        OrderCard(order = order, onClick = { onOrderClick(order.orderId) })
                    }
                }
            }
        }
    }
}

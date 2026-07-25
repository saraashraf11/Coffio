package com.coffeehub.pos.presentation.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.coffeehub.pos.presentation.components.CartItemRow
import com.coffeehub.pos.presentation.components.EmptyState
import com.coffeehub.pos.utils.toCurrencyString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToPayment: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cart (${uiState.cartItems.size})",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        TextButton(onClick = viewModel::clearCart) {
                            Text("Clear", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.cartItems.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                            Text(uiState.subtotal.toCurrencyString())
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tax (${(uiState.taxRate * 100).toInt()}%)", style = MaterialTheme.typography.bodyMedium)
                            Text(uiState.tax.toCurrencyString())
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(
                                uiState.total.toCurrencyString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToPayment,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.Payment, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Proceed to Payment", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.cartItems.isEmpty()) {
            EmptyState(
                icon = Icons.Default.ShoppingCart,
                title = "Your cart is empty",
                subtitle = "Add items from the menu to start an order",
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.cartItems, key = { it.id }) { item ->
                    CartItemRow(
                        cartItem = item,
                        onIncrease = { viewModel.increaseQuantity(item.id) },
                        onDecrease = { viewModel.decreaseQuantity(item.id) },
                        onRemove = { viewModel.removeItem(item.id) }
                    )
                }
            }
        }
    }
}

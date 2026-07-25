package com.coffeehub.pos.presentation.cashier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
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
import com.coffeehub.pos.domain.model.OrderType
import com.coffeehub.pos.domain.model.Product
import com.coffeehub.pos.presentation.cart.CartUiState
import com.coffeehub.pos.presentation.cart.CartViewModel
import com.coffeehub.pos.presentation.components.CartItemRow
import com.coffeehub.pos.presentation.components.CategoryChip
import com.coffeehub.pos.presentation.components.ProductCard
import com.coffeehub.pos.utils.getGridColumns
import com.coffeehub.pos.utils.isCompact
import com.coffeehub.pos.utils.toCurrencyString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierScreen(
    windowSizeClass: WindowSizeClass,
    onNavigateToPayment: () -> Unit,
    onNavigateBack: () -> Unit,
    cashierViewModel: CashierViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val cashierState by cashierViewModel.uiState.collectAsState()
    val cartState by cartViewModel.uiState.collectAsState()
    val isCompact = windowSizeClass.isCompact()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(cartState.lastOrderId) {
        if (cartState.lastOrderId != null) onNavigateToPayment()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cashier), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    Row {
                        OrderType.values().forEach { type ->
                            FilterChip(
                                selected = cartState.orderType == type,
                                onClick = { cartViewModel.setOrderType(type) },
                                label = { Text(type.name.replace("_", " "), style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isCompact) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                        text = { Text(stringResource(R.string.menu_tab)) },
                        icon = { Icon(Icons.Default.MenuBook, null) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                        text = { Text(stringResource(R.string.cart_tab, cartState.cartItems.size)) },
                        icon = { Icon(Icons.Default.ShoppingCart, null) })
                }
                when (selectedTab) {
                    0 -> MenuPanel(cashierState = cashierState, onCategorySelect = cashierViewModel::selectCategory,
                        onSearch = cashierViewModel::onSearch, onAddToCart = cartViewModel::addProduct, columns = 2)
                    1 -> CartPanel(cartState = cartState, onIncrease = cartViewModel::increaseQuantity,
                        onDecrease = cartViewModel::decreaseQuantity, onRemove = cartViewModel::removeItem,
                        onClear = cartViewModel::clearCart, onCheckout = onNavigateToPayment)
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Box(modifier = Modifier.weight(1.5f).fillMaxHeight()) {
                    MenuPanel(cashierState = cashierState, onCategorySelect = cashierViewModel::selectCategory,
                        onSearch = cashierViewModel::onSearch, onAddToCart = cartViewModel::addProduct,
                        columns = windowSizeClass.getGridColumns())
                }
                VerticalDivider()
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    CartPanel(cartState = cartState, onIncrease = cartViewModel::increaseQuantity,
                        onDecrease = cartViewModel::decreaseQuantity, onRemove = cartViewModel::removeItem,
                        onClear = cartViewModel::clearCart, onCheckout = onNavigateToPayment)
                }
            }
        }
    }
}

@Composable
private fun MenuPanel(
    cashierState: CashierUiState,
    onCategorySelect: (Int?) -> Unit,
    onSearch: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    columns: Int
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = cashierState.searchQuery,
            onValueChange = onSearch,
            placeholder = { Text(stringResource(R.string.search_products)) },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.extraLarge,
            singleLine = true
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                CategoryChip(category = null, isSelected = cashierState.selectedCategoryId == null,
                    onClick = { onCategorySelect(null) }, label = stringResource(R.string.all))
            }
            items(cashierState.categories) { category ->
                CategoryChip(category = category, isSelected = cashierState.selectedCategoryId == category.id,
                    onClick = { onCategorySelect(category.id) })
            }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(columns), contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()) {
            items(cashierState.products) { product ->
                ProductCard(product = product, onAddToCart = onAddToCart)
            }
        }
    }
}

@Composable
private fun CartPanel(
    cartState: CartUiState,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClear: () -> Unit,
    onCheckout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.current_order), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (cartState.cartItems.isNotEmpty()) {
                TextButton(onClick = onClear) { Text(stringResource(R.string.clear_all), color = MaterialTheme.colorScheme.error) }
            }
        }
        if (cartState.cartItems.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingCartCheckout, null, modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(R.string.cart_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)) {
                items(cartState.cartItems.size) { idx ->
                    val item = cartState.cartItems[idx]
                    CartItemRow(cartItem = item, onIncrease = { onIncrease(item.id) },
                        onDecrease = { onDecrease(item.id) }, onRemove = { onRemove(item.id) })
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.subtotal), style = MaterialTheme.typography.bodyMedium)
                    Text(cartState.subtotal.toCurrencyString(), style = MaterialTheme.typography.bodyMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.tax, (cartState.taxRate * 100).toInt()), style = MaterialTheme.typography.bodyMedium)
                    Text(cartState.tax.toCurrencyString(), style = MaterialTheme.typography.bodyMedium)
                }
                if (cartState.discount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.discount), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                        Text("-${cartState.discount.toCurrencyString()}", color = MaterialTheme.colorScheme.error)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(cartState.total.toCurrencyString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = cartState.cartItems.isNotEmpty() && !cartState.isPlacingOrder, shape = MaterialTheme.shapes.medium) {
            if (cartState.isPlacingOrder) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(Icons.Default.Payment, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.proceed_to_payment), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

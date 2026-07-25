package com.coffeehub.pos.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.coffeehub.pos.domain.model.OrderStatus
import com.coffeehub.pos.utils.toFormattedDateTime
import com.coffeehub.pos.utils.toCurrencyString
import com.coffeehub.pos.presentation.theme.ErrorRed
import com.coffeehub.pos.presentation.theme.InfoBlue
import com.coffeehub.pos.presentation.theme.SuccessGreen
import com.coffeehub.pos.presentation.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onNavigateBack: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val order = uiState.orders.find { it.orderId == orderId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(order?.orderNumber ?: "Order Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Order header
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(order.orderNumber, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Text(order.createdAt.toFormattedDateTime(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            val statusColor = when (order.status) {
                                OrderStatus.COMPLETED -> SuccessGreen
                                OrderStatus.CANCELLED -> ErrorRed
                                OrderStatus.READY -> SuccessGreen
                                OrderStatus.IN_PROGRESS -> InfoBlue
                                else -> WarningAmber
                            }
                            Box(
                                modifier = Modifier.background(statusColor.copy(alpha = 0.15f), MaterialTheme.shapes.medium).padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(order.status.name.replace("_", " "), color = statusColor, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            InfoChip(label = "Type", value = order.orderType.name.replace("_", " "))
                            if (order.tableNumber != null) InfoChip(label = "Table", value = "#${order.tableNumber}")
                            InfoChip(label = "Payment", value = order.paymentMethod.name)
                        }
                    }
                }
            }

            item { Text("Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            items(order.items) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.productName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text("${item.selectedSize} · ${item.selectedTemperature} · ${item.selectedMilkType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("x${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(item.subtotal.toCurrencyString(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                // Total summary
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        SummaryRow("Subtotal", order.subtotal.toCurrencyString())
                        SummaryRow("Tax", order.tax.toCurrencyString())
                        if (order.discount > 0) SummaryRow("Discount", "-${order.discount.toCurrencyString()}", MaterialTheme.colorScheme.error)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        SummaryRow("Total", order.total.toCurrencyString(), MaterialTheme.colorScheme.primary, isBold = true)
                        if (order.cashReceived > 0) {
                            SummaryRow("Cash Received", order.cashReceived.toCurrencyString())
                            SummaryRow("Change", order.change.toCurrencyString(), SuccessGreen)
                        }
                    }
                }
            }

            // Status update actions
            item {
                if (order.status == OrderStatus.PENDING || order.status == OrderStatus.IN_PROGRESS) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (order.status == OrderStatus.PENDING) {
                            Button(
                                onClick = { viewModel.updateOrderStatus(orderId, OrderStatus.IN_PROGRESS) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Mark In Progress")
                            }
                        }
                        if (order.status == OrderStatus.IN_PROGRESS) {
                            Button(
                                onClick = { viewModel.updateOrderStatus(orderId, OrderStatus.READY) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Mark Ready")
                            }
                        }
                        OutlinedButton(
                            onClick = { viewModel.updateOrderStatus(orderId, OrderStatus.CANCELLED) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SummaryRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = color, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}

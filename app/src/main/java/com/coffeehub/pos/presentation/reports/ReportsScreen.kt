package com.coffeehub.pos.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brewpoint.pos.R
import com.coffeehub.pos.presentation.components.StatCard
import com.coffeehub.pos.utils.toCurrencyString
import com.coffeehub.pos.presentation.theme.CreamWhite
import com.coffeehub.pos.presentation.theme.EspressoBrown
import com.coffeehub.pos.presentation.theme.EspressoBrownLight
import com.coffeehub.pos.presentation.theme.InfoBlue
import com.coffeehub.pos.presentation.theme.LatteCaramel
import com.coffeehub.pos.presentation.theme.LatteCaramelLight
import com.coffeehub.pos.presentation.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    windowSizeClass: WindowSizeClass,
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reports), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = uiState.selectedPeriod.ordinal) {
                ReportPeriod.values().forEach { period ->
                    Tab(selected = uiState.selectedPeriod == period,
                        onClick = { viewModel.selectPeriod(period) },
                        text = { Text(period.name.lowercase().replaceFirstChar { it.uppercase() }) })
                }
            }
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                val report = uiState.report
                if (report == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_data), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    StatCard(title = stringResource(R.string.total_revenue), value = report.totalRevenue.toCurrencyString(),
                                        icon = Icons.Default.AttachMoney, gradient = listOf(EspressoBrown, EspressoBrownLight), modifier = Modifier.weight(1f))
                                    StatCard(title = stringResource(R.string.total_orders), value = report.totalOrders.toString(),
                                        icon = Icons.Default.Receipt, gradient = listOf(LatteCaramel, LatteCaramelLight), modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    StatCard(title = stringResource(R.string.avg_order_value), value = report.averageOrderValue.toCurrencyString(),
                                        icon = Icons.Default.TrendingUp, gradient = listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.7f)), modifier = Modifier.weight(1f))
                                    StatCard(title = stringResource(R.string.top_category),
                                        value = report.revenueByCategory.maxByOrNull { it.revenue }?.categoryName ?: "-",
                                        icon = Icons.Default.Category, gradient = listOf(InfoBlue, InfoBlue.copy(alpha = 0.7f)), modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        item { Text(stringResource(R.string.revenue_by_category), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                        items(report.revenueByCategory) { category ->
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
                                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(category.categoryName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text(category.revenue.toCurrencyString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(progress = { category.percentage }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(MaterialTheme.shapes.small), color = MaterialTheme.colorScheme.primary)
                                    Text(stringResource(R.string.percent_of_total, (category.percentage * 100).toInt()), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        item { Text(stringResource(R.string.top_products), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                        items(report.topProducts.take(5).withIndex().toList()) { (index, product) ->
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(36.dp).background(Brush.linearGradient(listOf(EspressoBrown, LatteCaramel)), shape = MaterialTheme.shapes.medium), contentAlignment = Alignment.Center) {
                                        Text("#${index + 1}", style = MaterialTheme.typography.labelMedium, color = CreamWhite, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(product.productName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text(stringResource(R.string.sold_count, product.quantitySold), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(product.revenue.toCurrencyString(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        item { Text(stringResource(R.string.payment_methods), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                        items(report.paymentMethodBreakdown.entries.toList()) { (method, amount) ->
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(when (method) { "CASH" -> Icons.Default.Money; "CARD" -> Icons.Default.CreditCard; else -> Icons.Default.QrCode },
                                            contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(method, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Text(amount.toCurrencyString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

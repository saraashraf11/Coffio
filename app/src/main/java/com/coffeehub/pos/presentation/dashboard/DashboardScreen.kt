package com.coffeehub.pos.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brewpoint.pos.R
import com.coffeehub.pos.presentation.components.OrderCard
import com.coffeehub.pos.presentation.components.StatCard
import com.coffeehub.pos.presentation.navigation.Screen
import com.coffeehub.pos.utils.isCompact
import com.coffeehub.pos.utils.toCurrencyString
import com.coffeehub.pos.presentation.theme.EspressoBrown
import com.coffeehub.pos.presentation.theme.EspressoBrownLight
import com.coffeehub.pos.presentation.theme.InfoBlue
import com.coffeehub.pos.presentation.theme.LatteCaramel
import com.coffeehub.pos.presentation.theme.LatteCaramelLight
import com.coffeehub.pos.presentation.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    windowSizeClass: WindowSizeClass,
    onNavigateTo: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isCompact = windowSizeClass.isCompact()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.good_day, uiState.userName.ifEmpty { "Barista" }),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.userRole.ifEmpty { "CASHIER" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Brush.linearGradient(listOf(EspressoBrown, LatteCaramel))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateTo(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = stringResource(R.string.logout))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (isCompact) {
                DashboardBottomNav(onNavigateTo = onNavigateTo)
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (isCompact) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item { DashboardStatsGrid(uiState = uiState, columns = 2) }
                item { QuickActionsRow(onNavigateTo = onNavigateTo) }
                item {
                    Text(
                        stringResource(R.string.recent_orders),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(uiState.recentOrders) { order ->
                    OrderCard(order = order, onClick = {
                        onNavigateTo(Screen.OrderDetail.createRoute(order.orderId))
                    })
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Spacer(Modifier.weight(1f))
                    NavRailItem(Icons.Default.Dashboard, stringResource(R.string.nav_dashboard), Screen.Dashboard.route, onNavigateTo)
                    NavRailItem(Icons.Default.PointOfSale, stringResource(R.string.nav_cashier), Screen.Cashier.route, onNavigateTo)
                    NavRailItem(Icons.Default.MenuBook, stringResource(R.string.nav_menu), Screen.Menu.route, onNavigateTo)
                    NavRailItem(Icons.Default.Receipt, stringResource(R.string.nav_orders), Screen.Orders.route, onNavigateTo)
                    NavRailItem(Icons.Default.TableBar, stringResource(R.string.nav_tables), Screen.Tables.route, onNavigateTo)
                    NavRailItem(Icons.Default.People, stringResource(R.string.nav_customers), Screen.Customers.route, onNavigateTo)
                    NavRailItem(Icons.Default.BarChart, stringResource(R.string.nav_reports), Screen.Reports.route, onNavigateTo)
                    Spacer(Modifier.weight(1f))
                    NavRailItem(Icons.Default.Logout, stringResource(R.string.logout), "", null)
                }
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 24.dp)
                ) {
                    item { DashboardStatsGrid(uiState = uiState, columns = 4) }
                    item { QuickActionsRow(onNavigateTo = onNavigateTo) }
                    item {
                        Text(
                            stringResource(R.string.recent_orders),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.recentOrders) { order ->
                        OrderCard(order = order, onClick = {
                            onNavigateTo(Screen.OrderDetail.createRoute(order.orderId))
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardStatsGrid(uiState: DashboardUiState, columns: Int) {
    val todayRevenueLabel = stringResource(R.string.today_revenue)
    val totalOrdersLabel = stringResource(R.string.total_orders)
    val activeOrdersLabel = stringResource(R.string.active_orders)
    val avgOrderLabel = stringResource(R.string.avg_order)

    val stats = listOf(
        Triple(todayRevenueLabel, uiState.todayRevenue.toCurrencyString(), Icons.Default.AttachMoney),
        Triple(totalOrdersLabel, uiState.todayOrders.toString(), Icons.Default.Receipt),
        Triple(activeOrdersLabel, uiState.activeOrders.toString(), Icons.Default.HourglassTop),
        Triple(avgOrderLabel, (if (uiState.todayOrders > 0) uiState.todayRevenue / uiState.todayOrders else 0.0).toCurrencyString(), Icons.Default.TrendingUp)
    )
    val gradients = listOf(
        listOf(EspressoBrown, EspressoBrownLight),
        listOf(LatteCaramel, LatteCaramelLight),
        listOf(SuccessGreen, SuccessGreen.copy(0.7f)),
        listOf(InfoBlue, InfoBlue.copy(0.7f))
    )
    if (columns == 2) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            stats.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEachIndexed { _, (title, value, icon) ->
                        val globalIdx = stats.indexOfFirst { it.first == title }
                        StatCard(title = title, value = value, icon = icon, iconTint = Color.White, gradient = gradients[globalIdx], modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            stats.forEachIndexed { idx, (title, value, icon) ->
                StatCard(title = title, value = value, icon = icon, iconTint = Color.White, gradient = gradients[idx], modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QuickActionsRow(onNavigateTo: (String) -> Unit) {
    val newOrderLabel = stringResource(R.string.new_order)
    val tablesLabel = stringResource(R.string.nav_tables)
    val customersLabel = stringResource(R.string.nav_customers)
    val menuLabel = stringResource(R.string.nav_menu)
    val reportsLabel = stringResource(R.string.nav_reports)

    val actions = remember(newOrderLabel, tablesLabel, customersLabel, menuLabel, reportsLabel) {
        listOf(
            Triple(Icons.Default.PointOfSale, newOrderLabel, Screen.Cashier.route),
            Triple(Icons.Default.TableBar, tablesLabel, Screen.Tables.route),
            Triple(Icons.Default.People, customersLabel, Screen.Customers.route),
            Triple(Icons.Default.MenuBook, menuLabel, Screen.Menu.route),
            Triple(Icons.Default.BarChart, reportsLabel, Screen.Reports.route)
        )
    }

    Column {
        Text(stringResource(R.string.quick_actions), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(actions.size) { idx ->
                val (icon, label, route) = actions[idx]
                QuickActionCard(icon = icon, label = label, onClick = { onNavigateTo(route) })
            }
        }
    }
}

@Composable
private fun QuickActionCard(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(modifier = Modifier.width(100.dp).clickable { onClick() }, shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(48.dp).clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun NavRailItem(icon: ImageVector, label: String, route: String, onNavigateTo: ((String) -> Unit)?) {
    NavigationRailItem(
        selected = false,
        onClick = { onNavigateTo?.invoke(route) },
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
    )
}

@Composable
private fun DashboardBottomNav(onNavigateTo: (String) -> Unit) {
    NavigationBar {
        val items = listOf(
            Triple(Icons.Default.Dashboard, stringResource(R.string.nav_home), Screen.Dashboard.route),
            Triple(Icons.Default.PointOfSale, stringResource(R.string.nav_cashier), Screen.Cashier.route),
            Triple(Icons.Default.Receipt, stringResource(R.string.nav_orders), Screen.Orders.route),
            Triple(Icons.Default.BarChart, stringResource(R.string.nav_reports), Screen.Reports.route),
            Triple(Icons.Default.Settings, stringResource(R.string.nav_settings), Screen.Settings.route)
        )
        items.forEachIndexed { idx, (icon, label, route) ->
            NavigationBarItem(
                selected = idx == 0,
                onClick = { onNavigateTo(route) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}

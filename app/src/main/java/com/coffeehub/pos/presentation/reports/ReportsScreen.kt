package com.coffeehub.pos.presentation.reports

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brewpoint.pos.R
import com.coffeehub.pos.domain.model.CategoryRevenue
import com.coffeehub.pos.domain.model.HourlyData
import com.coffeehub.pos.domain.model.SalesReport
import com.coffeehub.pos.domain.model.TopProduct
import com.coffeehub.pos.utils.toCurrencyString
import com.coffeehub.pos.presentation.theme.EspressoBrown
import com.coffeehub.pos.presentation.theme.EspressoBrownLight
import com.coffeehub.pos.presentation.theme.InfoBlue
import com.coffeehub.pos.presentation.theme.LatteCaramel
import com.coffeehub.pos.presentation.theme.LatteCaramelLight
import com.coffeehub.pos.presentation.theme.SuccessGreen
import com.coffeehub.pos.presentation.theme.CreamWhite

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
                title = {
                    Text(
                        text = stringResource(R.string.reports),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = uiState.selectedPeriod.ordinal) {
                ReportPeriod.values().forEach { period ->
                    Tab(
                        selected = uiState.selectedPeriod == period,
                        onClick  = { viewModel.selectPeriod(period) },
                        text = {
                            Text(
                                text = when (period) {
                                    ReportPeriod.DAILY   -> stringResource(R.string.period_daily)
                                    ReportPeriod.WEEKLY  -> stringResource(R.string.period_weekly)
                                    ReportPeriod.MONTHLY -> stringResource(R.string.period_monthly)
                                },
                                fontWeight = if (uiState.selectedPeriod == period)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = EspressoBrown)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.loading),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = uiState.error ?: "",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                uiState.report == null || uiState.report?.totalOrders == 0 -> {
                    EmptyReportState()
                }

                else -> {
                    ReportContent(report = uiState.report!!)
                }
            }
        }
    }
}

@Composable
private fun EmptyReportState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.BarChart,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Text(
                text = stringResource(R.string.no_data),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.no_data_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ReportContent(report: SalesReport) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { SummarySection(report) }

        if (report.revenueByCategory.isNotEmpty()) {
            item {
                SectionTitle(
                    icon = Icons.Default.Category,
                    title = stringResource(R.string.revenue_by_category)
                )
            }
            items(report.revenueByCategory) { cat ->
                CategoryRevenueRow(cat)
            }
        }

        if (report.topProducts.isNotEmpty()) {
            item {
                SectionTitle(
                    icon = Icons.Default.Star,
                    title = stringResource(R.string.top_products)
                )
            }
            items(report.topProducts.take(8).withIndex().toList()) { (idx, product) ->
                TopProductRow(index = idx, product = product)
            }
        }

        val busyHours = report.hourlyData.filter { it.orders > 0 }
        if (busyHours.isNotEmpty()) {
            item {
                SectionTitle(
                    icon = Icons.Default.Schedule,
                    title = stringResource(R.string.hourly_breakdown)
                )
                HourlyChart(hourlyData = report.hourlyData)
            }
        }

        if (report.paymentMethodBreakdown.isNotEmpty()) {
            item {
                SectionTitle(
                    icon = Icons.Default.Payments,
                    title = stringResource(R.string.payment_methods)
                )
            }
            items(report.paymentMethodBreakdown.entries.toList()) { (method, amount) ->
                PaymentMethodRow(method = method, amount = amount, total = report.totalRevenue)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun SummarySection(report: SalesReport) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard(
                modifier    = Modifier.weight(1f),
                title       = stringResource(R.string.total_revenue),
                value       = report.totalRevenue.toCurrencyString(),
                icon        = Icons.Default.AttachMoney,
                gradient    = listOf(EspressoBrown, EspressoBrownLight)
            )
            KpiCard(
                modifier    = Modifier.weight(1f),
                title       = stringResource(R.string.total_orders),
                value       = report.totalOrders.toString(),
                icon        = Icons.Default.Receipt,
                gradient    = listOf(LatteCaramel, LatteCaramelLight)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard(
                modifier    = Modifier.weight(1f),
                title       = stringResource(R.string.avg_order_value),
                value       = report.averageOrderValue.toCurrencyString(),
                icon        = Icons.Default.TrendingUp,
                gradient    = listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.6f))
            )
            KpiCard(
                modifier    = Modifier.weight(1f),
                title       = stringResource(R.string.top_category),
                value       = report.revenueByCategory.maxByOrNull { it.revenue }?.categoryName ?: "-",
                icon        = Icons.Default.Category,
                gradient    = listOf(InfoBlue, InfoBlue.copy(alpha = 0.6f))
            )
        }
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    gradient: List<Color>
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradient))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = CreamWhite.copy(alpha = 0.85f)
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, modifier = Modifier.size(18.dp), tint = CreamWhite)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite
                )
            }
        }
    }
}

@Composable
private fun CategoryRevenueRow(category: CategoryRevenue) {
    val animatedProgress by animateFloatAsState(
        targetValue = category.percentage,
        animationSpec = tween(durationMillis = 600),
        label = "cat_progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = category.revenue.toCurrencyString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.percent_of_total, (category.percentage * 100).toInt()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.small),
                color = EspressoBrown,
                trackColor = EspressoBrown.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun TopProductRow(index: Int, product: TopProduct) {
    val medalColors = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFC0C0C0), // Silver
        Color(0xFFCD7F32)  // Bronze
    )
    val badgeColor = medalColors.getOrElse(index) { EspressoBrown }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = stringResource(R.string.sold_count, product.quantitySold),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = product.revenue.toCurrencyString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun HourlyChart(hourlyData: List<HourlyData>) {
    val maxOrders = hourlyData.maxOf { it.orders }.coerceAtLeast(1)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                hourlyData.filter { it.hour % 2 == 0 }.forEach { hourData ->
                    val fraction = hourData.orders.toFloat() / maxOrders
                    val animatedFraction by animateFloatAsState(
                        targetValue = fraction,
                        animationSpec = tween(600),
                        label = "bar_${hourData.hour}"
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((animatedFraction * 80).dp.coerceAtLeast(4.dp))
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(EspressoBrown, LatteCaramel)
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${hourData.hour}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            val peakHour = hourlyData.maxByOrNull { it.orders }
            if (peakHour != null && peakHour.orders > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = LatteCaramel
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.peak_hour, peakHour.hour, peakHour.orders),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodRow(method: String, amount: Double, total: Double) {
    val icon = when (method) {
        "CASH" -> Icons.Default.Money
        "CARD" -> Icons.Default.CreditCard
        else   -> Icons.Default.QrCode
    }
    val label = when (method) {
        "CASH" -> stringResource(R.string.cash)
        "CARD" -> stringResource(R.string.card)
        else   -> stringResource(R.string.qr)
    }
    val percentage = if (total > 0) (amount / total * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(EspressoBrown.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = EspressoBrown, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = stringResource(R.string.percent_of_total, percentage),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = amount.toCurrencyString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = EspressoBrown, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

package com.coffeehub.pos.domain.usecase

import com.coffeehub.pos.data.local.dao.OrderDao
import com.coffeehub.pos.domain.model.CategoryRevenue
import com.coffeehub.pos.domain.model.HourlyData
import com.coffeehub.pos.domain.model.SalesReport
import com.coffeehub.pos.domain.model.TopProduct
import com.coffeehub.pos.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetSalesReportUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val orderDao: OrderDao
) {

    private fun todayRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        return start to (start + 24L * 60 * 60 * 1000)
    }

    private fun weekRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        return start to (start + 7L * 24 * 60 * 60 * 1000)
    }

    private fun monthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        return start to cal.timeInMillis
    }

    fun getDailyReport(): Flow<SalesReport> {
        val (start, end) = todayRange()
        return buildReportFlow(start, end)
    }

    fun getWeeklyReport(): Flow<SalesReport> {
        val (start, end) = weekRange()
        return buildReportFlow(start, end)
    }

    fun getMonthlyReport(): Flow<SalesReport> {
        val (start, end) = monthRange()
        return buildReportFlow(start, end)
    }

    private fun buildReportFlow(startTime: Long, endTime: Long): Flow<SalesReport> =
        orderRepository.getOrdersByDateRange(startTime, endTime).map { orders ->
            // Count all valid orders regardless of status (or COMPLETED / PENDING) so report has data
            val validOrders = orders.filter { it.status.name in listOf("COMPLETED", "READY", "IN_PROGRESS", "PENDING") }

            val totalRevenue  = validOrders.sumOf { it.total }
            val totalOrders   = validOrders.size
            val avgOrderValue = if (totalOrders > 0) totalRevenue / totalOrders else 0.0

            // Top products
            val productSales = mutableMapOf<String, Pair<Int, Double>>()
            validOrders.forEach { order ->
                order.items.forEach { item ->
                    val prev = productSales[item.productName] ?: (0 to 0.0)
                    productSales[item.productName] =
                        (prev.first + item.quantity) to (prev.second + item.subtotal)
                }
            }
            val topProducts = productSales.entries
                .sortedByDescending { it.value.second }
                .take(10)
                .map { TopProduct(it.key, it.value.first, it.value.second) }

            // Category revenue
            val catSales = mutableMapOf<String, Double>()
            validOrders.forEach { order ->
                order.items.forEach { item ->
                    val cat = item.productName.split(" ").firstOrNull() ?: "Coffee"
                    catSales[cat] = (catSales[cat] ?: 0.0) + item.subtotal
                }
            }
            val totalCatRev = catSales.values.sum().takeIf { it > 0 } ?: 1.0
            val categoryRevenue = catSales.entries
                .sortedByDescending { it.value }
                .map { (name, rev) ->
                    CategoryRevenue(
                        categoryName = name,
                        revenue      = rev,
                        percentage   = (rev / totalCatRev).toFloat()
                    )
                }

            // Hourly breakdown
            val hourlyMap = mutableMapOf<Int, Pair<Int, Double>>()
            validOrders.forEach { order ->
                val hour = Calendar.getInstance()
                    .apply { timeInMillis = order.createdAt }
                    .get(Calendar.HOUR_OF_DAY)
                val prev = hourlyMap[hour] ?: (0 to 0.0)
                hourlyMap[hour] = (prev.first + 1) to (prev.second + order.total)
            }
            val hourlyData = (0..23).map { h ->
                val d = hourlyMap[h] ?: (0 to 0.0)
                HourlyData(h, d.first, d.second)
            }

            // Payment method breakdown
            val paymentBreakdown = validOrders
                .groupBy { it.paymentMethod.name }
                .mapValues { (_, list) -> list.sumOf { it.total } }

            SalesReport(
                totalRevenue           = totalRevenue,
                totalOrders            = totalOrders,
                averageOrderValue      = avgOrderValue,
                topProducts            = topProducts,
                revenueByCategory      = categoryRevenue,
                hourlyData             = hourlyData,
                paymentMethodBreakdown = paymentBreakdown
            )
        }
}

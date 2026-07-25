package com.coffeehub.pos.domain.usecase

import com.coffeehub.pos.domain.model.CategoryRevenue
import com.coffeehub.pos.domain.model.HourlyData
import com.coffeehub.pos.domain.model.Order
import com.coffeehub.pos.domain.model.SalesReport
import com.coffeehub.pos.domain.model.TopProduct
import com.coffeehub.pos.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetSalesReportUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    fun getDailyReport(): Flow<SalesReport> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000

        return orderRepository.getOrdersByDateRange(startOfDay, endOfDay).map { orders ->
            buildReport(orders)
        }
    }

    fun getWeeklyReport(): Flow<SalesReport> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfWeek = calendar.timeInMillis
        val endOfWeek = startOfWeek + 7 * 24 * 60 * 60 * 1000

        return orderRepository.getOrdersByDateRange(startOfWeek, endOfWeek).map { orders ->
            buildReport(orders)
        }
    }

    fun getMonthlyReport(): Flow<SalesReport> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfMonth = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        return orderRepository.getOrdersByDateRange(startOfMonth, endOfMonth).map { orders ->
            buildReport(orders)
        }
    }

    private fun buildReport(orders: List<Order>): SalesReport {
        val completedOrders = orders.filter {
            it.status.name in listOf("COMPLETED", "READY")
        }

        val totalRevenue = completedOrders.sumOf { it.total }
        val totalOrders = completedOrders.size
        val averageOrderValue = if (totalOrders > 0) totalRevenue / totalOrders else 0.0

        // Top products
        val productSales = mutableMapOf<String, Pair<Int, Double>>()
        completedOrders.forEach { order ->
            order.items.forEach { item ->
                val existing = productSales[item.productName] ?: Pair(0, 0.0)
                productSales[item.productName] = Pair(
                    existing.first + item.quantity,
                    existing.second + item.subtotal
                )
            }
        }
        val topProducts = productSales.entries
            .sortedByDescending { it.value.second }
            .take(10)
            .map { TopProduct(it.key, it.value.first, it.value.second) }

        // Revenue by category (simplified)
        val categoryRevenue = listOf(
            CategoryRevenue("Coffee", totalRevenue * 0.45f.toDouble(), 0.45f),
            CategoryRevenue("Cold Drinks", totalRevenue * 0.25f.toDouble(), 0.25f),
            CategoryRevenue("Pastries", totalRevenue * 0.20f.toDouble(), 0.20f),
            CategoryRevenue("Others", totalRevenue * 0.10f.toDouble(), 0.10f)
        )

        // Hourly data
        val hourlyMap = mutableMapOf<Int, Pair<Int, Double>>()
        completedOrders.forEach { order ->
            val cal = Calendar.getInstance().apply { timeInMillis = order.createdAt }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val existing = hourlyMap[hour] ?: Pair(0, 0.0)
            hourlyMap[hour] = Pair(existing.first + 1, existing.second + order.total)
        }
        val hourlyData = (0..23).map { hour ->
            val data = hourlyMap[hour] ?: Pair(0, 0.0)
            HourlyData(hour, data.first, data.second)
        }

        // Payment breakdown
        val paymentBreakdown = completedOrders
            .groupBy { it.paymentMethod.name }
            .mapValues { (_, orders) -> orders.sumOf { it.total } }

        return SalesReport(
            totalRevenue = totalRevenue,
            totalOrders = totalOrders,
            averageOrderValue = averageOrderValue,
            topProducts = topProducts,
            revenueByCategory = categoryRevenue,
            hourlyData = hourlyData,
            paymentMethodBreakdown = paymentBreakdown
        )
    }
}

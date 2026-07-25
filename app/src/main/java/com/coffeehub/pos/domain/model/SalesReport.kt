package com.coffeehub.pos.domain.model

data class SalesReport(
    val totalRevenue: Double,
    val totalOrders: Int,
    val averageOrderValue: Double,
    val topProducts: List<TopProduct>,
    val revenueByCategory: List<CategoryRevenue>,
    val hourlyData: List<HourlyData>,
    val paymentMethodBreakdown: Map<String, Double>
)

data class TopProduct(
    val productName: String,
    val quantitySold: Int,
    val revenue: Double
)

data class CategoryRevenue(
    val categoryName: String,
    val revenue: Double,
    val percentage: Float
)

data class HourlyData(
    val hour: Int,
    val orders: Int,
    val revenue: Double
)

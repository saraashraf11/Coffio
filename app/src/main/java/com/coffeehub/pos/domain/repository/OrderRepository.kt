package com.coffeehub.pos.domain.repository

import com.coffeehub.pos.domain.model.Order
import com.coffeehub.pos.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getAllOrders(): Flow<List<Order>>
    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>>
    fun getActiveOrders(): Flow<List<Order>>
    fun getOrdersByDateRange(startTime: Long, endTime: Long): Flow<List<Order>>
    suspend fun getOrderById(orderId: Int): Order?
    suspend fun insertOrder(order: Order): Long
    suspend fun updateOrderStatus(orderId: Int, status: OrderStatus)
    suspend fun deleteOrder(order: Order)
    suspend fun getTodayTotalRevenue(): Double
    suspend fun getTodayOrderCount(): Int
}

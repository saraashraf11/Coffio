package com.coffeehub.pos.domain.usecase

import com.coffeehub.pos.domain.model.Order
import com.coffeehub.pos.domain.model.OrderStatus
import com.coffeehub.pos.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrderHistoryUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    fun getAllOrders(): Flow<List<Order>> = orderRepository.getAllOrders()

    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> =
        orderRepository.getOrdersByStatus(status)

    fun getActiveOrders(): Flow<List<Order>> = orderRepository.getActiveOrders()

    fun getOrdersByDateRange(startTime: Long, endTime: Long): Flow<List<Order>> =
        orderRepository.getOrdersByDateRange(startTime, endTime)

    suspend fun getOrderById(orderId: Int): Order? = orderRepository.getOrderById(orderId)
}

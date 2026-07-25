package com.coffeehub.pos.data.repository

import com.coffeehub.pos.data.local.dao.OrderDao
import com.coffeehub.pos.data.local.entity.OrderEntity
import com.coffeehub.pos.data.local.entity.OrderItemEntity
import com.coffeehub.pos.data.local.relation.OrderWithItems
import com.coffeehub.pos.domain.repository.OrderRepository
import com.coffeehub.pos.domain.model.Order
import com.coffeehub.pos.domain.model.OrderItem
import com.coffeehub.pos.domain.model.OrderStatus
import com.coffeehub.pos.domain.model.OrderType
import com.coffeehub.pos.domain.model.PaymentMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao
) : OrderRepository {

    override fun getAllOrders(): Flow<List<Order>> =
        orderDao.getAllOrders().map { it.map { owI -> owI.toDomain() } }

    override fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> =
        orderDao.getOrdersByStatus(status.name).map { it.map { owI -> owI.toDomain() } }

    override fun getActiveOrders(): Flow<List<Order>> =
        orderDao.getActiveOrders().map { it.map { owI -> owI.toDomain() } }

    override fun getOrdersByDateRange(startTime: Long, endTime: Long): Flow<List<Order>> =
        orderDao.getOrdersByDateRange(startTime, endTime).map { it.map { owI -> owI.toDomain() } }

    override suspend fun getOrderById(orderId: Int): Order? =
        orderDao.getOrderById(orderId)?.toDomain()

    override suspend fun insertOrder(order: Order): Long {
        val orderId = orderDao.insertOrder(order.toEntity())
        val items = order.items.map { it.toEntity(orderId.toInt()) }
        if (items.isNotEmpty()) orderDao.insertOrderItems(items)
        return orderId
    }

    override suspend fun updateOrderStatus(orderId: Int, status: OrderStatus) {
        val completedAt = if (status == OrderStatus.COMPLETED) System.currentTimeMillis() else null
        orderDao.updateStatus(orderId, status.name, completedAt)
    }

    override suspend fun deleteOrder(order: Order) =
        orderDao.deleteOrder(order.toEntity())

    override suspend fun getTodayTotalRevenue(): Double {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis
        return orderDao.getTodayRevenue(startOfDay)
    }

    override suspend fun getTodayOrderCount(): Int {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis
        return orderDao.getTodayOrderCount(startOfDay)
    }

    private fun OrderWithItems.toDomain() = Order(
        orderId = order.orderId,
        orderNumber = order.orderNumber,
        orderType = try {
            OrderType.valueOf(order.orderType)
        } catch (e: Exception) {
            OrderType.DINE_IN
        },
        tableId = order.tableId,
        tableNumber = order.tableNumber,
        customerId = order.customerId,
        customerName = order.customerName,
        items = items.map { it.toDomain() },
        subtotal = order.subtotal,
        taxRate = order.taxRate,
        tax = order.tax,
        discount = order.discount,
        total = order.total,
        status = try {
            OrderStatus.valueOf(order.status)
        } catch (e: Exception) {
            OrderStatus.PENDING
        },
        paymentMethod = try {
            PaymentMethod.valueOf(order.paymentMethod)
        } catch (e: Exception) {
            PaymentMethod.CASH
        },
        cashierName = order.cashierName,
        cashReceived = order.cashReceived,
        change = order.change,
        notes = order.notes,
        createdAt = order.createdAt,
        completedAt = order.completedAt
    )

    private fun OrderItemEntity.toDomain() = OrderItem(
        id = id, orderId = orderId, productId = productId, productName = productName,
        selectedSize = selectedSize, selectedTemperature = selectedTemperature,
        selectedMilkType = selectedMilkType, extraShots = extraShots,
        quantity = quantity, unitPrice = unitPrice, notes = notes
    )

    private fun Order.toEntity() = OrderEntity(
        orderId = orderId, orderNumber = orderNumber, orderType = orderType.name,
        tableId = tableId, tableNumber = tableNumber, customerId = customerId, customerName = customerName,
        subtotal = subtotal, taxRate = taxRate, tax = tax, discount = discount, total = total,
        status = status.name, paymentMethod = paymentMethod.name, cashierName = cashierName,
        cashReceived = cashReceived, change = change, notes = notes, createdAt = createdAt, completedAt = completedAt
    )

    private fun OrderItem.toEntity(parentOrderId: Int) = OrderItemEntity(
        id = id, orderId = parentOrderId, productId = productId, productName = productName,
        selectedSize = selectedSize, selectedTemperature = selectedTemperature,
        selectedMilkType = selectedMilkType, extraShots = extraShots,
        quantity = quantity, unitPrice = unitPrice, notes = notes
    )
}

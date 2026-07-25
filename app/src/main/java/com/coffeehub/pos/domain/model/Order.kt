package com.coffeehub.pos.domain.model

data class Order(
    val orderId: Int = 0,
    val orderNumber: String = "",
    val orderType: OrderType = OrderType.DINE_IN,
    val tableId: Int? = null,
    val tableNumber: Int? = null,
    val customerId: Int? = null,
    val customerName: String? = null,
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val taxRate: Double = 0.14,
    val tax: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val cashierName: String = "",
    val cashReceived: Double = 0.0,
    val change: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

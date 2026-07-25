package com.coffeehub.pos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    val orderNumber: String = "",
    val orderType: String = "DINE_IN",
    val tableId: Int? = null,
    val tableNumber: Int? = null,
    val customerId: Int? = null,
    val customerName: String? = null,
    val subtotal: Double = 0.0,
    val taxRate: Double = 0.14,
    val tax: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val status: String = "PENDING",
    val paymentMethod: String = "CASH",
    val cashierName: String = "",
    val cashReceived: Double = 0.0,
    val change: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

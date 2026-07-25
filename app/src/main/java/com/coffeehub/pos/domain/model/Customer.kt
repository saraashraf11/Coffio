package com.coffeehub.pos.domain.model

data class Customer(
    val id: Int = 0,
    val name: String,
    val phone: String = "",
    val email: String = "",
    val loyaltyPoints: Int = 0,
    val totalSpend: Double = 0.0,
    val totalOrders: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

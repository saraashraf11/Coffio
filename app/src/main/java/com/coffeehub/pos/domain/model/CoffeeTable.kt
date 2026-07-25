package com.coffeehub.pos.domain.model

data class CoffeeTable(
    val id: Int = 0,
    val tableNumber: Int,
    val capacity: Int = 4,
    val status: TableStatus = TableStatus.AVAILABLE,
    val currentOrderId: Int? = null,
    val section: String = "Main"
)

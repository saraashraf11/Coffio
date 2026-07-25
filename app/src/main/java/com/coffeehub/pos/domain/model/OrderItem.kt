package com.coffeehub.pos.domain.model

data class OrderItem(
    val id: Int = 0,
    val orderId: Int = 0,
    val productId: Int,
    val productName: String,
    val selectedSize: String = "M",
    val selectedTemperature: String = "Hot",
    val selectedMilkType: String = "Whole Milk",
    val extraShots: Int = 0,
    val quantity: Int,
    val unitPrice: Double,
    val notes: String = ""
) {
    val subtotal: Double get() = unitPrice * quantity
}

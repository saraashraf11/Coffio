package com.coffeehub.pos.domain.model

import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val product: Product,
    var quantity: Int = 1,
    val selectedSize: ProductSize = ProductSize.MEDIUM,
    val selectedTemperature: Temperature = Temperature.HOT,
    val selectedMilkType: MilkType = MilkType.WHOLE,
    val extraShots: Int = 0,
    val notes: String = ""
) {
    val unitPrice: Double
        get() {
            var price = product.basePrice * selectedSize.priceMultiplier
            price += extraShots * 0.5
            return price
        }

    val subtotal: Double
        get() = unitPrice * quantity
}

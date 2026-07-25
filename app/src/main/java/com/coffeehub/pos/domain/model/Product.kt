package com.coffeehub.pos.domain.model

data class Product(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val basePrice: Double,
    val categoryId: Int,
    val categoryName: String = "",
    val imageUri: String? = null,
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false,
    val availableSizes: List<ProductSize> = listOf(ProductSize.MEDIUM),
    val availableTemperatures: List<Temperature> = listOf(Temperature.HOT),
    val availableMilkTypes: List<MilkType> = listOf(MilkType.WHOLE)
)

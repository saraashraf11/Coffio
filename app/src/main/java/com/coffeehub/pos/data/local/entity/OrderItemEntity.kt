package com.coffeehub.pos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    foreignKeys = [ForeignKey(
        entity = OrderEntity::class,
        parentColumns = ["orderId"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["orderId"])]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val productId: Int,
    val productName: String,
    val selectedSize: String = "M",
    val selectedTemperature: String = "Hot",
    val selectedMilkType: String = "Whole Milk",
    val extraShots: Int = 0,
    val quantity: Int,
    val unitPrice: Double,
    val notes: String = ""
)

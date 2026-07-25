package com.coffeehub.pos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String = "",
    val email: String = "",
    val loyaltyPoints: Int = 0,
    val totalSpend: Double = 0.0,
    val totalOrders: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

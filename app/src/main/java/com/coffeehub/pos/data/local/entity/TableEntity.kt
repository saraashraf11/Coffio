package com.coffeehub.pos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tables")
data class TableEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tableNumber: Int,
    val capacity: Int = 4,
    val status: String = "AVAILABLE",
    val currentOrderId: Int? = null,
    val section: String = "Main"
)

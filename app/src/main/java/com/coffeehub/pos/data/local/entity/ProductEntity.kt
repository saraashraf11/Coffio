package com.coffeehub.pos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["categoryId"])]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val basePrice: Double,
    val categoryId: Int = 0,
    val imageUri: String? = null,
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false,
    val sizesJson: String = "[\"M\"]",
    val temperaturesJson: String = "[\"Hot\"]",
    val milkTypesJson: String = "[\"Whole Milk\"]"
)

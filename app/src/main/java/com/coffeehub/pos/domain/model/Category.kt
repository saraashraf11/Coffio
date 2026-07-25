package com.coffeehub.pos.domain.model

data class Category(
    val id: Int = 0,
    val name: String,
    val iconName: String,
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

package com.coffeehub.pos.domain.model

data class User(
    val id: Int = 0,
    val name: String,
    val username: String,
    val passwordHash: String = "",
    val role: UserRole = UserRole.CASHIER,
    val isActive: Boolean = true,
    val lastLogin: Long? = null
)

package com.coffeehub.pos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val username: String,
    val passwordHash: String,
    val role: String = "CASHIER",
    val isActive: Boolean = true,
    val lastLogin: Long? = null
)

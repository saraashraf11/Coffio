package com.coffeehub.pos.domain.repository

import com.coffeehub.pos.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsers(): Flow<List<User>>
    suspend fun getUserByUsername(username: String): User?
    suspend fun getUserById(id: Int): User?
    suspend fun insertUser(user: User): Long
    suspend fun updateUser(user: User)
    suspend fun authenticate(username: String, passwordHash: String): User?
    suspend fun updateLastLogin(userId: Int)
}

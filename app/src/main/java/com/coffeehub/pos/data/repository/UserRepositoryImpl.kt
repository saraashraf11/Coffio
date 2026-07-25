package com.coffeehub.pos.data.repository

import com.coffeehub.pos.data.local.dao.UserDao
import com.coffeehub.pos.data.local.entity.UserEntity
import com.coffeehub.pos.domain.model.User
import com.coffeehub.pos.domain.model.UserRole
import com.coffeehub.pos.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { it.map { e -> e.toDomain() } }

    override suspend fun getUserByUsername(username: String): User? =
        userDao.getUserByUsername(username)?.toDomain()

    override suspend fun getUserById(id: Int): User? =
        userDao.getUserById(id)?.toDomain()

    override suspend fun insertUser(user: User): Long =
        userDao.insert(user.toEntity())

    override suspend fun updateUser(user: User) =
        userDao.update(user.toEntity())

    override suspend fun authenticate(username: String, passwordHash: String): User? =
        userDao.authenticate(username, passwordHash)?.toDomain()

    override suspend fun updateLastLogin(userId: Int) =
        userDao.updateLastLogin(userId, System.currentTimeMillis())

    private fun UserEntity.toDomain() = User(
        id = id, name = name, username = username, passwordHash = passwordHash,
        role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.CASHIER },
        isActive = isActive, lastLogin = lastLogin
    )

    private fun User.toEntity() = UserEntity(
        id = id, name = name, username = username, passwordHash = passwordHash,
        role = role.name, isActive = isActive, lastLogin = lastLogin
    )
}

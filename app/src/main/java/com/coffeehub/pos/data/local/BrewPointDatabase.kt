package com.coffeehub.pos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.coffeehub.pos.data.local.dao.CategoryDao
import com.coffeehub.pos.data.local.dao.CustomerDao
import com.coffeehub.pos.data.local.dao.OrderDao
import com.coffeehub.pos.data.local.dao.ProductDao
import com.coffeehub.pos.data.local.dao.TableDao
import com.coffeehub.pos.data.local.dao.UserDao
import com.coffeehub.pos.data.local.entity.CategoryEntity
import com.coffeehub.pos.data.local.entity.CustomerEntity
import com.coffeehub.pos.data.local.entity.OrderEntity
import com.coffeehub.pos.data.local.entity.OrderItemEntity
import com.coffeehub.pos.data.local.entity.ProductEntity
import com.coffeehub.pos.data.local.entity.TableEntity
import com.coffeehub.pos.data.local.entity.UserEntity

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CustomerEntity::class,
        TableEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BrewPointDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun customerDao(): CustomerDao
    abstract fun tableDao(): TableDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "brewpoint_db"
    }
}

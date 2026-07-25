package com.coffeehub.pos.data.local.dao

import androidx.room.*
import com.coffeehub.pos.data.local.entity.OrderEntity
import com.coffeehub.pos.data.local.entity.OrderItemEntity
import com.coffeehub.pos.data.local.relation.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderWithItems>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderWithItems>>

    @Query("SELECT * FROM orders WHERE status IN ('PENDING','IN_PROGRESS','READY') ORDER BY createdAt ASC")
    fun getActiveOrders(): Flow<List<OrderWithItems>>

    @Query("SELECT * FROM orders WHERE createdAt >= :startTime AND createdAt <= :endTime ORDER BY createdAt DESC")
    fun getOrdersByDateRange(startTime: Long, endTime: Long): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: Int): OrderWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE orders SET status = :status, completedAt = :completedAt WHERE orderId = :orderId")
    suspend fun updateStatus(orderId: Int, status: String, completedAt: Long? = null)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("SELECT COALESCE(SUM(total), 0.0) FROM orders WHERE status = 'COMPLETED' AND createdAt >= :startOfDay")
    suspend fun getTodayRevenue(startOfDay: Long): Double

    @Query("SELECT COUNT(*) FROM orders WHERE createdAt >= :startOfDay")
    suspend fun getTodayOrderCount(startOfDay: Long): Int
}

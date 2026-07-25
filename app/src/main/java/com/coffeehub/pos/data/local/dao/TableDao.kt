package com.coffeehub.pos.data.local.dao

import androidx.room.*
import com.coffeehub.pos.data.local.entity.TableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Query("SELECT * FROM tables ORDER BY tableNumber ASC")
    fun getAllTables(): Flow<List<TableEntity>>

    @Query("SELECT * FROM tables WHERE status = :status ORDER BY tableNumber ASC")
    fun getTablesByStatus(status: String): Flow<List<TableEntity>>

    @Query("SELECT * FROM tables WHERE id = :id")
    suspend fun getTableById(id: Int): TableEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(table: TableEntity): Long

    @Update
    suspend fun update(table: TableEntity)

    @Query("UPDATE tables SET status = :status, currentOrderId = :orderId WHERE id = :tableId")
    suspend fun updateTableStatus(tableId: Int, status: String, orderId: Int?)
}

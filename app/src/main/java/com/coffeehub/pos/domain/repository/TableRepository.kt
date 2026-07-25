package com.coffeehub.pos.domain.repository

import com.coffeehub.pos.domain.model.CoffeeTable
import com.coffeehub.pos.domain.model.TableStatus
import kotlinx.coroutines.flow.Flow

interface TableRepository {
    fun getAllTables(): Flow<List<CoffeeTable>>
    fun getTablesByStatus(status: TableStatus): Flow<List<CoffeeTable>>
    suspend fun getTableById(id: Int): CoffeeTable?
    suspend fun insertTable(table: CoffeeTable): Long
    suspend fun updateTable(table: CoffeeTable)
    suspend fun updateTableStatus(tableId: Int, status: TableStatus, orderId: Int?)
}

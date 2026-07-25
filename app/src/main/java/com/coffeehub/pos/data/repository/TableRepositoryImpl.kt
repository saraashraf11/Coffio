package com.coffeehub.pos.data.repository

import com.coffeehub.pos.data.local.dao.TableDao
import com.coffeehub.pos.data.local.entity.TableEntity
import com.coffeehub.pos.domain.model.CoffeeTable
import com.coffeehub.pos.domain.model.TableStatus
import com.coffeehub.pos.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TableRepositoryImpl @Inject constructor(
    private val tableDao: TableDao
) : TableRepository {

    override fun getAllTables(): Flow<List<CoffeeTable>> =
        tableDao.getAllTables().map { it.map { e -> e.toDomain() } }

    override fun getTablesByStatus(status: TableStatus): Flow<List<CoffeeTable>> =
        tableDao.getTablesByStatus(status.name).map { it.map { e -> e.toDomain() } }

    override suspend fun getTableById(id: Int): CoffeeTable? =
        tableDao.getTableById(id)?.toDomain()

    override suspend fun insertTable(table: CoffeeTable): Long =
        tableDao.insert(table.toEntity())

    override suspend fun updateTable(table: CoffeeTable) =
        tableDao.update(table.toEntity())

    override suspend fun updateTableStatus(tableId: Int, status: TableStatus, orderId: Int?) =
        tableDao.updateTableStatus(tableId, status.name, orderId)

    private fun TableEntity.toDomain() = CoffeeTable(
        id = id, tableNumber = tableNumber, capacity = capacity,
        status = try { TableStatus.valueOf(status) } catch (e: Exception) { TableStatus.AVAILABLE },
        currentOrderId = currentOrderId, section = section
    )

    private fun CoffeeTable.toEntity() = TableEntity(
        id = id, tableNumber = tableNumber, capacity = capacity,
        status = status.name, currentOrderId = currentOrderId, section = section
    )
}

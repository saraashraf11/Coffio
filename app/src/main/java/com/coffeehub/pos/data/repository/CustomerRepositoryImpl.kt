package com.coffeehub.pos.data.repository

import com.coffeehub.pos.data.local.dao.CustomerDao
import com.coffeehub.pos.data.local.entity.CustomerEntity
import com.coffeehub.pos.domain.model.Customer
import com.coffeehub.pos.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao
) : CustomerRepository {

    override fun getAllCustomers(): Flow<List<Customer>> =
        customerDao.getAllCustomers().map { it.map { e -> e.toDomain() } }

    override fun searchCustomers(query: String): Flow<List<Customer>> =
        customerDao.searchCustomers(query).map { it.map { e -> e.toDomain() } }

    override suspend fun getCustomerById(id: Int): Customer? =
        customerDao.getCustomerById(id)?.toDomain()

    override suspend fun insertCustomer(customer: Customer): Long =
        customerDao.insert(customer.toEntity())

    override suspend fun updateCustomer(customer: Customer) =
        customerDao.update(customer.toEntity())

    override suspend fun deleteCustomer(customer: Customer) =
        customerDao.delete(customer.toEntity())

    override suspend fun addLoyaltyPoints(customerId: Int, points: Int) =
        customerDao.addLoyaltyPoints(customerId, points)

    private fun CustomerEntity.toDomain() = Customer(
        id = id, name = name, phone = phone, email = email,
        loyaltyPoints = loyaltyPoints, totalSpend = totalSpend, totalOrders = totalOrders,
        notes = notes, createdAt = createdAt
    )

    private fun Customer.toEntity() = CustomerEntity(
        id = id, name = name, phone = phone, email = email,
        loyaltyPoints = loyaltyPoints, totalSpend = totalSpend, totalOrders = totalOrders,
        notes = notes, createdAt = createdAt
    )
}

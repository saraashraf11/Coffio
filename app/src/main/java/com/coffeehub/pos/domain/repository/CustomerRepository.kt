package com.coffeehub.pos.domain.repository

import com.coffeehub.pos.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getAllCustomers(): Flow<List<Customer>>
    fun searchCustomers(query: String): Flow<List<Customer>>
    suspend fun getCustomerById(id: Int): Customer?
    suspend fun insertCustomer(customer: Customer): Long
    suspend fun updateCustomer(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)
    suspend fun addLoyaltyPoints(customerId: Int, points: Int)
}

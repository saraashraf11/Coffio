package com.coffeehub.pos.domain.repository

import com.coffeehub.pos.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    fun getProductsByCategory(categoryId: Int): Flow<List<Product>>
    fun getPopularProducts(): Flow<List<Product>>
    fun searchProducts(query: String): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    suspend fun insertProduct(product: Product): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    suspend fun toggleProductAvailability(productId: Int, isAvailable: Boolean)
}

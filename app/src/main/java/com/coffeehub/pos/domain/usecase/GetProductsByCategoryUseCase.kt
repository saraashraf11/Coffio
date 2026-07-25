package com.coffeehub.pos.domain.usecase

import com.coffeehub.pos.domain.model.Product
import com.coffeehub.pos.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(categoryId: Int?): Flow<List<Product>> {
        return if (categoryId == null || categoryId == -1) {
            productRepository.getAllProducts()
        } else {
            productRepository.getProductsByCategory(categoryId)
        }
    }

    fun searchProducts(query: String): Flow<List<Product>> {
        return productRepository.searchProducts(query)
    }

    fun getPopularProducts(): Flow<List<Product>> {
        return productRepository.getPopularProducts()
    }
}

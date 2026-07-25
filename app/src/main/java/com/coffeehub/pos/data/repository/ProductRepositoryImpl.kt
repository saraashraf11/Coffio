package com.coffeehub.pos.data.repository

import com.coffeehub.pos.data.local.dao.ProductDao
import com.coffeehub.pos.data.local.entity.ProductEntity
import com.coffeehub.pos.domain.model.MilkType
import com.coffeehub.pos.domain.model.Product
import com.coffeehub.pos.domain.model.ProductSize
import com.coffeehub.pos.domain.model.Temperature
import com.coffeehub.pos.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> =
        productDao.getAllProducts().map { entities -> entities.map { it.toDomain() } }

    override fun getProductsByCategory(categoryId: Int): Flow<List<Product>> =
        productDao.getProductsByCategory(categoryId).map { entities -> entities.map { it.toDomain() } }

    override fun getPopularProducts(): Flow<List<Product>> =
        productDao.getPopularProducts().map { entities -> entities.map { it.toDomain() } }

    override fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getProductById(id: Int): Product? =
        productDao.getProductById(id)?.toDomain()

    override suspend fun insertProduct(product: Product): Long =
        productDao.insert(product.toEntity())

    override suspend fun updateProduct(product: Product) =
        productDao.update(product.toEntity())

    override suspend fun deleteProduct(product: Product) =
        productDao.delete(product.toEntity())

    override suspend fun toggleProductAvailability(productId: Int, isAvailable: Boolean) =
        productDao.toggleAvailability(productId, isAvailable)

    private fun ProductEntity.toDomain(): Product {
        val sizes = sizesJson.removeSurrounding("[", "]").split(",")
            .mapNotNull { s -> ProductSize.values().find { it.label == s.trim().removeSurrounding("\"") } }
            .ifEmpty { listOf(ProductSize.MEDIUM) }
        val temperatures = temperaturesJson.removeSurrounding("[", "]").split(",")
            .mapNotNull { s -> Temperature.values().find { it.label == s.trim().removeSurrounding("\"") } }
            .ifEmpty { listOf(Temperature.HOT) }
        val milkTypes = milkTypesJson.removeSurrounding("[", "]").split(",")
            .mapNotNull { s -> MilkType.values().find { it.label == s.trim().removeSurrounding("\"") } }
            .ifEmpty { listOf(MilkType.WHOLE) }
        return Product(
            id = id, name = name, description = description, basePrice = basePrice,
            categoryId = categoryId, imageUri = imageUri, isAvailable = isAvailable,
            isPopular = isPopular, availableSizes = sizes, availableTemperatures = temperatures,
            availableMilkTypes = milkTypes
        )
    }

    private fun Product.toEntity() = ProductEntity(
        id = id, name = name, description = description, basePrice = basePrice,
        categoryId = categoryId, imageUri = imageUri, isAvailable = isAvailable, isPopular = isPopular,
        sizesJson = "[${availableSizes.joinToString(",") { "\"${it.label}\"" }}]",
        temperaturesJson = "[${availableTemperatures.joinToString(",") { "\"${it.label}\"" }}]",
        milkTypesJson = "[${availableMilkTypes.joinToString(",") { "\"${it.label}\"" }}]"
    )
}

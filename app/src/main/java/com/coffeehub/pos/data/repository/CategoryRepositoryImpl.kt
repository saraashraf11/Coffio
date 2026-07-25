package com.coffeehub.pos.data.repository

import com.coffeehub.pos.data.local.dao.CategoryDao
import com.coffeehub.pos.data.local.entity.CategoryEntity
import com.coffeehub.pos.domain.model.Category
import com.coffeehub.pos.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { entities -> entities.map { it.toDomain() } }

    override fun getActiveCategories(): Flow<List<Category>> =
        categoryDao.getActiveCategories().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertCategory(category: Category): Long =
        categoryDao.insert(category.toEntity())

    override suspend fun updateCategory(category: Category) =
        categoryDao.update(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        categoryDao.delete(category.toEntity())

    override suspend fun getCategoryById(id: Int): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

    private fun CategoryEntity.toDomain() = Category(
        id = id, name = name, iconName = iconName, displayOrder = displayOrder, isActive = isActive
    )

    private fun Category.toEntity() = CategoryEntity(
        id = id, name = name, iconName = iconName, displayOrder = displayOrder, isActive = isActive
    )
}

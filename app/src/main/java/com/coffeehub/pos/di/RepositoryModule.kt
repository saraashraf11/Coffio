package com.coffeehub.pos.di

import com.coffeehub.pos.data.repository.CategoryRepositoryImpl
import com.coffeehub.pos.data.repository.CustomerRepositoryImpl
import com.coffeehub.pos.data.repository.OrderRepositoryImpl
import com.coffeehub.pos.data.repository.ProductRepositoryImpl
import com.coffeehub.pos.data.repository.TableRepositoryImpl
import com.coffeehub.pos.data.repository.UserRepositoryImpl
import com.coffeehub.pos.domain.repository.CategoryRepository
import com.coffeehub.pos.domain.repository.CustomerRepository
import com.coffeehub.pos.domain.repository.OrderRepository
import com.coffeehub.pos.domain.repository.ProductRepository
import com.coffeehub.pos.domain.repository.TableRepository
import com.coffeehub.pos.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds @Singleton
    abstract fun bindCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Binds @Singleton
    abstract fun bindTableRepository(impl: TableRepositoryImpl): TableRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}

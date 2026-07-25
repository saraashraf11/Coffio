package com.coffeehub.pos.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.coffeehub.pos.data.local.BrewPointDatabase
import com.coffeehub.pos.data.local.DatabaseSeeder
import com.coffeehub.pos.utils.Constants
import com.coffeehub.pos.data.local.dao.CategoryDao
import com.coffeehub.pos.data.local.dao.CustomerDao
import com.coffeehub.pos.data.local.dao.OrderDao
import com.coffeehub.pos.data.local.dao.ProductDao
import com.coffeehub.pos.data.local.dao.TableDao
import com.coffeehub.pos.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BrewPointDatabase {
        return Room.databaseBuilder(
            context,
            BrewPointDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed is handled via DatabaseSeeder after DB creation
            }
        }).build().also { db ->
            DatabaseSeeder.seed(db)
        }
    }

    @Provides fun provideCategoryDao(db: BrewPointDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideProductDao(db: BrewPointDatabase): ProductDao = db.productDao()
    @Provides fun provideOrderDao(db: BrewPointDatabase): OrderDao = db.orderDao()
    @Provides fun provideCustomerDao(db: BrewPointDatabase): CustomerDao = db.customerDao()
    @Provides fun provideTableDao(db: BrewPointDatabase): TableDao = db.tableDao()
    @Provides fun provideUserDao(db: BrewPointDatabase): UserDao = db.userDao()
}

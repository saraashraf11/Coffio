package com.coffeehub.pos.data.local

import com.coffeehub.pos.utils.PasswordUtils
import com.coffeehub.pos.data.local.entity.CategoryEntity
import com.coffeehub.pos.data.local.entity.ProductEntity
import com.coffeehub.pos.data.local.entity.TableEntity
import com.coffeehub.pos.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseSeeder {
    fun seed(database: BrewPointDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if database is already seeded
            if (database.categoryDao().getCategoryById(1) != null) {
                return@launch
            }
            seedCategories(database)
            seedProducts(database)
            seedTables(database)
            seedUsers(database)
        }
    }

    private suspend fun seedCategories(database: BrewPointDatabase) {
        val dao = database.categoryDao()
        val categories = listOf(
            CategoryEntity(
                id = 1,
                name = "Espresso & Coffee",
                iconName = "coffee",
                displayOrder = 1
            ),
            CategoryEntity(id = 2, name = "Cold Drinks", iconName = "local_bar", displayOrder = 2),
            CategoryEntity(
                id = 3,
                name = "Tea & Matcha",
                iconName = "emoji_food_beverage",
                displayOrder = 3
            ),
            CategoryEntity(id = 4, name = "Pastries", iconName = "bakery_dining", displayOrder = 4),
            CategoryEntity(id = 5, name = "Desserts", iconName = "cake", displayOrder = 5),
            CategoryEntity(id = 6, name = "Sandwiches", iconName = "lunch_dining", displayOrder = 6)
        )
        categories.forEach { dao.insert(it) }
    }

    private suspend fun seedProducts(database: BrewPointDatabase) {
        val dao = database.productDao()
        val products = listOf(
            // Espresso & Coffee
            ProductEntity(
                id = 1,
                name = "Espresso",
                description = "Rich, bold single or double shot",
                basePrice = 2.50,
                categoryId = 1,
                isPopular = true,
                sizesJson = "[\"S\",\"M\"]",
                temperaturesJson = "[\"Hot\"]"
            ),
            ProductEntity(
                id = 2,
                name = "Americano",
                description = "Espresso with hot water, smooth and balanced",
                basePrice = 3.00,
                categoryId = 1,
                isPopular = true,
                sizesJson = "[\"S\",\"M\",\"L\"]",
                temperaturesJson = "[\"Hot\",\"Iced\"]"
            ),
            ProductEntity(
                id = 3,
                name = "Cappuccino",
                description = "Equal parts espresso, steamed & foamed milk",
                basePrice = 4.00,
                categoryId = 1,
                isPopular = true,
                sizesJson = "[\"S\",\"M\",\"L\"]",
                temperaturesJson = "[\"Hot\"]"
            ),
            ProductEntity(
                id = 4,
                name = "Latte",
                description = "Smooth espresso with velvety steamed milk",
                basePrice = 4.50,
                categoryId = 1,
                isPopular = true,
                sizesJson = "[\"S\",\"M\",\"L\"]",
                temperaturesJson = "[\"Hot\",\"Iced\"]"
            ),
            ProductEntity(
                id = 5,
                name = "Flat White",
                description = "Double ristretto with microfoam milk",
                basePrice = 4.50,
                categoryId = 1,
                sizesJson = "[\"M\"]",
                temperaturesJson = "[\"Hot\"]"
            ),
            ProductEntity(
                id = 6,
                name = "Mocha",
                description = "Espresso, chocolate, steamed milk & cream",
                basePrice = 5.00,
                categoryId = 1,
                isPopular = true,
                sizesJson = "[\"M\",\"L\"]",
                temperaturesJson = "[\"Hot\",\"Iced\"]"
            ),
            ProductEntity(
                id = 7,
                name = "Caramel Macchiato",
                description = "Vanilla latte with caramel drizzle",
                basePrice = 5.50,
                categoryId = 1,
                isPopular = true,
                sizesJson = "[\"M\",\"L\"]",
                temperaturesJson = "[\"Hot\",\"Iced\"]"
            ),
            ProductEntity(
                id = 8,
                name = "Cold Brew",
                description = "12-hour steeped, smooth & less acidic",
                basePrice = 5.00,
                categoryId = 2,
                isPopular = true,
                sizesJson = "[\"M\",\"L\"]",
                temperaturesJson = "[\"Iced\"]"
            ),
            ProductEntity(
                id = 9,
                name = "Iced Latte",
                description = "Espresso over ice with cold milk",
                basePrice = 4.50,
                categoryId = 2,
                sizesJson = "[\"M\",\"L\"]",
                temperaturesJson = "[\"Iced\"]"
            ),
            ProductEntity(
                id = 10,
                name = "Frappuccino",
                description = "Blended coffee with ice and cream",
                basePrice = 6.00,
                categoryId = 2,
                isPopular = true,
                sizesJson = "[\"M\",\"L\"]",
                temperaturesJson = "[\"Blended\"]"
            ),
            ProductEntity(
                id = 11,
                name = "Matcha Latte",
                description = "Ceremonial matcha with steamed oat milk",
                basePrice = 5.50,
                categoryId = 3,
                isPopular = true,
                sizesJson = "[\"M\",\"L\"]",
                temperaturesJson = "[\"Hot\",\"Iced\"]"
            ),
            ProductEntity(
                id = 12,
                name = "Chai Latte",
                description = "Spiced chai concentrate with steamed milk",
                basePrice = 5.00,
                categoryId = 3,
                sizesJson = "[\"M\",\"L\"]",
                temperaturesJson = "[\"Hot\",\"Iced\"]"
            ),
            ProductEntity(
                id = 13,
                name = "Croissant",
                description = "Buttery, flaky French croissant",
                basePrice = 3.50,
                categoryId = 4,
                isPopular = true
            ),
            ProductEntity(
                id = 14,
                name = "Blueberry Muffin",
                description = "Moist muffin loaded with blueberries",
                basePrice = 3.50,
                categoryId = 4
            ),
            ProductEntity(
                id = 15,
                name = "Cinnamon Roll",
                description = "Warm roll with cream cheese frosting",
                basePrice = 4.50,
                categoryId = 4,
                isPopular = true
            ),
            ProductEntity(
                id = 16,
                name = "Cheesecake Slice",
                description = "New York style creamy cheesecake",
                basePrice = 6.00,
                categoryId = 5,
                isPopular = true
            ),
            ProductEntity(
                id = 17,
                name = "Tiramisu",
                description = "Classic Italian coffee dessert",
                basePrice = 6.50,
                categoryId = 5
            ),
            ProductEntity(
                id = 18,
                name = "Club Sandwich",
                description = "Triple decker with chicken, bacon & veggies",
                basePrice = 8.50,
                categoryId = 6
            )
        )
        products.forEach { dao.insert(it) }
    }

    private suspend fun seedTables(database: BrewPointDatabase) {
        val dao = database.tableDao()
        val tables = listOf(
            TableEntity(id = 1, tableNumber = 1, capacity = 2, section = "Window"),
            TableEntity(id = 2, tableNumber = 2, capacity = 2, section = "Window"),
            TableEntity(id = 3, tableNumber = 3, capacity = 4, section = "Main"),
            TableEntity(id = 4, tableNumber = 4, capacity = 4, section = "Main"),
            TableEntity(id = 5, tableNumber = 5, capacity = 4, section = "Main"),
            TableEntity(id = 6, tableNumber = 6, capacity = 6, section = "Main"),
            TableEntity(id = 7, tableNumber = 7, capacity = 6, section = "Lounge"),
            TableEntity(id = 8, tableNumber = 8, capacity = 8, section = "Lounge"),
            TableEntity(id = 9, tableNumber = 9, capacity = 2, section = "Outdoor"),
            TableEntity(id = 10, tableNumber = 10, capacity = 4, section = "Outdoor")
        )
        tables.forEach { dao.insert(it) }
    }

    private suspend fun seedUsers(database: BrewPointDatabase) {
        val dao = database.userDao()
        val users = listOf(
            UserEntity(
                id = 1,
                name = "Manager",
                username = "manager",
                passwordHash = PasswordUtils.hash("manager123"),
                role = "MANAGER"
            ),
            UserEntity(
                id = 2,
                name = "Sara Cashier",
                username = "cashier",
                passwordHash = PasswordUtils.hash("cashier123"),
                role = "CASHIER"
            ),
            UserEntity(
                id = 3,
                name = "Ahmed Barista",
                username = "barista",
                passwordHash = PasswordUtils.hash("barista123"),
                role = "BARISTA"
            )
        )
        users.forEach { dao.insert(it) }
    }
}

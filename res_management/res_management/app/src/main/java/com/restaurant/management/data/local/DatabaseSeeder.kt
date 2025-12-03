package com.restaurant.management.data.local

import android.content.Context
import com.restaurant.management.data.model.MenuItem
import com.restaurant.management.data.model.User
import com.restaurant.management.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseSeeder(private val database: AppDatabase) {

    suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        // Check if already seeded
        val menuItemDao = database.menuItemDao()
        val userDao = database.userDao()
        
        // Seed sample users
        seedUsers(userDao)
        
        // Seed menu items
        seedMenuItems(menuItemDao)
    }

    private suspend fun seedUsers(userDao: UserDao) {
        // Check if users already exist
        val existingStaff = userDao.getUserByEmail("staff@restaurant.com")
        if (existingStaff == null) {
            // Create a staff user
            userDao.insertUser(
                User(
                    email = "staff@restaurant.com",
                    password = "staff123",
                    name = "John Staff",
                    role = UserRole.STAFF
                )
            )
        }

        val existingGuest = userDao.getUserByEmail("guest@restaurant.com")
        if (existingGuest == null) {
            // Create a guest user
            userDao.insertUser(
                User(
                    email = "guest@restaurant.com",
                    password = "guest123",
                    name = "Jane Customer",
                    role = UserRole.GUEST
                )
            )
        }
    }

    private suspend fun seedMenuItems(menuItemDao: MenuItemDao) {
        val sampleMenuItems = listOf(
            // Appetizers
            MenuItem(
                name = "Bruschetta",
                description = "Grilled bread rubbed with garlic and topped with diced tomatoes, olive oil, and basil",
                price = 8.99,
                category = "Appetizers",
                imageUrl = "https://images.unsplash.com/photo-1572695157366-5e585ab2b69f",
                available = true
            ),
            MenuItem(
                name = "Calamari Fritti",
                description = "Lightly battered and fried squid served with marinara sauce",
                price = 12.99,
                category = "Appetizers",
                imageUrl = "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0",
                available = true
            ),
            MenuItem(
                name = "Caprese Salad",
                description = "Fresh mozzarella, tomatoes, and basil drizzled with balsamic glaze",
                price = 10.99,
                category = "Appetizers",
                imageUrl = "https://images.unsplash.com/photo-1608897013039-887f21d8c804",
                available = true
            ),

            // Main Courses
            MenuItem(
                name = "Grilled Salmon",
                description = "Fresh Atlantic salmon grilled to perfection with lemon butter sauce",
                price = 24.99,
                category = "Main Course",
                imageUrl = "https://images.unsplash.com/photo-1467003909585-2f8a72700288",
                available = true
            ),
            MenuItem(
                name = "Ribeye Steak",
                description = "12oz premium ribeye steak with garlic mashed potatoes and seasonal vegetables",
                price = 32.99,
                category = "Main Course",
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d",
                available = true
            ),
            MenuItem(
                name = "Chicken Parmesan",
                description = "Breaded chicken breast topped with marinara and melted mozzarella",
                price = 18.99,
                category = "Main Course",
                imageUrl = "https://images.unsplash.com/photo-1632778149955-e80f8ceca2e8",
                available = true
            ),
            MenuItem(
                name = "Seafood Paella",
                description = "Spanish rice dish with shrimp, mussels, clams, and saffron",
                price = 26.99,
                category = "Main Course",
                imageUrl = "https://images.unsplash.com/photo-1534080564583-6be75777b70a",
                available = true
            ),

            // Pasta
            MenuItem(
                name = "Spaghetti Carbonara",
                description = "Classic Italian pasta with eggs, cheese, pancetta, and black pepper",
                price = 16.99,
                category = "Pasta",
                imageUrl = "https://images.unsplash.com/photo-1612874742237-6526221588e3",
                available = true
            ),
            MenuItem(
                name = "Fettuccine Alfredo",
                description = "Creamy Alfredo sauce with fresh parmesan cheese",
                price = 15.99,
                category = "Pasta",
                imageUrl = "https://images.unsplash.com/photo-1645112411341-6c4fd023714a",
                available = true
            ),
            MenuItem(
                name = "Penne Arrabbiata",
                description = "Spicy tomato sauce with garlic and red chili peppers",
                price = 14.99,
                category = "Pasta",
                imageUrl = "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9",
                available = true
            ),

            // Pizza
            MenuItem(
                name = "Margherita Pizza",
                description = "Classic pizza with tomato sauce, mozzarella, and fresh basil",
                price = 13.99,
                category = "Pizza",
                imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002",
                available = true
            ),
            MenuItem(
                name = "Pepperoni Pizza",
                description = "Loaded with pepperoni and mozzarella cheese",
                price = 15.99,
                category = "Pizza",
                imageUrl = "https://images.unsplash.com/photo-1628840042765-356cda07504e",
                available = true
            ),
            MenuItem(
                name = "Quattro Formaggi",
                description = "Four cheese pizza with mozzarella, gorgonzola, parmesan, and fontina",
                price = 17.99,
                category = "Pizza",
                imageUrl = "https://images.unsplash.com/photo-1571997478779-2adcbbe9ab2f",
                available = true
            ),

            // Desserts
            MenuItem(
                name = "Tiramisu",
                description = "Classic Italian dessert with coffee-soaked ladyfingers and mascarpone",
                price = 7.99,
                category = "Desserts",
                imageUrl = "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9",
                available = true
            ),
            MenuItem(
                name = "Chocolate Lava Cake",
                description = "Warm chocolate cake with molten center, served with vanilla ice cream",
                price = 8.99,
                category = "Desserts",
                imageUrl = "https://images.unsplash.com/photo-1624353365286-3f8d62daad51",
                available = true
            ),
            MenuItem(
                name = "Crème Brûlée",
                description = "Rich custard topped with caramelized sugar",
                price = 7.99,
                category = "Desserts",
                imageUrl = "https://images.unsplash.com/photo-1470124182917-cc6e71b22ecc",
                available = true
            ),

            // Beverages
            MenuItem(
                name = "Fresh Lemonade",
                description = "Freshly squeezed lemon juice with a touch of sweetness",
                price = 3.99,
                category = "Beverages",
                imageUrl = "https://images.unsplash.com/photo-1523677011781-c91d1bbe2f93",
                available = true
            ),
            MenuItem(
                name = "Italian Espresso",
                description = "Rich and bold espresso shot",
                price = 2.99,
                category = "Beverages",
                imageUrl = "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04",
                available = true
            ),
            MenuItem(
                name = "Cappuccino",
                description = "Espresso with steamed milk and foam",
                price = 4.99,
                category = "Beverages",
                imageUrl = "https://images.unsplash.com/photo-1572442388796-11668a67e53d",
                available = true
            )
        )

        // Insert all menu items
        sampleMenuItems.forEach { menuItem ->
            menuItemDao.insertMenuItem(menuItem)
        }
    }
}

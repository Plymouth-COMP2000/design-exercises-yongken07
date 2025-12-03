package com.restaurant.management

import android.app.Application
import com.restaurant.management.data.local.AppDatabase
import com.restaurant.management.data.local.DatabaseSeeder
import com.restaurant.management.data.repository.MenuRepository
import com.restaurant.management.data.repository.ReservationRepository
import com.restaurant.management.data.repository.UserRepository
import com.restaurant.management.notification.NotificationHelper
import com.restaurant.management.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RestaurantApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var database: AppDatabase
        private set

    lateinit var userRepository: UserRepository
        private set

    lateinit var menuRepository: MenuRepository
        private set

    lateinit var reservationRepository: ReservationRepository
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var notificationHelper: NotificationHelper
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize database
        database = AppDatabase.getDatabase(this)
        
        // Initialize repositories
        userRepository = UserRepository(database.userDao())
        menuRepository = MenuRepository(database.menuItemDao())
        reservationRepository = ReservationRepository(database.reservationDao())
        
        // Initialize utilities
        preferencesManager = PreferencesManager(this)
        notificationHelper = NotificationHelper(this)
        
        // Seed database with sample data
        seedDatabaseIfNeeded()
    }

    private fun seedDatabaseIfNeeded() {
        applicationScope.launch(Dispatchers.IO) {
            val seeder = DatabaseSeeder(database)
            seeder.seedDatabase()
        }
    }
}

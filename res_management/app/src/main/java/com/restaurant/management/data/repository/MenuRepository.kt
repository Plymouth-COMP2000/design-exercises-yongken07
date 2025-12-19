package com.restaurant.management.data.repository

import com.restaurant.management.data.local.MenuItemDao
import com.restaurant.management.data.model.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MenuRepository(private val menuItemDao: MenuItemDao) {

    fun getAllMenuItems(): Flow<List<MenuItem>> {
        return menuItemDao.getAllMenuItems()
    }

    fun getAvailableMenuItems(): Flow<List<MenuItem>> {
        return menuItemDao.getAvailableMenuItems()
    }

    fun getMenuItemsByCategory(category: String): Flow<List<MenuItem>> {
        return menuItemDao.getMenuItemsByCategory(category)
    }

    fun getAllCategories(): Flow<List<String>> {
        return menuItemDao.getAllCategories()
    }

    suspend fun getMenuItemById(id: Long): MenuItem? = withContext(Dispatchers.IO) {
        menuItemDao.getMenuItemById(id)
    }

    suspend fun insertMenuItem(menuItem: MenuItem): Result<MenuItem> = withContext(Dispatchers.IO) {
        try {
            val id = menuItemDao.insertMenuItem(menuItem)
            val newMenuItem = menuItem.copy(id = id)
            Result.success(newMenuItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMenuItem(menuItem: MenuItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updatedItem = menuItem.copy(updatedAt = System.currentTimeMillis())
            menuItemDao.updateMenuItem(updatedItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMenuItem(menuItem: MenuItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            menuItemDao.deleteMenuItem(menuItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncWithServer(): Result<Unit> = withContext(Dispatchers.IO) {
        Result.failure(Exception("Remote menu sync not supported by coursework API"))
    }
}

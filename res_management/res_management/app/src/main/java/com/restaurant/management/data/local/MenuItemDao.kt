package com.restaurant.management.data.local

import androidx.room.*
import com.restaurant.management.data.model.MenuItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items ORDER BY category, name")
    fun getAllMenuItems(): Flow<List<MenuItem>>

    @Query("SELECT * FROM menu_items WHERE available = 1 ORDER BY category, name")
    fun getAvailableMenuItems(): Flow<List<MenuItem>>

    @Query("SELECT * FROM menu_items WHERE id = :id LIMIT 1")
    suspend fun getMenuItemById(id: Long): MenuItem?

    @Query("SELECT * FROM menu_items WHERE category = :category ORDER BY name")
    fun getMenuItemsByCategory(category: String): Flow<List<MenuItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(menuItem: MenuItem): Long

    @Update
    suspend fun updateMenuItem(menuItem: MenuItem)

    @Delete
    suspend fun deleteMenuItem(menuItem: MenuItem)

    @Query("DELETE FROM menu_items WHERE id = :id")
    suspend fun deleteMenuItemById(id: Long)

    @Query("SELECT DISTINCT category FROM menu_items ORDER BY category")
    fun getAllCategories(): Flow<List<String>>
}

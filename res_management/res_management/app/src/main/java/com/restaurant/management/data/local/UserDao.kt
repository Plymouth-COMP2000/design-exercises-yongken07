package com.restaurant.management.data.local

import androidx.room.*
import com.restaurant.management.data.model.User
import com.restaurant.management.data.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET notificationsEnabled = :enabled WHERE id = :userId")
    suspend fun updateNotificationsEnabled(userId: Long, enabled: Boolean)

    @Query("UPDATE users SET notifyOnNewReservations = :enabled WHERE id = :userId")
    suspend fun updateNotifyNewReservations(userId: Long, enabled: Boolean)

    @Query("UPDATE users SET notifyOnReservationChanges = :enabled WHERE id = :userId")
    suspend fun updateNotifyReservationChanges(userId: Long, enabled: Boolean)

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<User>>
}

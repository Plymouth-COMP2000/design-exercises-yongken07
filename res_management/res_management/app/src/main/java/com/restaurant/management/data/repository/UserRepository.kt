package com.restaurant.management.data.repository

import com.restaurant.management.data.local.UserDao
import com.restaurant.management.data.model.User
import com.restaurant.management.data.model.UserRole
import com.restaurant.management.data.remote.ApiClient
import com.restaurant.management.data.remote.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Try local login first
            val user = userDao.login(email, password)
            if (user != null) {
                return@withContext Result.success(user)
            }

            // Try API login (if available)
            try {
                val response = ApiClient.apiService.loginUser(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val apiUser = response.body()!!.user
                    // Save to local database
                    userDao.insertUser(apiUser)
                    return@withContext Result.success(apiUser)
                }
            } catch (e: Exception) {
                // API not available, continue with local only
            }

            Result.failure(Exception("Invalid credentials"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Check if user already exists
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                return@withContext Result.failure(Exception("User already exists"))
            }

            // Insert into local database
            val userId = userDao.insertUser(user)
            val newUser = user.copy(id = userId)

            // Try to sync with API (if available)
            try {
                ApiClient.apiService.registerUser(newUser)
            } catch (e: Exception) {
                // API not available, continue with local only
            }

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: Long): User? = withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
    }

    suspend fun updateUser(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            userDao.updateUser(user)
            
            // Try to sync with API
            try {
                ApiClient.apiService.updateUser(user.id, user)
            } catch (e: Exception) {
                // API not available, continue with local only
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNotificationSettings(
        userId: Long,
        notificationsEnabled: Boolean? = null,
        notifyOnNewReservations: Boolean? = null,
        notifyOnReservationChanges: Boolean? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            notificationsEnabled?.let {
                userDao.updateNotificationsEnabled(userId, it)
            }
            notifyOnNewReservations?.let {
                userDao.updateNotifyNewReservations(userId, it)
            }
            notifyOnReservationChanges?.let {
                userDao.updateNotifyReservationChanges(userId, it)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUsersByRole(role: UserRole): Flow<List<User>> {
        return userDao.getUsersByRole(role)
    }
}

package com.restaurant.management.data.repository

import com.restaurant.management.data.local.UserDao
import com.restaurant.management.data.model.User
import com.restaurant.management.data.model.UserRole
import com.restaurant.management.data.remote.ApiClient
import com.restaurant.management.data.remote.CourseworkUserPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val studentId = ApiClient.studentId

            // Try remote first to keep local cache in sync
            val remoteUsers = fetchRemoteUsers(studentId).getOrNull()
            val remoteMatch = remoteUsers?.firstOrNull {
                it.email.equals(email, ignoreCase = true) && it.password == password
            }
            if (remoteMatch != null) {
                val persisted = persistOrUpdateLocal(remoteMatch)
                return@withContext Result.success(persisted)
            }

            // Fallback to local login
            val user = userDao.login(email, password)
            if (user != null) {
                return@withContext Result.success(user)
            }

            Result.failure(Exception("Invalid credentials"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                return@withContext Result.failure(Exception("User already exists"))
            }

            val studentId = ApiClient.studentId
            ensureStudentDatabase(studentId)

            val remotePayload = user.toRemotePayload()
            val remoteResponse = ApiClient.apiService.createUser(studentId, remotePayload)
            if (!remoteResponse.isSuccessful) {
                val message = remoteResponse.errorBody()?.string() ?: remoteResponse.message()
                return@withContext Result.failure(Exception("Remote create failed: $message"))
            }

            // Try to pull remote id after creation
            val remoteAfterCreate = fetchRemoteUsers(studentId).getOrNull()
            val remoteUser = remoteAfterCreate?.firstOrNull {
                it.email.equals(user.email, ignoreCase = true)
            }

            val localUser = if (remoteUser != null) {
                user.copy(
                    remoteId = remoteUser.remoteId,
                    name = remoteUser.name.ifBlank { user.name },
                    password = remoteUser.password,
                    role = remoteUser.role
                )
            } else {
                user
            }

            val userId = userDao.insertUser(localUser)
            Result.success(localUser.copy(id = userId))
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

            val studentId = ApiClient.studentId
            val remoteId = user.remoteId ?: fetchRemoteIdByEmail(studentId, user.email)
            if (remoteId != null) {
                val payload = user.toRemotePayload(remoteId)
                try {
                    ApiClient.apiService.updateUser(studentId, remoteId, payload)
                } catch (_: Exception) {
                    // Keep local update even if remote fails
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRemoteUser(remoteId: String, studentId: String = ApiClient.studentId): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.deleteUser(studentId, remoteId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val message = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Failed to delete user: $message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRemoteAndLocal(remoteId: String, studentId: String = ApiClient.studentId): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = deleteRemoteUser(remoteId, studentId)
            remoteResult.getOrThrow()
            userDao.deleteByRemoteId(remoteId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchRemoteUsers(studentId: String = ApiClient.studentId): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            ensureStudentDatabase(studentId)
            val response = ApiClient.apiService.readAllUsers(studentId)
            if (response.isSuccessful && response.body() != null) {
                val users = response.body()!!.users.map { it.toLocalUser() }
                Result.success(users)
            } else {
                val message = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Failed to read users: $message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchRemoteUser(studentId: String = ApiClient.studentId, remoteId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            ensureStudentDatabase(studentId)
            val response = ApiClient.apiService.readUser(studentId, remoteId)
            if (response.isSuccessful && response.body()?.user != null) {
                Result.success(response.body()!!.user!!.toLocalUser())
            } else {
                val message = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("User not found: $message"))
            }
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

    suspend fun syncRemoteUsersToLocal(studentId: String = ApiClient.studentId): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val remoteUsers = fetchRemoteUsers(studentId).getOrThrow()
            val persisted = remoteUsers.map { persistOrUpdateLocal(it) }
            Result.success(persisted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun ensureStudentDatabase(studentId: String) {
        try {
            ApiClient.apiService.createStudentDatabase(studentId)
        } catch (_: Exception) {
            // best effort
        }
    }

    private suspend fun fetchRemoteIdByEmail(studentId: String, email: String): String? {
        val users = fetchRemoteUsers(studentId).getOrNull() ?: return null
        return users.firstOrNull { it.email.equals(email, ignoreCase = true) }?.remoteId
    }

    private suspend fun persistOrUpdateLocal(user: User): User {
        val existing = userDao.getUserByEmail(user.email)
        return if (existing == null) {
            val id = userDao.insertUser(user)
            user.copy(id = id)
        } else {
            val updated = existing.copy(
                remoteId = user.remoteId ?: existing.remoteId,
                name = user.name,
                password = user.password,
                role = user.role
            )
            userDao.updateUser(updated)
            updated
        }
    }

    private fun User.toRemotePayload(remoteId: String? = null): CourseworkUserPayload {
        val nameParts = name.trim().split(" ")
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.drop(1).joinToString(" ")
        val remoteRole = when (role) {
            UserRole.STAFF -> "staff"
            UserRole.GUEST -> "guest"
        }
        return CourseworkUserPayload(
            userId = remoteId,
            username = email.substringBefore("@"),
            password = password,
            firstname = firstName,
            lastname = lastName,
            email = email,
            contact = "",
            usertype = remoteRole
        )
    }

    private fun CourseworkUserPayload.toLocalUser(): User {
        val displayName = listOf(firstname, lastname)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { username }
        val role = when (usertype.lowercase()) {
            "staff", "admin" -> UserRole.STAFF
            else -> UserRole.GUEST
        }
        return User(
            remoteId = userId,
            email = email,
            password = password,
            name = displayName,
            role = role
        )
    }
}

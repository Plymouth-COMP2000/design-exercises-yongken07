package com.restaurant.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: String? = null,
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole,
    val notificationsEnabled: Boolean = true,
    val notifyOnNewReservations: Boolean = true,
    val notifyOnReservationChanges: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    STAFF,
    GUEST
}

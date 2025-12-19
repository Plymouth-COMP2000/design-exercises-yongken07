package com.restaurant.management.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservations",
    indices = [Index(value = ["userId"])],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userEmail: String,
    val tableNumber: Int,
    val numberOfGuests: Int,
    val reservationDate: String,
    val reservationTime: String,
    val status: ReservationStatus = ReservationStatus.CONFIRMED,
    val specialRequests: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ReservationStatus {
    CONFIRMED,
    MODIFIED,
    CANCELLED
}

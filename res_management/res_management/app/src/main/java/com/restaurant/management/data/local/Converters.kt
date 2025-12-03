package com.restaurant.management.data.local

import androidx.room.TypeConverter
import com.restaurant.management.data.model.ReservationStatus
import com.restaurant.management.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(role: String): UserRole {
        return UserRole.valueOf(role)
    }

    @TypeConverter
    fun fromReservationStatus(status: ReservationStatus): String {
        return status.name
    }

    @TypeConverter
    fun toReservationStatus(status: String): ReservationStatus {
        return ReservationStatus.valueOf(status)
    }
}

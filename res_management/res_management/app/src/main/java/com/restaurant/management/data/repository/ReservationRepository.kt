package com.restaurant.management.data.repository

import com.restaurant.management.data.local.ReservationDao
import com.restaurant.management.data.model.Reservation
import com.restaurant.management.data.model.ReservationStatus
import com.restaurant.management.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ReservationRepository(private val reservationDao: ReservationDao) {

    fun getAllReservations(): Flow<List<Reservation>> {
        return reservationDao.getAllReservations()
    }

    fun getReservationsByUser(userId: Long): Flow<List<Reservation>> {
        return reservationDao.getReservationsByUser(userId)
    }

    fun getReservationsByStatus(status: ReservationStatus): Flow<List<Reservation>> {
        return reservationDao.getReservationsByStatus(status)
    }

    fun getReservationsByDate(date: String): Flow<List<Reservation>> {
        return reservationDao.getReservationsByDate(date)
    }

    suspend fun getReservationById(id: Long): Reservation? = withContext(Dispatchers.IO) {
        reservationDao.getReservationById(id)
    }

    suspend fun createReservation(reservation: Reservation): Result<Reservation> = withContext(Dispatchers.IO) {
        try {
            val id = reservationDao.insertReservation(reservation)
            val newReservation = reservation.copy(id = id)

            // Try to sync with API
            try {
                ApiClient.apiService.createReservation(newReservation)
            } catch (e: Exception) {
                // API not available, continue with local only
            }

            Result.success(newReservation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReservation(reservation: Reservation): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updatedReservation = reservation.copy(
                updatedAt = System.currentTimeMillis(),
                status = ReservationStatus.MODIFIED
            )
            reservationDao.updateReservation(updatedReservation)

            // Try to sync with API
            try {
                ApiClient.apiService.updateReservation(updatedReservation.id, updatedReservation)
            } catch (e: Exception) {
                // API not available, continue with local only
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelReservation(reservationId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            reservationDao.updateReservationStatus(reservationId, ReservationStatus.CANCELLED)

            // Try to sync with API
            try {
                ApiClient.apiService.cancelReservation(reservationId)
            } catch (e: Exception) {
                // API not available, continue with local only
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReservation(reservation: Reservation): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            reservationDao.deleteReservation(reservation)

            // Try to sync with API
            try {
                ApiClient.apiService.cancelReservation(reservation.id)
            } catch (e: Exception) {
                // API not available, continue with local only
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncWithServer(userId: Long? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = if (userId != null) {
                ApiClient.apiService.getReservationsByUser(userId)
            } else {
                ApiClient.apiService.getAllReservations()
            }

            if (response.isSuccessful && response.body() != null) {
                val serverReservations = response.body()!!
                serverReservations.forEach { reservation ->
                    reservationDao.insertReservation(reservation)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to sync with server"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

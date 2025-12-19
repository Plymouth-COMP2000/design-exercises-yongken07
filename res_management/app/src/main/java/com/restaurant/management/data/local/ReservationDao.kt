package com.restaurant.management.data.local

import androidx.room.*
import com.restaurant.management.data.model.Reservation
import com.restaurant.management.data.model.ReservationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations ORDER BY reservationDate DESC, reservationTime DESC")
    fun getAllReservations(): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY reservationDate DESC, reservationTime DESC")
    fun getReservationsByUser(userId: Long): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE status = :status ORDER BY reservationDate DESC, reservationTime DESC")
    fun getReservationsByStatus(status: ReservationStatus): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE id = :id LIMIT 1")
    suspend fun getReservationById(id: Long): Reservation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: Reservation): Long

    @Update
    suspend fun updateReservation(reservation: Reservation)

    @Delete
    suspend fun deleteReservation(reservation: Reservation)

    @Query("DELETE FROM reservations WHERE id = :id")
    suspend fun deleteReservationById(id: Long)

    @Query("UPDATE reservations SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateReservationStatus(id: Long, status: ReservationStatus, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM reservations WHERE reservationDate = :date ORDER BY reservationTime")
    fun getReservationsByDate(date: String): Flow<List<Reservation>>
}

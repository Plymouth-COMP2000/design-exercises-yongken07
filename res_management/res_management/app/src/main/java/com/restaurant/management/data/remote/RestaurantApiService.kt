package com.restaurant.management.data.remote

import com.restaurant.management.data.model.MenuItem
import com.restaurant.management.data.model.Reservation
import com.restaurant.management.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface RestaurantApiService {
    // User endpoints
    @POST("api/users/register")
    suspend fun registerUser(@Body user: User): Response<User>

    @POST("api/users/login")
    suspend fun loginUser(@Body credentials: LoginRequest): Response<LoginResponse>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") userId: Long): Response<User>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") userId: Long, @Body user: User): Response<User>

    // Menu endpoints
    @GET("api/menu")
    suspend fun getAllMenuItems(): Response<List<MenuItem>>

    @GET("api/menu/{id}")
    suspend fun getMenuItemById(@Path("id") menuItemId: Long): Response<MenuItem>

    @POST("api/menu")
    suspend fun createMenuItem(@Body menuItem: MenuItem): Response<MenuItem>

    @PUT("api/menu/{id}")
    suspend fun updateMenuItem(@Path("id") menuItemId: Long, @Body menuItem: MenuItem): Response<MenuItem>

    @DELETE("api/menu/{id}")
    suspend fun deleteMenuItem(@Path("id") menuItemId: Long): Response<Unit>

    // Reservation endpoints
    @GET("api/reservations")
    suspend fun getAllReservations(): Response<List<Reservation>>

    @GET("api/reservations/user/{userId}")
    suspend fun getReservationsByUser(@Path("userId") userId: Long): Response<List<Reservation>>

    @GET("api/reservations/{id}")
    suspend fun getReservationById(@Path("id") reservationId: Long): Response<Reservation>

    @POST("api/reservations")
    suspend fun createReservation(@Body reservation: Reservation): Response<Reservation>

    @PUT("api/reservations/{id}")
    suspend fun updateReservation(@Path("id") reservationId: Long, @Body reservation: Reservation): Response<Reservation>

    @DELETE("api/reservations/{id}")
    suspend fun cancelReservation(@Path("id") reservationId: Long): Response<Unit>
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val user: User,
    val token: String
)

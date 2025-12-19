package com.restaurant.management.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestaurantApiService {
    // Coursework API endpoints
    @POST("create_student/{student_id}")
    suspend fun createStudentDatabase(
        @Path("student_id") studentId: String
    ): Response<ApiMessageResponse>

    @POST("create_user/{student_id}")
    suspend fun createUser(
        @Path("student_id") studentId: String,
        @Body user: CourseworkUserPayload
    ): Response<ApiMessageResponse>

    @GET("read_all_users/{student_id}")
    suspend fun readAllUsers(
        @Path("student_id") studentId: String
    ): Response<CourseworkUserListResponse>

    @GET("read_user/{student_id}/{user_id}")
    suspend fun readUser(
        @Path("student_id") studentId: String,
        @Path("user_id") userId: String
    ): Response<CourseworkUserResponse>

    @PUT("update_user/{student_id}/{user_id}")
    suspend fun updateUser(
        @Path("student_id") studentId: String,
        @Path("user_id") userId: String,
        @Body user: CourseworkUserPayload
    ): Response<ApiMessageResponse>

    @DELETE("delete_user/{student_id}/{user_id}")
    suspend fun deleteUser(
        @Path("student_id") studentId: String,
        @Path("user_id") userId: String
    ): Response<ApiMessageResponse>
}

data class ApiMessageResponse(
    val message: String? = null,
    val detail: String? = null
)

data class CourseworkUserPayload(
    @SerializedName("user_id") val userId: String? = null,
    val username: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val contact: String,
    val usertype: String
)

data class CourseworkUserListResponse(
    val users: List<CourseworkUserPayload> = emptyList()
)

data class CourseworkUserResponse(
    val user: CourseworkUserPayload?
)

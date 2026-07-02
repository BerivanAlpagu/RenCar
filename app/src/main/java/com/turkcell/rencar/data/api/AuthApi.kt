package com.turkcell.rencar.data.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Header

@Serializable
data class MessageResponseDto(
    val message: String
)

@Serializable
data class UserResponseDto(
    val id: String,
    val email: String,
    val phone: JsonElement? = null,
    val fullName: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String
)

interface AuthApi {
    @GET("health")
    suspend fun checkHealth(): MessageResponseDto

    @GET("auth/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): UserResponseDto
}

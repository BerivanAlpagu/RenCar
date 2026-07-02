package com.turkcell.rencar.data.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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

// Wait, let's look at lines 1381-1410 in openapi.json.
// "password": { "type": "string", "description": "Parola..." }
// So password is a simple string.
// Let's rewrite RegisterDto:
@Serializable
data class UserRegisterDto(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String
)

@Serializable
data class LoginDto(
    val phone: String
)

@Serializable
data class OtpRequiredResponseDto(
    val message: String,
    val phone: String,
    val expiresAt: String
)

@Serializable
data class VerifyOtpDto(
    val phone: String,
    val code: String
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponseDto
)

interface AuthApi {
    @GET("health")
    suspend fun checkHealth(): MessageResponseDto

    @GET("auth/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): UserResponseDto

    @POST("auth/register")
    suspend fun register(
        @Body body: UserRegisterDto
    ): AuthResponseDto

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginDto
    ): OtpRequiredResponseDto

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body body: VerifyOtpDto
    ): AuthResponseDto
}

package com.turkcell.rencar.feature.auth.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
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
    suspend fun getMe(): UserResponseDto

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

@Serializable
data class UploadLicenseResponseDto(
    val id: String,
    val status: String,
    val frontImageUrl: String,
    val backImageUrl: String,
    val rejectReason: String? = null,
    val reviewedAt: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class LicenseStatusResponseDto(
    val status: String,
    val frontImageUrl: String? = null,
    val backImageUrl: String? = null,
    val rejectReason: String? = null,
    val reviewedAt: String? = null
)

interface LicenseApi {
    @GET("license/status")
    suspend fun getStatus(): LicenseStatusResponseDto

    @Multipart
    @POST("license/upload")
    suspend fun upload(
        @Part front: okhttp3.MultipartBody.Part,
        @Part back: okhttp3.MultipartBody.Part
    ): UploadLicenseResponseDto
}

package com.turkcell.rencar.feature.auth.data.repository

import com.turkcell.rencar.feature.auth.data.local.TokenManager
import com.turkcell.rencar.feature.auth.data.remote.AuthApi
import com.turkcell.rencar.feature.auth.data.remote.UserResponseDto
import com.turkcell.rencar.feature.auth.data.remote.LicenseStatusResponseDto
import com.turkcell.rencar.feature.auth.domain.model.UserProfile
import com.turkcell.rencar.feature.auth.domain.model.UserLicenseStatus
import com.turkcell.rencar.feature.auth.domain.model.LicenseStatus
import com.turkcell.rencar.feature.auth.domain.repository.AuthRepository
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun getMe(): Result<UserProfile> {
        return try {
            val responseDto = authApi.getMe()
            Result.success(responseDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLicenseStatus(): Result<UserLicenseStatus> {
        return try {
            val responseDto = authApi.getLicenseStatus()
            Result.success(responseDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            tokenManager.clearTokens()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun UserResponseDto.toDomain(): UserProfile {
        val phoneStr = when (val p = phone) {
            is JsonPrimitive -> p.contentOrNull ?: ""
            null -> ""
            else -> p.toString().trim('"')
        }
        return UserProfile(
            id = id,
            email = email,
            phone = phoneStr,
            fullName = fullName,
            role = role
        )
    }

    private fun LicenseStatusResponseDto.toDomain(): UserLicenseStatus {
        val enumStatus = try {
            LicenseStatus.valueOf(status)
        } catch (e: Exception) {
            LicenseStatus.NOT_SUBMITTED
        }

        val front = when (val p = frontImageUrl) {
            is JsonPrimitive -> p.contentOrNull
            else -> p?.toString()?.trim('"')
        }
        val back = when (val p = backImageUrl) {
            is JsonPrimitive -> p.contentOrNull
            else -> p?.toString()?.trim('"')
        }
        val reason = when (val p = rejectReason) {
            is JsonPrimitive -> p.contentOrNull
            else -> p?.toString()?.trim('"')
        }
        val reviewed = when (val p = reviewedAt) {
            is JsonPrimitive -> p.contentOrNull
            else -> p?.toString()?.trim('"')
        }

        return UserLicenseStatus(
            status = enumStatus,
            frontImageUrl = front,
            backImageUrl = back,
            rejectReason = reason,
            reviewedAt = reviewed
        )
    }
}

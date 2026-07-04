package com.turkcell.rencar.feature.auth.domain.repository

import com.turkcell.rencar.feature.auth.domain.model.UserProfile
import com.turkcell.rencar.feature.auth.domain.model.UserLicenseStatus

interface AuthRepository {
    suspend fun getMe(): Result<UserProfile>
    suspend fun getLicenseStatus(): Result<UserLicenseStatus>
    suspend fun logout(): Result<Unit>
}

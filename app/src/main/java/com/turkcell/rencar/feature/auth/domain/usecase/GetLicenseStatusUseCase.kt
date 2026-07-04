package com.turkcell.rencar.feature.auth.domain.usecase

import com.turkcell.rencar.feature.auth.domain.model.UserLicenseStatus
import com.turkcell.rencar.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GetLicenseStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<UserLicenseStatus> {
        return authRepository.getLicenseStatus()
    }
}

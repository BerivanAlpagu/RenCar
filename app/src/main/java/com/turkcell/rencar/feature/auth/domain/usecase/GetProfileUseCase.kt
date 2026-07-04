package com.turkcell.rencar.feature.auth.domain.usecase

import com.turkcell.rencar.feature.auth.domain.model.UserProfile
import com.turkcell.rencar.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        return authRepository.getMe()
    }
}

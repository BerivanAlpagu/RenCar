package com.turkcell.rencar.feature.auth.domain.usecase

import com.turkcell.rencar.feature.auth.data.remote.LicenseApi
import javax.inject.Inject

sealed interface AuthDestination {
    data object Home : AuthDestination
    data object LicenseApproval : AuthDestination
    data object License : AuthDestination
}

/**
 * Ehliyet durumuna göre giriş sonrası yönlendirme kararını üretir.
 * Splash (soğuk başlangıç) ve OTP doğrulama (interaktif giriş) akışlarının
 * ikisi de aynı kararı vermesi gerektiği için tek yerde tutulur.
 */
class ResolvePostAuthDestinationUseCase @Inject constructor(
    private val licenseApi: LicenseApi
) {
    suspend operator fun invoke(): AuthDestination {
        val licenseStatus = runCatching { licenseApi.getStatus() }.getOrNull()
        return when (licenseStatus?.status) {
            "APPROVED" -> AuthDestination.Home
            "UNDER_REVIEW" -> AuthDestination.LicenseApproval
            else -> AuthDestination.License
        }
    }
}

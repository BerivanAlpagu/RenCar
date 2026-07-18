package com.turkcell.rencar.feature.auth.domain.usecase

import com.turkcell.rencar.feature.auth.data.remote.LicenseApi
import com.turkcell.rencar.feature.rentals.data.remote.RentalApi
import javax.inject.Inject

sealed interface AuthDestination {
    data object Home : AuthDestination
    data object LicenseApproval : AuthDestination
    data object License : AuthDestination
    data class PendingPayment(val rentalId: String) : AuthDestination
}

/**
 * Ehliyet durumuna göre giriş sonrası yönlendirme kararını üretir.
 * Splash (soğuk başlangıç) ve OTP doğrulama (interaktif giriş) akışlarının
 * ikisi de aynı kararı vermesi gerektiği için tek yerde tutulur.
 *
 * Ehliyeti onaylı bir kullanıcının ödemesi alınmamış (COMPLETED + UNPAID) bir
 * yolculuğu varsa ana sayfaya değil, doğrudan ödeme ekranına yönlendirilir —
 * kullanıcı ödemeyi tamamlamadan uygulamayı kullanamaz.
 */
class ResolvePostAuthDestinationUseCase @Inject constructor(
    private val licenseApi: LicenseApi,
    private val rentalApi: RentalApi
) {
    suspend operator fun invoke(): AuthDestination {
        val licenseStatus = runCatching { licenseApi.getStatus() }.getOrNull()
        return when (licenseStatus?.status) {
            "APPROVED" -> resolveHomeOrPendingPayment()
            "UNDER_REVIEW" -> AuthDestination.LicenseApproval
            else -> AuthDestination.License
        }
    }

    private suspend fun resolveHomeOrPendingPayment(): AuthDestination {
        val unpaidRental = runCatching { rentalApi.listMine() }
            .getOrNull()
            ?.body()
            ?.firstOrNull { it.status == "COMPLETED" && it.paymentStatus == "UNPAID" }
        return unpaidRental?.let { AuthDestination.PendingPayment(it.id) } ?: AuthDestination.Home
    }
}

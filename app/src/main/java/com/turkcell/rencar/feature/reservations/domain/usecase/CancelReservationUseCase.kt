package com.turkcell.rencar.feature.reservations.domain.usecase

import com.turkcell.rencar.feature.reservations.domain.repository.ReservationRepository
import javax.inject.Inject

class CancelReservationUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.cancelReservation(id)
    }
}

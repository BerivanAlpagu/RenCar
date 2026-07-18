package com.turkcell.rencar.feature.reservations.domain.usecase

import com.turkcell.rencar.feature.reservations.domain.model.Reservation
import com.turkcell.rencar.feature.reservations.domain.repository.ReservationRepository
import javax.inject.Inject

class GetActiveReservationUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(): Result<Reservation?> {
        return repository.getActiveReservation()
    }
}

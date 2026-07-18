package com.turkcell.rencar.feature.reservations.domain.usecase

import com.turkcell.rencar.feature.reservations.domain.model.Reservation
import com.turkcell.rencar.feature.reservations.domain.repository.ReservationRepository
import javax.inject.Inject

class CreateReservationUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(vehicleId: String): Result<Reservation> {
        return repository.createReservation(vehicleId)
    }
}

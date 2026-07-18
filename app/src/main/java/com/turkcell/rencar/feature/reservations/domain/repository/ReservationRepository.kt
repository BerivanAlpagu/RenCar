package com.turkcell.rencar.feature.reservations.domain.repository

import com.turkcell.rencar.feature.reservations.domain.model.Reservation

interface ReservationRepository {
    suspend fun createReservation(vehicleId: String): Result<Reservation>
    suspend fun getActiveReservation(): Result<Reservation?>
    suspend fun cancelReservation(id: String): Result<Unit>
}

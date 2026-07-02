package com.turkcell.rencar.feature.rentals.data.repository

import com.turkcell.rencar.feature.rentals.domain.model.Rental
import com.turkcell.rencar.feature.rentals.domain.model.RentalStatus
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import java.time.LocalDateTime
import javax.inject.Inject

class DefaultRentalRepository @Inject constructor() : RentalRepository {
    override suspend fun getRentalHistory(): List<Rental> {
        return listOf(
            Rental(
                id = "1",
                vehicleId = "v1",
                vehicleBrand = "Renault",
                vehicleModel = "Clio",
                startDate = LocalDateTime.of(2026, 6, 26, 14, 8),
                endDate = LocalDateTime.of(2026, 6, 26, 14, 32),
                totalPrice = 110.50,
                durationMinutes = 24,
                distanceKm = 12.4,
                status = RentalStatus.COMPLETED
            ),
            Rental(
                id = "2",
                vehicleId = "v2",
                vehicleBrand = "Fiat",
                vehicleModel = "Egea",
                startDate = LocalDateTime.of(2026, 6, 24, 17, 47),
                endDate = LocalDateTime.of(2026, 6, 24, 18, 5),
                totalPrice = 86.00,
                durationMinutes = 18,
                distanceKm = 8.1,
                status = RentalStatus.COMPLETED
            ),
            Rental(
                id = "3",
                vehicleId = "v3",
                vehicleBrand = "Volkswagen",
                vehicleModel = "Polo",
                startDate = LocalDateTime.of(2026, 6, 21, 9, 17),
                endDate = LocalDateTime.of(2026, 6, 21, 9, 48),
                totalPrice = 142.00,
                durationMinutes = 31,
                distanceKm = 19.6,
                status = RentalStatus.COMPLETED
            ),
            Rental(
                id = "4",
                vehicleId = "v4",
                vehicleBrand = "Renault",
                vehicleModel = "Clio",
                startDate = LocalDateTime.of(2026, 6, 18, 20, 0),
                endDate = LocalDateTime.of(2026, 6, 18, 20, 14),
                totalPrice = 64.50,
                durationMinutes = 14,
                distanceKm = 6.2,
                status = RentalStatus.COMPLETED
            )
        )
    }
}

package com.turkcell.rencar.feature.rentals.data.repository

import com.turkcell.rencar.feature.rentals.data.remote.RentalApi
import com.turkcell.rencar.feature.rentals.data.remote.dto.CreateRentalDto
import com.turkcell.rencar.feature.rentals.domain.model.Rental
import com.turkcell.rencar.feature.rentals.domain.model.RentalStatus
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DefaultRentalRepository @Inject constructor(
    private val rentalApi: RentalApi
) : RentalRepository {
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

    override suspend fun createRental(vehicleId: String, endDate: String): Result<Rental> {
        return try {
            val response = rentalApi.createRental(CreateRentalDto(vehicleId, endDate))
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                
                // Parsers
                val formatter = DateTimeFormatter.ISO_DATE_TIME
                val startDate = try { LocalDateTime.parse(dto.startDate, formatter) } catch (e: Exception) { LocalDateTime.now() }
                val parsedEndDate = try { LocalDateTime.parse(dto.endDate, formatter) } catch (e: Exception) { LocalDateTime.now().plusHours(1) }
                val status = try { RentalStatus.valueOf(dto.status) } catch (e: Exception) { RentalStatus.ACTIVE }

                val rental = Rental(
                    id = dto.id,
                    vehicleId = dto.vehicleId,
                    vehicleBrand = "Kiralık", // Mock until we fetch vehicle details properly in Rental
                    vehicleModel = "Araç",
                    startDate = startDate,
                    endDate = parsedEndDate,
                    totalPrice = dto.totalPrice,
                    durationMinutes = 0,
                    distanceKm = 0.0,
                    status = status
                )
                Result.success(rental)
            } else {
                Result.failure(Exception("Failed to create rental: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

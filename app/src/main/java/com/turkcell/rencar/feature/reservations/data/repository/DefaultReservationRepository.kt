package com.turkcell.rencar.feature.reservations.data.repository

import com.turkcell.rencar.feature.reservations.data.remote.ReservationApi
import com.turkcell.rencar.feature.reservations.data.remote.dto.CreateReservationDto
import com.turkcell.rencar.feature.reservations.data.remote.dto.ReservationResponseDto
import com.turkcell.rencar.feature.reservations.data.remote.dto.ReservationVehicleSummaryDto
import com.turkcell.rencar.feature.reservations.domain.model.Reservation
import com.turkcell.rencar.feature.reservations.domain.model.ReservationStatus
import com.turkcell.rencar.feature.reservations.domain.model.ReservationVehicleSummary
import com.turkcell.rencar.feature.reservations.domain.repository.ReservationRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DefaultReservationRepository @Inject constructor(
    private val reservationApi: ReservationApi
) : ReservationRepository {

    override suspend fun createReservation(vehicleId: String): Result<Reservation> {
        return try {
            val response = reservationApi.create(CreateReservationDto(vehicleId))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Rezervasyon oluşturulamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActiveReservation(): Result<Reservation?> {
        return try {
            val response = reservationApi.getActive()
            when {
                response.isSuccessful && response.body() != null -> Result.success(response.body()!!.toDomain())
                response.code() == 404 -> Result.success(null)
                else -> Result.failure(Exception("Aktif rezervasyon alınamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelReservation(id: String): Result<Unit> {
        return try {
            val response = reservationApi.cancel(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Rezervasyon iptal edilemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private val isoFormatter = DateTimeFormatter.ISO_DATE_TIME

private fun ReservationVehicleSummaryDto.toDomain() = ReservationVehicleSummary(
    id = id,
    plate = plate,
    brand = brand,
    model = model,
    type = type,
    latitude = latitude,
    longitude = longitude,
    pricePerMinute = pricePerMinute
)

private fun ReservationResponseDto.toDomain() = Reservation(
    id = id,
    userId = userId,
    vehicleId = vehicleId,
    vehicle = vehicle.toDomain(),
    status = runCatching { ReservationStatus.valueOf(status) }.getOrDefault(ReservationStatus.ACTIVE),
    expiresAt = runCatching { LocalDateTime.parse(expiresAt, isoFormatter) }.getOrDefault(LocalDateTime.now()),
    remainingSeconds = remainingSeconds,
    createdAt = runCatching { LocalDateTime.parse(createdAt, isoFormatter) }.getOrDefault(LocalDateTime.now())
)

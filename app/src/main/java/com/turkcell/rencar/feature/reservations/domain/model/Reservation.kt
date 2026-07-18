package com.turkcell.rencar.feature.reservations.domain.model

import java.time.LocalDateTime

enum class ReservationStatus {
    ACTIVE,
    CONVERTED,
    CANCELLED,
    EXPIRED
}

data class ReservationVehicleSummary(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val pricePerMinute: Double
)

data class Reservation(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: ReservationVehicleSummary,
    val status: ReservationStatus,
    val expiresAt: LocalDateTime,
    val remainingSeconds: Long,
    val createdAt: LocalDateTime
)

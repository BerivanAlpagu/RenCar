package com.turkcell.rencar.feature.rentals.domain.model

import java.time.LocalDateTime

data class Rental(
    val id: String,
    val vehicleId: String,
    val vehicleBrand: String,
    val vehicleModel: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val totalPrice: Double,
    val durationMinutes: Int,
    val distanceKm: Double,
    val status: RentalStatus
)

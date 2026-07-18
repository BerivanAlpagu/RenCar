package com.turkcell.rencar.feature.rentals.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RentalVehicleSummaryDto(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String
)

@Serializable
data class RentalResponseDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: RentalVehicleSummaryDto,
    val plan: String,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val endDate: String? = null,
    val totalPrice: Double? = null,
    val startFee: Double = 0.0,
    val serviceFee: Double? = null,
    val distanceKm: Double = 0.0,
    val durationMinutes: Int = 0,
    val status: String,
    val paymentStatus: String,
    val paymentMethod: String? = null,
    val discountAmount: Double = 0.0,
    val createdAt: String
)

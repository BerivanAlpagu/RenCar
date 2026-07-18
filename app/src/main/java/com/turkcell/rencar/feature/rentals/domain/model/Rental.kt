package com.turkcell.rencar.feature.rentals.domain.model

import java.time.LocalDateTime

data class RentalVehicleSummary(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String
)

data class Rental(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: RentalVehicleSummary,
    val plan: RentalPlan,
    val startedAt: LocalDateTime?,
    val endedAt: LocalDateTime?,
    val endDate: LocalDateTime?,
    val totalPrice: Double?,
    val startFee: Double,
    val serviceFee: Double?,
    val distanceKm: Double,
    val durationMinutes: Int,
    val status: RentalStatus,
    val paymentStatus: RentalPaymentStatus,
    val paymentMethod: RentalPaymentMethod?,
    val discountAmount: Double,
    val createdAt: LocalDateTime
)

data class RentalPhoto(
    val side: RentalPhotoSide,
    val imageUrl: String
)

data class RentalPhotosState(
    val rentalId: String,
    val photos: List<RentalPhoto>,
    val uploadedCount: Int,
    val remainingSides: List<RentalPhotoSide>,
    val photosComplete: Boolean
)

data class ActiveRentalInfo(
    val rental: Rental,
    val elapsedSeconds: Long,
    val currentCost: Double
)

data class FinishRentalInfo(
    val rental: Rental,
    val usageFee: Double,
    val elapsedSeconds: Long
)

data class PaymentReceipt(
    val rentalId: String,
    val paymentStatus: RentalPaymentStatus,
    val method: RentalPaymentMethod,
    val totalPrice: Double,
    val discountAmount: Double,
    val paidAmount: Double,
    val walletBalance: Double?
)

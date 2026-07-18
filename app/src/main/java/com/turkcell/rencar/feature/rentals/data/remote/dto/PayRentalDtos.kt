package com.turkcell.rencar.feature.rentals.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PayRentalDto(
    val method: String,
    val cardId: String? = null,
    val discountCode: String? = null,
    val iyzicoPaymentId: String? = null
)

@Serializable
data class PaidCardSummaryDto(
    val brand: String,
    val last4: String
)

@Serializable
data class PayRentalResponseDto(
    val rentalId: String,
    val paymentStatus: String,
    val method: String,
    val totalPrice: Double,
    val discountAmount: Double,
    val paidAmount: Double,
    val walletBalance: Double? = null,
    val card: PaidCardSummaryDto? = null
)

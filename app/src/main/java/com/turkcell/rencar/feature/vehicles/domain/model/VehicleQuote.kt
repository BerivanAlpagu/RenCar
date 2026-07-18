package com.turkcell.rencar.feature.vehicles.domain.model

data class VehicleQuote(
    val vehicleId: String,
    val plan: String,
    val minutes: Int,
    val usageFee: Double,
    val startFee: Double,
    val serviceFee: Double,
    val estimatedTotal: Double
)

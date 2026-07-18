package com.turkcell.rencar.feature.rentals.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateRentalDto(
    val vehicleId: String,
    val plan: String,
    val endDate: String? = null
)

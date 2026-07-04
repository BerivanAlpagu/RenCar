package com.turkcell.rencar.feature.rentals.domain.repository

import com.turkcell.rencar.feature.rentals.domain.model.Rental

interface RentalRepository {
    suspend fun getRentalHistory(): List<Rental>
    suspend fun createRental(vehicleId: String, endDate: String): Result<Rental>
}

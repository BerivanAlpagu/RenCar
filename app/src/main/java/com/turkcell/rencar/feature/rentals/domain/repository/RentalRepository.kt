package com.turkcell.rencar.feature.rentals.domain.repository

import com.turkcell.rencar.feature.rentals.domain.model.Rental

interface RentalRepository {
    suspend fun getRentalHistory(): List<Rental>
}

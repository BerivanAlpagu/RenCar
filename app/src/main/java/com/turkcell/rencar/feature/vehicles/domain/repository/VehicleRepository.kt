package com.turkcell.rencar.feature.vehicles.domain.repository

import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle

interface VehicleRepository {
    suspend fun getAvailableVehicles(type: String? = null): Result<List<Vehicle>>
}

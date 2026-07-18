package com.turkcell.rencar.feature.vehicles.domain.repository

import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle
import com.turkcell.rencar.feature.vehicles.domain.model.VehicleQuote

interface VehicleRepository {
    suspend fun getAvailableVehicles(
        type: String? = null,
        segment: String? = null,
        includeBusy: Boolean = false
    ): Result<List<Vehicle>>

    suspend fun getVehicle(id: String): Result<Vehicle>

    suspend fun getQuote(id: String, plan: String, minutes: Int): Result<VehicleQuote>
}

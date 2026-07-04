package com.turkcell.rencar.feature.vehicles.domain.usecase

import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle
import com.turkcell.rencar.feature.vehicles.domain.repository.VehicleRepository
import javax.inject.Inject

class GetAvailableVehiclesUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(type: String? = null): Result<List<Vehicle>> {
        return repository.getAvailableVehicles(type)
    }
}

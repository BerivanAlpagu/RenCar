package com.turkcell.rencar.feature.vehicles.domain.usecase

import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle
import com.turkcell.rencar.feature.vehicles.domain.repository.VehicleRepository
import javax.inject.Inject

class GetVehicleUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Result<Vehicle> {
        return repository.getVehicle(id)
    }
}

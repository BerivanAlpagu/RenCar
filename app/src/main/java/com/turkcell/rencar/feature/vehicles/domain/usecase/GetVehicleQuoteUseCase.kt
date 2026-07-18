package com.turkcell.rencar.feature.vehicles.domain.usecase

import com.turkcell.rencar.feature.vehicles.domain.model.VehicleQuote
import com.turkcell.rencar.feature.vehicles.domain.repository.VehicleRepository
import javax.inject.Inject

class GetVehicleQuoteUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(id: String, plan: String, minutes: Int): Result<VehicleQuote> {
        return repository.getQuote(id, plan, minutes)
    }
}

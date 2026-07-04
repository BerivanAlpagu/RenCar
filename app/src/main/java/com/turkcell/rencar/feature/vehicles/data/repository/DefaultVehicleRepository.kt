package com.turkcell.rencar.feature.vehicles.data.repository

import com.turkcell.rencar.feature.vehicles.data.remote.VehicleApi
import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle
import com.turkcell.rencar.feature.vehicles.domain.repository.VehicleRepository
import javax.inject.Inject

class DefaultVehicleRepository @Inject constructor(
    private val vehicleApi: VehicleApi
) : VehicleRepository {
    override suspend fun getAvailableVehicles(type: String?): Result<List<Vehicle>> {
        return try {
            val response = vehicleApi.getVehicles(type = type)
            if (response.isSuccessful && response.body() != null) {
                val vehicles = response.body()!!.map { dto ->
                    Vehicle(
                        id = dto.id,
                        plate = dto.plate,
                        brand = dto.brand,
                        model = dto.model,
                        type = dto.type,
                        pricePerDay = dto.pricePerDay,
                        latitude = dto.latitude,
                        longitude = dto.longitude
                    )
                }
                Result.success(vehicles)
            } else {
                Result.failure(Exception("Araçlar getirilirken hata oluştu: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.turkcell.rencar.feature.vehicles.data.repository

import com.turkcell.rencar.feature.vehicles.data.remote.VehicleApi
import com.turkcell.rencar.feature.vehicles.data.remote.dto.QuoteResponseDto
import com.turkcell.rencar.feature.vehicles.data.remote.dto.VehicleResponseDto
import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle
import com.turkcell.rencar.feature.vehicles.domain.model.VehicleQuote
import com.turkcell.rencar.feature.vehicles.domain.repository.VehicleRepository
import javax.inject.Inject

class DefaultVehicleRepository @Inject constructor(
    private val vehicleApi: VehicleApi
) : VehicleRepository {
    override suspend fun getAvailableVehicles(
        type: String?,
        segment: String?,
        includeBusy: Boolean
    ): Result<List<Vehicle>> {
        return try {
            val response = vehicleApi.getVehicles(
                type = type,
                segment = segment,
                includeBusy = if (includeBusy) true else null
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Araçlar getirilirken hata oluştu: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVehicle(id: String): Result<Vehicle> {
        return try {
            val response = vehicleApi.getOne(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Araç bulunamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuote(id: String, plan: String, minutes: Int): Result<VehicleQuote> {
        return try {
            val response = vehicleApi.getQuote(id, plan, minutes)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Fiyat tahmini alınamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun VehicleResponseDto.toDomain() = Vehicle(
    id = id,
    plate = plate,
    brand = brand,
    model = model,
    type = type,
    pricePerDay = pricePerDay,
    pricePerMinute = pricePerMinute,
    pricePerHour = pricePerHour,
    fuelPercent = fuelPercent,
    rangeKm = rangeKm,
    transmission = transmission,
    seats = seats,
    segment = segment,
    status = status,
    latitude = latitude,
    longitude = longitude
)

private fun QuoteResponseDto.toDomain() = VehicleQuote(
    vehicleId = vehicleId,
    plan = plan,
    minutes = minutes,
    usageFee = usageFee,
    startFee = startFee,
    serviceFee = serviceFee,
    estimatedTotal = estimatedTotal
)

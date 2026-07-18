package com.turkcell.rencar.feature.rentals.data.repository

import com.turkcell.rencar.feature.rentals.data.remote.RentalApi
import com.turkcell.rencar.feature.rentals.data.remote.dto.ActiveRentalResponseDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.CreateRentalDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.FinishRentalResponseDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.PayRentalDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.PayRentalResponseDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.RentalPhotosStateDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.RentalResponseDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.RentalVehicleSummaryDto
import com.turkcell.rencar.feature.rentals.domain.model.ActiveRentalInfo
import com.turkcell.rencar.feature.rentals.domain.model.FinishRentalInfo
import com.turkcell.rencar.feature.rentals.domain.model.PaymentReceipt
import com.turkcell.rencar.feature.rentals.domain.model.Rental
import com.turkcell.rencar.feature.rentals.domain.model.RentalPaymentMethod
import com.turkcell.rencar.feature.rentals.domain.model.RentalPaymentStatus
import com.turkcell.rencar.feature.rentals.domain.model.RentalPhoto
import com.turkcell.rencar.feature.rentals.domain.model.RentalPhotoSide
import com.turkcell.rencar.feature.rentals.domain.model.RentalPhotosState
import com.turkcell.rencar.feature.rentals.domain.model.RentalPlan
import com.turkcell.rencar.feature.rentals.domain.model.RentalStatus
import com.turkcell.rencar.feature.rentals.domain.model.RentalVehicleSummary
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DefaultRentalRepository @Inject constructor(
    private val rentalApi: RentalApi
) : RentalRepository {

    override suspend fun getRentalHistory(): List<Rental> {
        val response = rentalApi.listMine()
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.map { it.toDomain() }
        }
        throw Exception("Kiralama geçmişi alınamadı: ${response.code()}")
    }

    override suspend fun createRental(vehicleId: String, plan: RentalPlan, endDate: String?): Result<Rental> {
        return try {
            val response = rentalApi.createRental(CreateRentalDto(vehicleId, plan.name, endDate))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Kiralama oluşturulamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRental(id: String): Result<Rental> {
        return try {
            val response = rentalApi.getOne(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Kiralama bulunamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActiveRental(): Result<ActiveRentalInfo?> {
        return try {
            val response = rentalApi.getActive()
            when {
                response.isSuccessful && response.body() != null -> Result.success(response.body()!!.toDomain())
                response.code() == 404 -> Result.success(null)
                else -> Result.failure(Exception("Aktif kiralama alınamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPhoto(rentalId: String, side: RentalPhotoSide, file: File): Result<RentalPhotosState> {
        return try {
            val sideBody = side.name.toRequestBody("text/plain".toMediaType())
            val filePart = MultipartBody.Part.createFormData(
                "file",
                file.name,
                file.asRequestBody("image/jpeg".toMediaType())
            )
            val response = rentalApi.uploadPhoto(rentalId, sideBody, filePart)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Fotoğraf yüklenemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPhotosState(rentalId: String): Result<RentalPhotosState> {
        return try {
            val response = rentalApi.getPhotos(rentalId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Fotoğraf durumu alınamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startRental(id: String): Result<Rental> {
        return try {
            val response = rentalApi.start(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Kiralama başlatılamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun finishRental(id: String): Result<FinishRentalInfo> {
        return try {
            val response = rentalApi.finish(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Kiralama bitirilemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun payRental(
        id: String,
        method: RentalPaymentMethod,
        cardId: String?,
        discountCode: String?
    ): Result<PaymentReceipt> {
        return try {
            val response = rentalApi.pay(id, PayRentalDto(method.name, cardId, discountCode))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Ödeme alınamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelRental(id: String): Result<Unit> {
        return try {
            val response = rentalApi.cancel(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Kiralama iptal edilemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private val isoFormatter = DateTimeFormatter.ISO_DATE_TIME

private fun parseDateTime(value: String?): LocalDateTime? {
    if (value.isNullOrBlank()) return null
    return runCatching { LocalDateTime.parse(value, isoFormatter) }.getOrNull()
}

private fun RentalVehicleSummaryDto.toDomain() = RentalVehicleSummary(
    id = id,
    plate = plate,
    brand = brand,
    model = model,
    type = type
)

private fun RentalResponseDto.toDomain() = Rental(
    id = id,
    userId = userId,
    vehicleId = vehicleId,
    vehicle = vehicle.toDomain(),
    plan = runCatching { RentalPlan.valueOf(plan) }.getOrDefault(RentalPlan.DAILY),
    startedAt = parseDateTime(startedAt),
    endedAt = parseDateTime(endedAt),
    endDate = parseDateTime(endDate),
    totalPrice = totalPrice,
    startFee = startFee,
    serviceFee = serviceFee,
    distanceKm = distanceKm,
    durationMinutes = durationMinutes,
    status = runCatching { RentalStatus.valueOf(status) }.getOrDefault(RentalStatus.PREPARING),
    paymentStatus = runCatching { RentalPaymentStatus.valueOf(paymentStatus) }.getOrDefault(RentalPaymentStatus.UNPAID),
    paymentMethod = paymentMethod?.let { runCatching { RentalPaymentMethod.valueOf(it) }.getOrNull() },
    discountAmount = discountAmount,
    createdAt = parseDateTime(createdAt) ?: LocalDateTime.now()
)

private fun ActiveRentalResponseDto.toDomain(): ActiveRentalInfo {
    val rental = Rental(
        id = id,
        userId = userId,
        vehicleId = vehicleId,
        vehicle = vehicle.toDomain(),
        plan = runCatching { RentalPlan.valueOf(plan) }.getOrDefault(RentalPlan.DAILY),
        startedAt = parseDateTime(startedAt),
        endedAt = parseDateTime(endedAt),
        endDate = parseDateTime(endDate),
        totalPrice = totalPrice,
        startFee = startFee,
        serviceFee = serviceFee,
        distanceKm = distanceKm,
        durationMinutes = durationMinutes,
        status = runCatching { RentalStatus.valueOf(status) }.getOrDefault(RentalStatus.ACTIVE),
        paymentStatus = runCatching { RentalPaymentStatus.valueOf(paymentStatus) }.getOrDefault(RentalPaymentStatus.UNPAID),
        paymentMethod = paymentMethod?.let { runCatching { RentalPaymentMethod.valueOf(it) }.getOrNull() },
        discountAmount = discountAmount,
        createdAt = parseDateTime(createdAt) ?: LocalDateTime.now()
    )
    return ActiveRentalInfo(rental = rental, elapsedSeconds = elapsedSeconds, currentCost = currentCost)
}

private fun FinishRentalResponseDto.toDomain(): FinishRentalInfo {
    val rental = Rental(
        id = id,
        userId = userId,
        vehicleId = vehicleId,
        vehicle = vehicle.toDomain(),
        plan = runCatching { RentalPlan.valueOf(plan) }.getOrDefault(RentalPlan.DAILY),
        startedAt = parseDateTime(startedAt),
        endedAt = parseDateTime(endedAt),
        endDate = parseDateTime(endDate),
        totalPrice = totalPrice,
        startFee = startFee,
        serviceFee = serviceFee,
        distanceKm = distanceKm,
        durationMinutes = durationMinutes,
        status = runCatching { RentalStatus.valueOf(status) }.getOrDefault(RentalStatus.COMPLETED),
        paymentStatus = runCatching { RentalPaymentStatus.valueOf(paymentStatus) }.getOrDefault(RentalPaymentStatus.UNPAID),
        paymentMethod = paymentMethod?.let { runCatching { RentalPaymentMethod.valueOf(it) }.getOrNull() },
        discountAmount = discountAmount,
        createdAt = parseDateTime(createdAt) ?: LocalDateTime.now()
    )
    return FinishRentalInfo(rental = rental, usageFee = usageFee, elapsedSeconds = elapsedSeconds)
}

private fun RentalPhotosStateDto.toDomain() = RentalPhotosState(
    rentalId = rentalId,
    photos = photos.map { photo ->
        RentalPhoto(
            side = runCatching { RentalPhotoSide.valueOf(photo.side) }.getOrDefault(RentalPhotoSide.FRONT),
            imageUrl = photo.imageUrl
        )
    },
    uploadedCount = uploadedCount,
    remainingSides = remainingSides.mapNotNull { side -> runCatching { RentalPhotoSide.valueOf(side) }.getOrNull() },
    photosComplete = photosComplete
)

private fun PayRentalResponseDto.toDomain() = PaymentReceipt(
    rentalId = rentalId,
    paymentStatus = runCatching { RentalPaymentStatus.valueOf(paymentStatus) }.getOrDefault(RentalPaymentStatus.UNPAID),
    method = runCatching { RentalPaymentMethod.valueOf(method) }.getOrDefault(RentalPaymentMethod.WALLET),
    totalPrice = totalPrice,
    discountAmount = discountAmount,
    paidAmount = paidAmount,
    walletBalance = walletBalance
)

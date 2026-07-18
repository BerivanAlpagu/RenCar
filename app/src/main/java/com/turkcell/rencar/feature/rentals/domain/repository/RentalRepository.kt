package com.turkcell.rencar.feature.rentals.domain.repository

import com.turkcell.rencar.feature.rentals.domain.model.ActiveRentalInfo
import com.turkcell.rencar.feature.rentals.domain.model.FinishRentalInfo
import com.turkcell.rencar.feature.rentals.domain.model.PaymentReceipt
import com.turkcell.rencar.feature.rentals.domain.model.Rental
import com.turkcell.rencar.feature.rentals.domain.model.RentalPaymentMethod
import com.turkcell.rencar.feature.rentals.domain.model.RentalPhotoSide
import com.turkcell.rencar.feature.rentals.domain.model.RentalPhotosState
import com.turkcell.rencar.feature.rentals.domain.model.RentalPlan
import com.turkcell.rencar.feature.rentals.domain.model.VehicleLocation
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RentalRepository {
    suspend fun getRentalHistory(): List<Rental>
    suspend fun createRental(vehicleId: String, plan: RentalPlan, endDate: String? = null): Result<Rental>
    suspend fun getRental(id: String): Result<Rental>
    suspend fun getActiveRental(): Result<ActiveRentalInfo?>
    suspend fun uploadPhoto(rentalId: String, side: RentalPhotoSide, file: File): Result<RentalPhotosState>
    suspend fun getPhotosState(rentalId: String): Result<RentalPhotosState>
    suspend fun startRental(id: String): Result<Rental>
    suspend fun finishRental(id: String): Result<FinishRentalInfo>
    suspend fun payRental(
        id: String,
        method: RentalPaymentMethod,
        cardId: String? = null,
        discountCode: String? = null
    ): Result<PaymentReceipt>
    suspend fun cancelRental(id: String): Result<Unit>
    fun observeMyVehicleLocation(): Flow<VehicleLocation>
}

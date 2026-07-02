package com.turkcell.rencar.feature.rentals.domain.usecase

import com.turkcell.rencar.feature.rentals.domain.model.Rental
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import javax.inject.Inject

class GetRentalHistoryUseCase @Inject constructor(
    private val repository: RentalRepository
) {
    suspend operator fun invoke(): Result<List<Rental>> {
        return try {
            Result.success(repository.getRentalHistory())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

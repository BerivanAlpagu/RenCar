package com.turkcell.rencar.feature.rentals.presentation.history

import com.turkcell.rencar.feature.rentals.domain.model.Rental

data class RentalHistoryState(
    val isLoading: Boolean = false,
    val rentals: List<Rental> = emptyList(),
    val errorMessage: String? = null,
    val totalTripsCount: Int = 0,
    val totalSpend: Double = 0.0
)

sealed interface RentalHistoryEvent {
    object LoadRentals : RentalHistoryEvent
    object RefreshRentals : RentalHistoryEvent
}

sealed interface RentalHistoryEffect {
    data class ShowSnackbar(val message: String) : RentalHistoryEffect
    data class NavigateToDetail(val rentalId: String) : RentalHistoryEffect
}

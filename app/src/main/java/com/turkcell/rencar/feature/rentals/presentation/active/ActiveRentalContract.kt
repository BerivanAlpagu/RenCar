package com.turkcell.rencar.feature.rentals.presentation.active

import com.turkcell.rencar.feature.rentals.domain.model.Rental
import com.turkcell.rencar.feature.rentals.domain.model.VehicleLocation

data class ActiveRentalState(
    val isLoading: Boolean = false,
    val rental: Rental? = null,
    val elapsedSeconds: Long = 0,
    val currentCost: Double = 0.0,
    val distanceKm: Double = 0.0,
    val isFinishing: Boolean = false,
    val error: String? = null,
    val vehicleLocation: VehicleLocation? = null,
    val routePoints: List<VehicleLocation> = emptyList()
)

sealed interface ActiveRentalEvent {
    data class ScreenOpened(val rentalId: String) : ActiveRentalEvent
    data object FinishClicked : ActiveRentalEvent
}

sealed interface ActiveRentalEffect {
    data class NavigateToReturnPhoto(val rentalId: String) : ActiveRentalEffect
    data class ShowError(val message: String) : ActiveRentalEffect
}

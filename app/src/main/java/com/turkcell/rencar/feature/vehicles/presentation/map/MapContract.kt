package com.turkcell.rencar.feature.vehicles.presentation.map

import android.location.Location
import com.turkcell.rencar.feature.rentals.domain.model.Rental
import com.turkcell.rencar.feature.reservations.domain.model.Reservation
import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle

data class MapState(
    val isLoading: Boolean = false,
    val vehicles: List<Vehicle> = emptyList(),
    val userLocation: Location? = null,
    val selectedVehicle: Vehicle? = null,
    val error: String? = null,
    val hasLocationPermission: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: String? = null, // null for Tümü, "HATCHBACK", "SEDAN", "SUV"
    val locationText: String = "Konumun",
    val activeReservation: Reservation? = null,
    val activeRental: Rental? = null,
    val isReserving: Boolean = false
)

sealed interface MapEvent {
    data object OnLocationPermissionGranted : MapEvent
    data object OnLocationPermissionDenied : MapEvent
    data class OnFilterChanged(val type: String?) : MapEvent
    data class OnSearchQueryChanged(val query: String) : MapEvent
    data class OnVehicleClicked(val id: String) : MapEvent
    data object DismissVehicleDetails : MapEvent
    data class OnReserveClicked(val vehicleId: String) : MapEvent
    data class OnUnlockClicked(val vehicleId: String) : MapEvent
    data class OnResumeRentalClicked(val vehicleId: String) : MapEvent
    data class OnCancelReservationClicked(val reservationId: String) : MapEvent
}

sealed interface MapEffect {
    data class ShowError(val message: String) : MapEffect
    data class NavigateToVehicleDetails(val id: String) : MapEffect
    data class NavigateToUnlock(val vehicleId: String) : MapEffect
    data class NavigateToHandoverPhoto(val rentalId: String) : MapEffect
    data class NavigateToActiveRental(val rentalId: String) : MapEffect
}

package com.turkcell.rencar.feature.vehicles.presentation.map

import android.location.Location
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
    val locationText: String = "Konumun"
)

sealed interface MapEvent {
    data object OnLocationPermissionGranted : MapEvent
    data object OnLocationPermissionDenied : MapEvent
    data class OnFilterChanged(val type: String?) : MapEvent
    data class OnSearchQueryChanged(val query: String) : MapEvent
    data class OnVehicleClicked(val id: String) : MapEvent
    data object DismissVehicleDetails : MapEvent
}

sealed interface MapEffect {
    data class ShowError(val message: String) : MapEffect
    data class NavigateToVehicleDetails(val id: String) : MapEffect
}

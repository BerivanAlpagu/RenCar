package com.turkcell.rencar.feature.vehicles.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.core.location.LocationTracker
import com.turkcell.rencar.feature.vehicles.domain.usecase.GetAvailableVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAvailableVehiclesUseCase: GetAvailableVehiclesUseCase,
    private val locationTracker: LocationTracker,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MapEffect>()
    val effect: SharedFlow<MapEffect> = _effect.asSharedFlow()

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.OnLocationPermissionGranted -> {
                _state.update { it.copy(hasLocationPermission = true) }
                fetchLocation()
                fetchVehicles(null)
            }
            is MapEvent.OnLocationPermissionDenied -> {
                _state.update { it.copy(hasLocationPermission = false) }
                fetchVehicles(null) // Still fetch vehicles even without location
            }
            is MapEvent.OnFilterChanged -> {
                _state.update { it.copy(selectedFilter = event.type) }
                fetchVehicles(event.type)
            }
            is MapEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            is MapEvent.OnVehicleClicked -> {
                val vehicle = _state.value.vehicles.find { it.id == event.id }
                if (vehicle != null) {
                    _state.update { it.copy(selectedVehicle = vehicle) }
                }
            }
            is MapEvent.DismissVehicleDetails -> {
                _state.update { it.copy(selectedVehicle = null) }
            }
        }
    }

    private fun fetchLocation() {
        viewModelScope.launch {
            val location = locationTracker.getCurrentLocation()
            var locationText = "Konumun"
            if (location != null) {
                try {
                    val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        locationText = address.subLocality ?: address.locality ?: "Konumun"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            _state.update { it.copy(userLocation = location, locationText = locationText) }
        }
    }

    private fun fetchVehicles(type: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = getAvailableVehiclesUseCase(type)
            
            result.onSuccess { vehicles ->
                _state.update { it.copy(isLoading = false, vehicles = vehicles) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
                _effect.emit(MapEffect.ShowError(error.message ?: "Bilinmeyen bir hata oluştu"))
            }
        }
    }
}

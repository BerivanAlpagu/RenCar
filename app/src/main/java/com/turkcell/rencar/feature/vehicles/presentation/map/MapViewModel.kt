package com.turkcell.rencar.feature.vehicles.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.core.location.LocationTracker
import com.turkcell.rencar.feature.rentals.domain.model.RentalStatus
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import com.turkcell.rencar.feature.reservations.domain.repository.ReservationRepository
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
    private val reservationRepository: ReservationRepository,
    private val rentalRepository: RentalRepository,
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
                refreshMyActivity()
            }
            is MapEvent.OnLocationPermissionDenied -> {
                _state.update { it.copy(hasLocationPermission = false) }
                fetchVehicles(null) // Still fetch vehicles even without location
                refreshMyActivity()
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
            is MapEvent.OnReserveClicked -> reserveVehicle(event.vehicleId)
            is MapEvent.OnUnlockClicked -> {
                viewModelScope.launch {
                    _effect.emit(MapEffect.NavigateToUnlock(event.vehicleId))
                }
            }
            is MapEvent.OnResumeRentalClicked -> {
                val rental = _state.value.activeRental
                if (rental != null && rental.vehicleId == event.vehicleId) {
                    viewModelScope.launch {
                        val effect = if (rental.status == RentalStatus.ACTIVE) {
                            MapEffect.NavigateToActiveRental(rental.id)
                        } else {
                            MapEffect.NavigateToHandoverPhoto(rental.id)
                        }
                        _effect.emit(effect)
                    }
                }
            }
            is MapEvent.OnCancelReservationClicked -> cancelReservation(event.reservationId)
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
            val result = getAvailableVehiclesUseCase(type = type, includeBusy = true)

            result.onSuccess { vehicles ->
                _state.update { it.copy(isLoading = false, vehicles = vehicles) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
                _effect.emit(MapEffect.ShowError(error.message ?: "Bilinmeyen bir hata oluştu"))
            }
        }
    }

    /**
     * Uygulama kapanıp açıldığında yarım kalan rezervasyon/kiralamayı geri kazandırır.
     * GET /rentals/active yalnız ACTIVE (başlamış) yolculuğu döner — PREPARING (fotoğraf
     * adımında yarım kalmış) kiralamayı yakalamak için tüm kiralama geçmişinden aranır.
     */
    private fun refreshMyActivity() {
        viewModelScope.launch {
            reservationRepository.getActiveReservation()
                .onSuccess { reservation -> _state.update { it.copy(activeReservation = reservation) } }
            runCatching { rentalRepository.getRentalHistory() }
                .onSuccess { rentals ->
                    val inProgress = rentals.firstOrNull {
                        it.status == RentalStatus.PREPARING || it.status == RentalStatus.ACTIVE
                    }
                    _state.update { it.copy(activeRental = inProgress) }
                }
        }
    }

    private fun reserveVehicle(vehicleId: String) {
        if (_state.value.isReserving) return
        viewModelScope.launch {
            _state.update { it.copy(isReserving = true) }
            reservationRepository.createReservation(vehicleId)
                .onSuccess { reservation ->
                    _state.update { it.copy(isReserving = false, activeReservation = reservation) }
                    fetchVehicles(_state.value.selectedFilter)
                }
                .onFailure { error ->
                    _state.update { it.copy(isReserving = false) }
                    _effect.emit(MapEffect.ShowError(error.message ?: "Rezervasyon oluşturulamadı"))
                }
        }
    }

    private fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            reservationRepository.cancelReservation(reservationId)
                .onSuccess {
                    _state.update { it.copy(activeReservation = null, selectedVehicle = null) }
                    fetchVehicles(_state.value.selectedFilter)
                }
                .onFailure { error ->
                    _effect.emit(MapEffect.ShowError(error.message ?: "Rezervasyon iptal edilemedi"))
                }
        }
    }
}

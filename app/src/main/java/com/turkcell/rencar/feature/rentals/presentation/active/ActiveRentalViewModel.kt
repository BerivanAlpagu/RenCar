package com.turkcell.rencar.feature.rentals.presentation.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.rentals.domain.model.Rental
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ActiveRentalViewModel @Inject constructor(
    private val rentalRepository: RentalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ActiveRentalState())
    val state: StateFlow<ActiveRentalState> = _state.asStateFlow()

    fun startRental(vehicleId: String) {
        if (_state.value.rental != null || _state.value.isLoading) return
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val endDate = LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_DATE_TIME)
            
            val result = rentalRepository.createRental(vehicleId, endDate)
            result.onSuccess { rental ->
                _state.value = _state.value.copy(isLoading = false, rental = rental)
            }.onFailure { e ->
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

data class ActiveRentalState(
    val isLoading: Boolean = false,
    val rental: Rental? = null,
    val error: String? = null
)

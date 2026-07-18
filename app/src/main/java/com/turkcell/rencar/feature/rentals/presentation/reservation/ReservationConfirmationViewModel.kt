package com.turkcell.rencar.feature.rentals.presentation.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.rentals.domain.model.RentalPlan
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import com.turkcell.rencar.feature.vehicles.domain.usecase.GetVehicleQuoteUseCase
import com.turkcell.rencar.feature.vehicles.domain.usecase.GetVehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReservationConfirmationViewModel @Inject constructor(
    private val getVehicleUseCase: GetVehicleUseCase,
    private val getVehicleQuoteUseCase: GetVehicleQuoteUseCase,
    private val rentalRepository: RentalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationConfirmationState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ReservationConfirmationEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: ReservationConfirmationEvent) {
        when (event) {
            is ReservationConfirmationEvent.LoadVehicle -> loadVehicle(event.vehicleId)
            is ReservationConfirmationEvent.PlanSelected -> selectPlan(event.plan)
            is ReservationConfirmationEvent.TermsToggled -> {
                _state.update { it.copy(isTermsAccepted = event.accepted) }
            }
            is ReservationConfirmationEvent.ConfirmClicked -> confirm()
        }
    }

    private fun loadVehicle(vehicleId: String) {
        _state.update { it.copy(vehicleId = vehicleId, isLoading = true, error = null) }
        viewModelScope.launch {
            getVehicleUseCase(vehicleId)
                .onSuccess { vehicle ->
                    _state.update { it.copy(isLoading = false, vehicle = vehicle) }
                    fetchQuote()
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effect.send(ReservationConfirmationEffect.ShowError(error.message ?: "Araç bilgisi alınamadı"))
                }
        }
    }

    private fun selectPlan(plan: RentalPlan) {
        val minutes = if (plan == RentalPlan.DAILY) 24 * 60 else 30
        _state.update { it.copy(selectedPlan = plan, estimatedMinutes = minutes) }
        fetchQuote()
    }

    private fun fetchQuote() {
        val vehicleId = _state.value.vehicleId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isQuoteLoading = true) }
            getVehicleQuoteUseCase(vehicleId, _state.value.selectedPlan.name, _state.value.estimatedMinutes)
                .onSuccess { quote ->
                    _state.update { it.copy(isQuoteLoading = false, quote = quote) }
                }
                .onFailure {
                    _state.update { it.copy(isQuoteLoading = false, quote = null) }
                }
        }
    }

    private fun confirm() {
        val vehicleId = _state.value.vehicleId ?: return
        if (!_state.value.isTermsAccepted || _state.value.isConfirming) return

        viewModelScope.launch {
            _state.update { it.copy(isConfirming = true) }
            val plan = _state.value.selectedPlan
            val endDate = if (plan == RentalPlan.DAILY) {
                LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME)
            } else {
                null
            }
            rentalRepository.createRental(vehicleId, plan, endDate)
                .onSuccess { rental ->
                    _state.update { it.copy(isConfirming = false) }
                    _effect.send(ReservationConfirmationEffect.NavigateToHandover(rental.id))
                }
                .onFailure { error ->
                    _state.update { it.copy(isConfirming = false) }
                    _effect.send(ReservationConfirmationEffect.ShowError(error.message ?: "Kilit açılamadı"))
                }
        }
    }
}

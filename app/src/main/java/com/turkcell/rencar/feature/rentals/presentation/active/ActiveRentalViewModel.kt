package com.turkcell.rencar.feature.rentals.presentation.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val POLL_INTERVAL_MS = 4000L

@HiltViewModel
class ActiveRentalViewModel @Inject constructor(
    private val rentalRepository: RentalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ActiveRentalState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ActiveRentalEffect>()
    val effect = _effect.receiveAsFlow()

    private var pollingJob: Job? = null

    fun onEvent(event: ActiveRentalEvent) {
        when (event) {
            is ActiveRentalEvent.ScreenOpened -> openScreen(event.rentalId)
            is ActiveRentalEvent.FinishClicked -> finish()
        }
    }

    private fun openScreen(rentalId: String) {
        if (_state.value.rental?.id == rentalId && pollingJob != null) return
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            rentalRepository.getRental(rentalId)
                .onSuccess { rental -> _state.update { it.copy(isLoading = false, rental = rental) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effect.send(ActiveRentalEffect.ShowError(error.message ?: "Kiralama bilgisi alınamadı"))
                }
        }
        startPolling()
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                rentalRepository.getActiveRental()
                    .onSuccess { activeInfo ->
                        if (activeInfo != null) {
                            _state.update {
                                it.copy(
                                    rental = activeInfo.rental,
                                    elapsedSeconds = activeInfo.elapsedSeconds,
                                    currentCost = activeInfo.currentCost,
                                    distanceKm = activeInfo.rental.distanceKm
                                )
                            }
                        }
                    }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun finish() {
        val rentalId = _state.value.rental?.id ?: return
        pollingJob?.cancel()
        viewModelScope.launch {
            _effect.send(ActiveRentalEffect.NavigateToReturnPhoto(rentalId))
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}

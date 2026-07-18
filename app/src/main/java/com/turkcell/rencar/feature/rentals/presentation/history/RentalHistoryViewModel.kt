package com.turkcell.rencar.feature.rentals.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.rentals.domain.usecase.GetRentalHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RentalHistoryViewModel @Inject constructor(
    private val getRentalHistoryUseCase: GetRentalHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RentalHistoryState())
    val state = _state.asStateFlow()

    private val _effect = Channel<RentalHistoryEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(RentalHistoryEvent.LoadRentals)
    }

    fun onEvent(event: RentalHistoryEvent) {
        when (event) {
            is RentalHistoryEvent.LoadRentals -> loadRentals()
            is RentalHistoryEvent.RefreshRentals -> loadRentals(isRefreshing = true)
        }
    }

    private fun loadRentals(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = !isRefreshing) }
            getRentalHistoryUseCase()
                .onSuccess { rentalsList ->
                    val totalSpend = rentalsList.sumOf { it.totalPrice ?: 0.0 }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            rentals = rentalsList,
                            totalTripsCount = rentalsList.size,
                            totalSpend = totalSpend,
                            errorMessage = null
                        )
                    }
                    if (isRefreshing) {
                        _effect.send(RentalHistoryEffect.ShowSnackbar("Geçmiş güncellendi"))
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Bir hata oluştu"
                        )
                    }
                    _effect.send(RentalHistoryEffect.ShowSnackbar(error.localizedMessage ?: "Bir hata oluştu"))
                }
        }
    }
}

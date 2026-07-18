package com.turkcell.rencar.feature.rentals.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.rentals.domain.model.RentalPaymentMethod
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import com.turkcell.rencar.feature.wallet.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentSummaryViewModel @Inject constructor(
    private val rentalRepository: RentalRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentSummaryState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PaymentSummaryEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: PaymentSummaryEvent) {
        when (event) {
            is PaymentSummaryEvent.LoadSummary -> loadSummary(event.rentalId)
            is PaymentSummaryEvent.MethodSelected -> {
                _state.update { it.copy(selectedMethod = event.method) }
            }
            is PaymentSummaryEvent.CardSelected -> {
                _state.update { it.copy(selectedCardId = event.cardId) }
            }
            is PaymentSummaryEvent.PayClicked -> pay()
        }
    }

    private fun loadSummary(rentalId: String) {
        if (_state.value.rentalId == rentalId) return
        _state.update { it.copy(rentalId = rentalId, isLoading = true, error = null) }
        viewModelScope.launch {
            rentalRepository.getRental(rentalId)
                .onSuccess { rental -> _state.update { it.copy(isLoading = false, rental = rental) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effect.send(PaymentSummaryEffect.ShowError(error.message ?: "Kiralama bilgisi alınamadı"))
                }

            runCatching { walletRepository.getWalletInfoFlow().first() }
                .onSuccess { info ->
                    _state.update {
                        it.copy(
                            walletBalance = info.balance,
                            cards = info.cards,
                            selectedCardId = info.cards.firstOrNull { card -> card.isDefault }?.id ?: info.cards.firstOrNull()?.id
                        )
                    }
                }
        }
    }

    private fun pay() {
        val rentalId = _state.value.rentalId ?: return
        if (_state.value.isPaying) return
        val method = _state.value.selectedMethod
        if (method == RentalPaymentMethod.CARD && _state.value.selectedCardId == null) {
            viewModelScope.launch {
                _effect.send(PaymentSummaryEffect.ShowError("Lütfen ödeme için bir kart seçin"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isPaying = true) }
            rentalRepository.payRental(
                id = rentalId,
                method = method,
                cardId = if (method == RentalPaymentMethod.CARD) _state.value.selectedCardId else null
            ).onSuccess { receipt ->
                _state.update { it.copy(isPaying = false, receipt = receipt) }
                _effect.send(PaymentSummaryEffect.NavigateHome)
            }.onFailure { error ->
                _state.update { it.copy(isPaying = false) }
                _effect.send(PaymentSummaryEffect.ShowError(error.message ?: "Ödeme alınamadı"))
            }
        }
    }
}

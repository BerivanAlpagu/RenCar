package com.turkcell.rencar.feature.wallet.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.wallet.domain.usecase.AddBalanceUseCase
import com.turkcell.rencar.feature.wallet.domain.usecase.GetWalletInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val getWalletInfoUseCase: GetWalletInfoUseCase,
    private val addBalanceUseCase: AddBalanceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WalletState())
    val state = _state.asStateFlow()

    private val _effect = Channel<WalletEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadWallet()
    }

    fun onEvent(event: WalletEvent) {
        when (event) {
            is WalletEvent.LoadWallet -> loadWallet()
            is WalletEvent.TopUpButtonClicked -> {
                _state.update { it.copy(showAddBalanceSheet = true) }
            }
            is WalletEvent.DismissAddBalanceSheet -> {
                _state.update { it.copy(showAddBalanceSheet = false) }
            }
            is WalletEvent.AddBalanceClicked -> addBalance(event.amount)
        }
    }

    private fun loadWallet() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getWalletInfoUseCase().collect { walletInfo ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        walletInfo = walletInfo,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun addBalance(amount: Double) {
        viewModelScope.launch {
            addBalanceUseCase(amount)
                .onSuccess {
                    _state.update { it.copy(showAddBalanceSheet = false) }
                    _effect.send(WalletEffect.ShowSnackbar("₺$amount başarıyla yüklendi!"))
                }
                .onFailure { error ->
                    _effect.send(WalletEffect.ShowSnackbar(error.localizedMessage ?: "Yükleme başarısız"))
                }
        }
    }
}

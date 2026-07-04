package com.turkcell.rencar.feature.wallet.presentation.wallet

import com.turkcell.rencar.feature.wallet.domain.model.WalletInfo

data class WalletState(
    val isLoading: Boolean = false,
    val walletInfo: WalletInfo? = null,
    val errorMessage: String? = null,
    val showAddBalanceSheet: Boolean = false
)

sealed interface WalletEvent {
    object LoadWallet : WalletEvent
    data class AddBalanceClicked(val amount: Double) : WalletEvent
    object TopUpButtonClicked : WalletEvent
    object DismissAddBalanceSheet : WalletEvent
}

sealed interface WalletEffect {
    data class ShowSnackbar(val message: String) : WalletEffect
}

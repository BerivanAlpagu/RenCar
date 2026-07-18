package com.turkcell.rencar.feature.wallet.presentation.wallet

import com.turkcell.rencar.feature.wallet.domain.model.WalletInfo

data class WalletState(
    val isLoading: Boolean = false,
    val walletInfo: WalletInfo? = null,
    val errorMessage: String? = null,
    val showAddBalanceSheet: Boolean = false,
    val showAddCardSheet: Boolean = false,
    val isAddingCard: Boolean = false
)

sealed interface WalletEvent {
    object LoadWallet : WalletEvent
    data class AddBalanceClicked(val amount: Double) : WalletEvent
    object TopUpButtonClicked : WalletEvent
    object DismissAddBalanceSheet : WalletEvent
    object AddCardButtonClicked : WalletEvent
    object DismissAddCardSheet : WalletEvent
    data class AddCardClicked(
        val brand: String,
        val last4: String,
        val expMonth: Int,
        val expYear: Int
    ) : WalletEvent
}

sealed interface WalletEffect {
    data class ShowSnackbar(val message: String) : WalletEffect
}

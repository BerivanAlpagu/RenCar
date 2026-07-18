package com.turkcell.rencar.feature.rentals.presentation.payment

import com.turkcell.rencar.feature.rentals.domain.model.PaymentReceipt
import com.turkcell.rencar.feature.rentals.domain.model.Rental
import com.turkcell.rencar.feature.rentals.domain.model.RentalPaymentMethod
import com.turkcell.rencar.feature.wallet.domain.model.PaymentCard

data class PaymentSummaryState(
    val rentalId: String? = null,
    val isLoading: Boolean = false,
    val rental: Rental? = null,
    val walletBalance: Double? = null,
    val cards: List<PaymentCard> = emptyList(),
    val selectedMethod: RentalPaymentMethod = RentalPaymentMethod.WALLET,
    val selectedCardId: String? = null,
    val isPaying: Boolean = false,
    val receipt: PaymentReceipt? = null,
    val error: String? = null,
    val showAddBalanceSheet: Boolean = false,
    val isToppingUp: Boolean = false,
    val showAddCardSheet: Boolean = false,
    val isAddingCard: Boolean = false
)

sealed interface PaymentSummaryEvent {
    data class LoadSummary(val rentalId: String) : PaymentSummaryEvent
    data class MethodSelected(val method: RentalPaymentMethod) : PaymentSummaryEvent
    data class CardSelected(val cardId: String) : PaymentSummaryEvent
    data object PayClicked : PaymentSummaryEvent
    data object TopUpButtonClicked : PaymentSummaryEvent
    data object DismissAddBalanceSheet : PaymentSummaryEvent
    data class AddBalanceClicked(val amount: Double) : PaymentSummaryEvent
    data object AddCardButtonClicked : PaymentSummaryEvent
    data object DismissAddCardSheet : PaymentSummaryEvent
    data class AddCardClicked(
        val brand: String,
        val last4: String,
        val expMonth: Int,
        val expYear: Int
    ) : PaymentSummaryEvent
}

sealed interface PaymentSummaryEffect {
    data object NavigateHome : PaymentSummaryEffect
    data class ShowError(val message: String) : PaymentSummaryEffect
    data class ShowMessage(val message: String) : PaymentSummaryEffect
}

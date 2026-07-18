package com.turkcell.rencar.feature.rentals.presentation.reservation

import com.turkcell.rencar.feature.rentals.domain.model.RentalPlan
import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle
import com.turkcell.rencar.feature.vehicles.domain.model.VehicleQuote

data class ReservationConfirmationState(
    val vehicleId: String? = null,
    val isLoading: Boolean = false,
    val vehicle: Vehicle? = null,
    val selectedPlan: RentalPlan = RentalPlan.PER_MINUTE,
    val estimatedMinutes: Int = 30,
    val quote: VehicleQuote? = null,
    val isQuoteLoading: Boolean = false,
    val isTermsAccepted: Boolean = true,
    val isConfirming: Boolean = false,
    val error: String? = null
)

sealed interface ReservationConfirmationEvent {
    data class LoadVehicle(val vehicleId: String) : ReservationConfirmationEvent
    data class PlanSelected(val plan: RentalPlan) : ReservationConfirmationEvent
    data class TermsToggled(val accepted: Boolean) : ReservationConfirmationEvent
    data object ConfirmClicked : ReservationConfirmationEvent
}

sealed interface ReservationConfirmationEffect {
    data class NavigateToHandover(val rentalId: String) : ReservationConfirmationEffect
    data class ShowError(val message: String) : ReservationConfirmationEffect
    data object NavigateBack : ReservationConfirmationEffect
}

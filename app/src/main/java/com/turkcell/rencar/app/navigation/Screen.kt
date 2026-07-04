package com.turkcell.rencar.app.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Register : Screen

    @Serializable
    data class Otp(val phone: String) : Screen

    @Serializable
    data object License : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data class ReservationConfirmation(val vehicleId: String) : Screen

    @Serializable
    data class HandoverPhoto(val vehicleId: String) : Screen

    @Serializable
    data class ActiveRental(val vehicleId: String) : Screen
    
    @Serializable
    data class PaymentSummary(val vehicleId: String) : Screen
}

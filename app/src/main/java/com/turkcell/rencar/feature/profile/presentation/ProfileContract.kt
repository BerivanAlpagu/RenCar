package com.turkcell.rencar.feature.profile.presentation

data class ProfileState(
    val isLoading: Boolean = false,
    val fullName: String = "",
    val phone: String = "",
    val licenseStatus: String = "NOT_SUBMITTED",
    val errorMessage: String? = null
)

sealed interface ProfileEvent {
    data object LoadProfile : ProfileEvent
    data object LogoutClicked : ProfileEvent
}

sealed interface ProfileEffect {
    data class ShowError(val message: String) : ProfileEffect
}

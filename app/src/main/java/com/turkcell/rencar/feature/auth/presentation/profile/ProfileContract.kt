package com.turkcell.rencar.feature.auth.presentation.profile

import com.turkcell.rencar.feature.auth.domain.model.UserProfile
import com.turkcell.rencar.feature.auth.domain.model.UserLicenseStatus

data class ProfileState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val licenseStatus: UserLicenseStatus? = null,
    val errorMessage: String? = null
)

sealed interface ProfileEvent {
    object LoadProfile : ProfileEvent
    object RefreshProfile : ProfileEvent
    object LogoutClicked : ProfileEvent
}

sealed interface ProfileEffect {
    data class ShowSnackbar(val message: String) : ProfileEffect
    object NavigateToOnboarding : ProfileEffect
}

package com.turkcell.rencar.feature.auth.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.auth.data.remote.AuthApi
import com.turkcell.rencar.feature.auth.data.local.TokenManager
import com.turkcell.rencar.feature.auth.domain.usecase.AuthDestination
import com.turkcell.rencar.feature.auth.domain.usecase.ResolvePostAuthDestinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: AuthApi,
    private val resolvePostAuthDestinationUseCase: ResolvePostAuthDestinationUseCase
) : ViewModel() {

    sealed interface SplashState {
        data object Loading : SplashState
        data object NavigateToOnboarding : SplashState
        data object NavigateToLicense : SplashState
        data object NavigateToLicenseApproval : SplashState
        data object NavigateToHome : SplashState
        data class NavigateToPendingPayment(val rentalId: String) : SplashState
    }

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            try {
                val token = tokenManager.accessToken.first()
                if (token.isNullOrBlank()) {
                    _state.value = SplashState.NavigateToOnboarding
                } else {
                    try {
                        authApi.getMe()
                        _state.value = when (val destination = resolvePostAuthDestinationUseCase()) {
                            AuthDestination.Home -> SplashState.NavigateToHome
                            AuthDestination.LicenseApproval -> SplashState.NavigateToLicenseApproval
                            AuthDestination.License -> SplashState.NavigateToLicense
                            is AuthDestination.PendingPayment -> SplashState.NavigateToPendingPayment(destination.rentalId)
                        }
                    } catch (e: retrofit2.HttpException) {
                        if (e.code() == 401) {
                            tokenManager.clearTokens()
                            _state.value = SplashState.NavigateToOnboarding
                        } else {
                            _state.value = SplashState.NavigateToHome
                        }
                    } catch (e: Exception) {
                        _state.value = SplashState.NavigateToHome
                    }
                }
            } catch (e: Exception) {
                _state.value = SplashState.NavigateToOnboarding
            }
        }
    }
}

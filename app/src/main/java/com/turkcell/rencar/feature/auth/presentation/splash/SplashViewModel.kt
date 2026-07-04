package com.turkcell.rencar.feature.auth.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.auth.data.remote.AuthApi
import com.turkcell.rencar.feature.auth.data.remote.LicenseApi
import com.turkcell.rencar.feature.auth.data.local.TokenManager
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
    private val licenseApi: LicenseApi
) : ViewModel() {

    sealed interface SplashState {
        data object Loading : SplashState
        data object NavigateToOnboarding : SplashState
        data object NavigateToLicense : SplashState
        data object NavigateToLicenseApproval : SplashState
        data object NavigateToHome : SplashState
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
                        val licenseStatus = runCatching { licenseApi.getStatus() }.getOrNull()
                        _state.value = when (licenseStatus?.status) {
                            "APPROVED" -> SplashState.NavigateToHome
                            "UNDER_REVIEW" -> SplashState.NavigateToLicenseApproval
                            null, "NOT_SUBMITTED", "REJECTED" -> SplashState.NavigateToLicense
                            else -> SplashState.NavigateToLicense
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

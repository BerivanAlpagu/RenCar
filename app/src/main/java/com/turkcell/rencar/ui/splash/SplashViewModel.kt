package com.turkcell.rencar.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.data.api.AuthApi
import com.turkcell.rencar.data.preferences.TokenManager
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
    private val authApi: AuthApi
) : ViewModel() {

    sealed interface SplashState {
        data object Loading : SplashState
        data object NavigateToOnboarding : SplashState
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
                // Get the saved token from token manager flow
                val token = tokenManager.accessToken.first()
                if (token.isNullOrBlank()) {
                    _state.value = SplashState.NavigateToOnboarding
                } else {
                    // Validate via authApi.getMe
                    authApi.getMe("Bearer $token")
                    _state.value = SplashState.NavigateToHome
                }
            } catch (e: Exception) {
                // Network failure or unauthorized -> clear tokens & onboarding
                tokenManager.clearTokens()
                _state.value = SplashState.NavigateToOnboarding
            }
        }
    }
}

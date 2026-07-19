package com.turkcell.rencar.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.auth.data.local.TokenManager
import com.turkcell.rencar.feature.auth.data.remote.AuthApi
import com.turkcell.rencar.feature.auth.data.remote.LicenseApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val licenseApi: LicenseApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(ProfileEvent.LoadProfile)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.LogoutClicked -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = authApi.getMe()
                val licenseStatus = runCatching { licenseApi.getStatus() }.getOrNull()
                _state.update {
                    it.copy(
                        isLoading = false,
                        fullName = user.fullName,
                        phone = formatDisplayPhone(user.phone?.toString()?.replace("\"", "") ?: ""),
                        licenseStatus = licenseStatus?.status ?: "NOT_SUBMITTED"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Profil yüklenemedi."
                    )
                }
                _effect.send(ProfileEffect.ShowError(e.localizedMessage ?: "Profil yüklenemedi."))
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            tokenManager.clearTokens()
        }
    }

    private fun formatDisplayPhone(rawPhone: String): String {
        val clean = rawPhone.replace("\\D".toRegex(), "")
        return if (clean.length == 12 && clean.startsWith("90")) {
            "+90 ${clean.substring(2, 5)} ${clean.substring(5, 8)} ${clean.substring(8, 10)} ${clean.substring(10, 12)}"
        } else if (clean.length == 10) {
            "+90 ${clean.substring(0, 3)} ${clean.substring(3, 6)} ${clean.substring(6, 8)} ${clean.substring(8, 10)}"
        } else if (clean.length == 11 && clean.startsWith("0")) {
            "+90 ${clean.substring(1, 4)} ${clean.substring(4, 7)} ${clean.substring(7, 9)} ${clean.substring(9, 11)}"
        } else {
            rawPhone
        }
    }
}

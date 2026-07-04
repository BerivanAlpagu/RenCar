package com.turkcell.rencar.feature.auth.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.auth.domain.usecase.GetLicenseStatusUseCase
import com.turkcell.rencar.feature.auth.domain.usecase.GetProfileUseCase
import com.turkcell.rencar.feature.auth.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getLicenseStatusUseCase: GetLicenseStatusUseCase,
    private val logoutUseCase: LogoutUseCase
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
            is ProfileEvent.LoadProfile -> loadProfileData()
            is ProfileEvent.RefreshProfile -> loadProfileData(isRefreshing = true)
            is ProfileEvent.LogoutClicked -> logout()
        }
    }

    private fun loadProfileData(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = !isRefreshing) }
            
            val profileDeferred = async { getProfileUseCase() }
            val licenseDeferred = async { getLicenseStatusUseCase() }

            val profileResult = profileDeferred.await()
            val licenseResult = licenseDeferred.await()

            if (profileResult.isSuccess && licenseResult.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        userProfile = profileResult.getOrNull(),
                        licenseStatus = licenseResult.getOrNull(),
                        errorMessage = null
                    )
                }
                if (isRefreshing) {
                    _effect.send(ProfileEffect.ShowSnackbar("Profil güncellendi"))
                }
            } else {
                val errorMsg = profileResult.exceptionOrNull()?.localizedMessage 
                    ?: licenseResult.exceptionOrNull()?.localizedMessage 
                    ?: "Profil bilgileri yüklenemedi"
                
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
                _effect.send(ProfileEffect.ShowSnackbar(errorMsg))
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
                .onSuccess {
                    _effect.send(ProfileEffect.NavigateToOnboarding)
                }
                .onFailure { error ->
                    _effect.send(ProfileEffect.ShowSnackbar(error.localizedMessage ?: "Çıkış yapılırken hata oluştu"))
                }
        }
    }
}

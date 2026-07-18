package com.turkcell.rencar.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.auth.data.remote.AuthApi
import com.turkcell.rencar.feature.auth.data.remote.LicenseApi
import com.turkcell.rencar.feature.auth.data.remote.UserResponseDto
import com.turkcell.rencar.feature.rentals.data.remote.RentalApi
import com.turkcell.rencar.feature.rentals.data.remote.dto.RentalStatsResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: UserResponseDto? = null,
    val licenseStatus: String = "NOT_SUBMITTED",
    val stats: RentalStatsResponseDto? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val licenseApi: LicenseApi,
    private val rentalApi: RentalApi
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val userDeferred = async { authApi.getMe() }
                val licenseDeferred = async {
                    runCatching { licenseApi.getStatus().status }.getOrDefault("NOT_SUBMITTED")
                }
                val statsDeferred = async {
                    val response = rentalApi.getStats()
                    if (response.isSuccessful) response.body() else null
                }
                Triple(userDeferred.await(), licenseDeferred.await(), statsDeferred.await())
            }.onSuccess { (user, licenseStatus, stats) ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        licenseStatus = licenseStatus,
                        stats = stats
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Profil bilgileri alınamadı."
                    )
                }
            }
        }
    }
}

fun UserResponseDto.displayPhone(): String {
    val raw = (phone as? JsonPrimitive)?.contentOrNull
    return raw?.takeIf { it.isNotBlank() } ?: "Telefon bilgisi yok"
}

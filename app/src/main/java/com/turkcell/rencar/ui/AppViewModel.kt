package com.turkcell.rencar.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface AppEvent {
    data object ShowLogin : AppEvent
    data object ShowRegister : AppEvent
    data class SelectVehicle(val vehicleId: String) : AppEvent
    data class SelectPlan(val plan: RentalPlan) : AppEvent
    data class UpdateOtp(val code: String) : AppEvent
    data class MarkScreen(val route: String) : AppEvent
    data class UpdateLoginEmail(val email: String) : AppEvent
    data class UpdateLoginPassword(val password: String) : AppEvent
    data class UpdateRegisterFullName(val fullName: String) : AppEvent
    data class UpdateRegisterEmail(val email: String) : AppEvent
    data class UpdateRegisterPhone(val phone: String) : AppEvent
    data class UpdateRegisterPassword(val password: String) : AppEvent
}

class RenCarAppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun onEvent(event: AppEvent) {
        when (event) {
            AppEvent.ShowLogin -> _uiState.update { it.copy(authMode = AuthMode.Login) }
            AppEvent.ShowRegister -> _uiState.update { it.copy(authMode = AuthMode.Register) }
            is AppEvent.SelectVehicle -> _uiState.update { current ->
                val selected = current.vehicles.firstOrNull { it.id == event.vehicleId } ?: current.vehicle
                current.copy(selectedVehicleId = selected.id, vehicle = selected)
            }
            is AppEvent.SelectPlan -> _uiState.update { it.copy(selectedPlan = event.plan) }
            is AppEvent.UpdateOtp -> _uiState.update { it.copy(otpCode = event.code.take(6)) }
            is AppEvent.MarkScreen -> _uiState.update { it.copy(activeScreen = event.route) }
            is AppEvent.UpdateLoginEmail -> _uiState.update { it.copy(loginEmail = event.email) }
            is AppEvent.UpdateLoginPassword -> _uiState.update { it.copy(loginPassword = event.password) }
            is AppEvent.UpdateRegisterFullName -> _uiState.update { it.copy(registerFullName = event.fullName) }
            is AppEvent.UpdateRegisterEmail -> _uiState.update { it.copy(registerEmail = event.email) }
            is AppEvent.UpdateRegisterPhone -> _uiState.update { it.copy(registerPhone = event.phone) }
            is AppEvent.UpdateRegisterPassword -> _uiState.update { it.copy(registerPassword = event.password) }
        }
    }
}

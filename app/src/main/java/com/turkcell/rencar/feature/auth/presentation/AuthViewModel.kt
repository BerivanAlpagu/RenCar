package com.turkcell.rencar.feature.auth.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.auth.data.remote.AuthApi
import com.turkcell.rencar.feature.auth.data.remote.UserRegisterDto
import com.turkcell.rencar.feature.auth.data.remote.LoginDto
import com.turkcell.rencar.feature.auth.data.remote.VerifyOtpDto
import com.turkcell.rencar.feature.auth.data.local.TokenManager
import com.turkcell.rencar.feature.auth.domain.usecase.AuthDestination
import com.turkcell.rencar.feature.auth.domain.usecase.ResolvePostAuthDestinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val resolvePostAuthDestinationUseCase: ResolvePostAuthDestinationUseCase
) : ViewModel() {

    sealed interface AuthEvent {
        data class NavigateToOtp(val phone: String) : AuthEvent
        data object NavigateToLicense : AuthEvent
        data object NavigateToLicenseApproval : AuthEvent
        data object NavigateToHome : AuthEvent
        data object NavigateToOnboarding : AuthEvent
        data class ShowError(val message: String) : AuthEvent
    }

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    // Login/Register Screen State
    var phoneInput by mutableStateOf("")
    var emailInput by mutableStateOf("")
    var nameInput by mutableStateOf("")
    var passwordInput by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    // OTP Screen State
    var otpCode by mutableStateOf("")
    var timerSeconds by mutableStateOf(42)
    private var timerJob: Job? = null

    fun startOtpTimer() {
        timerSeconds = 42
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timerSeconds > 0) {
                delay(1000L)
                timerSeconds--
            }
        }
    }

    private fun formatPhone(phone: String): String {
        val cleanPhone = phone.replace(" ", "").replace("-", "")
        return if (cleanPhone.startsWith("+90")) {
            cleanPhone
        } else if (cleanPhone.startsWith("90") && cleanPhone.length == 12) {
            "+$cleanPhone"
        } else if (cleanPhone.startsWith("0")) {
            "+90${cleanPhone.substring(1)}"
        } else {
            "+90$cleanPhone"
        }
    }

    fun login(phone: String) {
        val formattedPhone = formatPhone(phone)
        viewModelScope.launch {
            isLoading = true
            try {
                authApi.login(LoginDto(phone = formattedPhone))
                isLoading = false
                startOtpTimer()
                _events.emit(AuthEvent.NavigateToOtp(formattedPhone))
            } catch (e: Exception) {
                isLoading = false
                val errorMsg = e.localizedMessage ?: "Giriş başarısız. Telefon numarası kayıtlı olmayabilir."
                _events.emit(AuthEvent.ShowError(errorMsg))
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, passwordPlain: String) {
        val formattedPhone = formatPhone(phone)
        viewModelScope.launch {
            isLoading = true
            try {
                authApi.register(
                    UserRegisterDto(
                        fullName = fullName,
                        email = email,
                        phone = formattedPhone,
                        password = passwordPlain
                    )
                )
                authApi.login(LoginDto(phone = formattedPhone))
                isLoading = false
                startOtpTimer()
                _events.emit(AuthEvent.NavigateToOtp(formattedPhone))
            } catch (e: Exception) {
                isLoading = false
                val errorMsg = e.localizedMessage ?: "Kayıt sırasında bir hata oluştu."
                _events.emit(AuthEvent.ShowError(errorMsg))
            }
        }
    }

    fun verifyOtp(phone: String, code: String) {
        val formattedPhone = formatPhone(phone)
        viewModelScope.launch {
            isLoading = true
            try {
                val response = authApi.verifyOtp(VerifyOtpDto(phone = formattedPhone, code = code))
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
                isLoading = false
                val event = when (resolvePostAuthDestinationUseCase()) {
                    AuthDestination.Home -> AuthEvent.NavigateToHome
                    AuthDestination.LicenseApproval -> AuthEvent.NavigateToLicenseApproval
                    AuthDestination.License -> AuthEvent.NavigateToLicense
                }
                _events.emit(event)
            } catch (e: Exception) {
                isLoading = false
                val errorMsg = e.localizedMessage ?: "Doğrulama kodu geçersiz veya süresi dolmuş."
                _events.emit(AuthEvent.ShowError(errorMsg))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearTokens()
            _events.emit(AuthEvent.NavigateToOnboarding)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

package com.turkcell.rencar.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.data.api.AuthApi
import com.turkcell.rencar.data.api.UserRegisterDto
import com.turkcell.rencar.data.api.LoginDto
import com.turkcell.rencar.data.api.VerifyOtpDto
import com.turkcell.rencar.data.preferences.TokenManager
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
    private val tokenManager: TokenManager
) : ViewModel() {

    sealed interface AuthEvent {
        data class NavigateToOtp(val phone: String) : AuthEvent
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

    fun login(phone: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Call login API
                authApi.login(LoginDto(phone = phone))
                isLoading = false
                startOtpTimer()
                _events.emit(AuthEvent.NavigateToOtp(phone))
            } catch (e: Exception) {
                isLoading = false
                val errorMsg = e.localizedMessage ?: "Giriş başarısız. Telefon numarası kayıtlı olmayabilir."
                _events.emit(AuthEvent.ShowError(errorMsg))
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, passwordPlain: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Register the user
                authApi.register(
                    UserRegisterDto(
                        fullName = fullName,
                        email = email,
                        phone = phone,
                        password = passwordPlain
                    )
                )
                // After registration, trigger OTP via login to force phone verification
                authApi.login(LoginDto(phone = phone))
                isLoading = false
                startOtpTimer()
                _events.emit(AuthEvent.NavigateToOtp(phone))
            } catch (e: Exception) {
                isLoading = false
                val errorMsg = e.localizedMessage ?: "Kayıt sırasında bir hata oluştu."
                _events.emit(AuthEvent.ShowError(errorMsg))
            }
        }
    }

    fun verifyOtp(phone: String, code: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = authApi.verifyOtp(VerifyOtpDto(phone = phone, code = code))
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
                isLoading = false
                _events.emit(AuthEvent.NavigateToHome)
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

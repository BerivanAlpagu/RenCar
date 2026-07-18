package com.turkcell.rencar.feature.auth.presentation.license

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.auth.data.local.TokenManager
import com.turkcell.rencar.feature.auth.data.remote.AuthApi
import com.turkcell.rencar.feature.auth.data.remote.LicenseApi
import com.turkcell.rencar.feature.auth.data.remote.RefreshTokenDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LicenseViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val licenseApi: LicenseApi,
    private val authApi: AuthApi
) : ViewModel() {

    private val _status = MutableStateFlow<String>("NOT_SUBMITTED")
    val status = _status.asStateFlow()

    fun refreshStatus() {
        viewModelScope.launch {
            runCatching { licenseApi.getStatus() }
                .onSuccess { _status.value = it.status }
        }
    }

    /**
     * Ehliyet APPROVED olduğunda eldeki access token'ın rolü hâlâ PENDING'dir (JWT'ye rol
     * üretim anında gömülür). Home'a geçmeden önce /auth/refresh ile DB'den güncel rollü
     * (CUSTOMER) yeni bir token çifti alınır — aksi halde Home'daki tüm CUSTOMER uçları 403 döner.
     */
    suspend fun refreshTokenAfterApproval(): Boolean {
        val currentRefreshToken = tokenManager.refreshToken.first() ?: return false
        return runCatching {
            val response = authApi.refresh(RefreshTokenDto(currentRefreshToken))
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
        }.isSuccess
    }

    fun upload(frontFile: File?, backFile: File?, selfieFile: File?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (frontFile == null || backFile == null || selfieFile == null) {
            onError("Lütfen ön yüz, arka yüz ve selfie fotoğraflarının üçünü de ekle.")
            return
        }

        viewModelScope.launch {
            runCatching {
                val front = MultipartBody.Part.createFormData(
                    "front",
                    frontFile.name,
                    frontFile.asRequestBody("image/jpeg".toMediaType())
                )
                val back = MultipartBody.Part.createFormData(
                    "back",
                    backFile.name,
                    backFile.asRequestBody("image/jpeg".toMediaType())
                )
                val selfie = MultipartBody.Part.createFormData(
                    "selfie",
                    selfieFile.name,
                    selfieFile.asRequestBody("image/jpeg".toMediaType())
                )
                licenseApi.upload(front, back, selfie)
            }.onSuccess {
                _status.value = "UNDER_REVIEW"
                onSuccess()
            }.onFailure {
                onError(it.localizedMessage ?: "Ehliyet yüklenemedi.")
            }
        }
    }
}

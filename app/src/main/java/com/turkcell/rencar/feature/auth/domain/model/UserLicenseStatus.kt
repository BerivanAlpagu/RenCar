package com.turkcell.rencar.feature.auth.domain.model

enum class LicenseStatus {
    NOT_SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED
}

data class UserLicenseStatus(
    val status: LicenseStatus,
    val frontImageUrl: String?,
    val backImageUrl: String?,
    val rejectReason: String?,
    val reviewedAt: String?
)

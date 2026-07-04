package com.turkcell.rencar.feature.auth.domain.model

data class UserProfile(
    val id: String,
    val email: String,
    val phone: String,
    val fullName: String,
    val role: String
)

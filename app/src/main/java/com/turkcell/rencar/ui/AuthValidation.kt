package com.turkcell.rencar.ui

data class FieldError(
    val message: String
)

data class LoginValidation(
    val emailError: FieldError? = null,
    val passwordError: FieldError? = null,
    val canSubmit: Boolean = true
)

data class RegisterValidation(
    val fullNameError: FieldError? = null,
    val emailError: FieldError? = null,
    val phoneError: FieldError? = null,
    val passwordError: FieldError? = null,
    val canSubmit: Boolean = true
)

fun validateLogin(email: String, password: String): LoginValidation {
    val emailError = when {
        email.isBlank() -> FieldError("E-posta gerekli.")
        !email.contains("@") -> FieldError("Geçerli bir e-posta gir.")
        else -> null
    }
    val passwordError = when {
        password.isBlank() -> FieldError("Parola gerekli.")
        password.length < 8 -> FieldError("Parola en az 8 karakter olmalı.")
        else -> null
    }
    return LoginValidation(
        emailError = emailError,
        passwordError = passwordError,
        canSubmit = emailError == null && passwordError == null
    )
}

fun validateRegister(
    fullName: String,
    email: String,
    phone: String,
    password: String
): RegisterValidation {
    val fullNameError = if (fullName.trim().length < 3) FieldError("Ad soyad gerekli.") else null
    val emailError = when {
        email.isBlank() -> FieldError("E-posta gerekli.")
        !email.contains("@") -> FieldError("Geçerli bir e-posta gir.")
        else -> null
    }
    val phoneDigits = phone.filter(Char::isDigit)
    val phoneError = when {
        phoneDigits.length < 10 -> FieldError("Telefon numarası eksik.")
        else -> null
    }
    val passwordError = when {
        password.length < 8 -> FieldError("Parola en az 8 karakter olmalı.")
        !password.any(Char::isUpperCase) -> FieldError("En az 1 büyük harf ekle.")
        !password.any(Char::isDigit) -> FieldError("En az 1 rakam ekle.")
        else -> null
    }
    return RegisterValidation(
        fullNameError = fullNameError,
        emailError = emailError,
        phoneError = phoneError,
        passwordError = passwordError,
        canSubmit = fullNameError == null && emailError == null && phoneError == null && passwordError == null
    )
}

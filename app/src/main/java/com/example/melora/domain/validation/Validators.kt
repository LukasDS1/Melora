package com.example.melora.domain.validation

import android.util.Patterns

fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El email es obligatorio."
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (!ok) "Formato de email inválido" else null
}

fun validateNickname(nickname: String): String? {
    if (nickname.isBlank()) return "El nombre de usuario es obligatorio."
    if (nickname.contains(' ')) return "No debe contener espacios."
    val regex = Regex("^[A-Za-z0-9._-]+$")
    if (!regex.matches(nickname)) return "Sólo letras, números y . _ -"
    if (nickname.length !in 3..20) return "Debe tener entre 3 a 20 caracteres"
    return null
}

fun validatePassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria."
    if (pass.length < 8) return "Mínimo 8 caracteres."
    if (!pass.any { it.isUpperCase() }) return "Debe incluir al menos una mayúscula."
    if (!pass.any { it.isLowerCase() }) return "Debe incluir al menos una minúscula."
    if (!pass.any { it.isDigit() }) return "Debe incluir al menos un número."
    if (!pass.any { it.isLetterOrDigit() }) return "Debe incluir al menos un símbolo."
    if (pass.contains(' ')) return "No debe contener espacios."
    return null
}

fun validateConfirmPassword(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirma tu contraseña."
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}

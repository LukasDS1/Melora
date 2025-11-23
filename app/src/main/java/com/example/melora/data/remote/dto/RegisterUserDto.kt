package com.example.melora.data.remote.dto

data class RegisterUserDto(
    val email: String,
    val password: String,
    val rol: RolDto = RolDto(2),
    val nickname: String,
    val profilePhotoBase64: String? = null
)

data class RolDto(
    val idRol: Long
)
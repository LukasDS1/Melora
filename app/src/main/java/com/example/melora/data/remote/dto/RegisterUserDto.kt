package com.example.melora.data.remote.dto

data class RegisterUserDto(
    val email: String,
    val password: String,
    val rolId: Long = 1,
    val nickname: String,
    val profilePhotoBase64: String? = null
)

data class RolDto(
    val idRol: Long
)
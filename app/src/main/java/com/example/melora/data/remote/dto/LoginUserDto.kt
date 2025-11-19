package com.example.melora.data.remote.dto


data class LoginUserDto(
    val email: String,
    val password: String
)

data class LoginResponse(
    val self: String?,
    val data: LoginData?
)

data class LoginData(
    val idUser: Long,
    val email: String,
    val nickname: String,
    val rolId: Long,
    val rolName: String,
    val profilePhotoBase64: String?
)



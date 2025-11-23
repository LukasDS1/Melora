package com.example.melora.data.remote.dto

data class RecoverPassDto(
    val email: String
)

data class RecoverPassResponse(
    val message: String,
    val status: Boolean
)
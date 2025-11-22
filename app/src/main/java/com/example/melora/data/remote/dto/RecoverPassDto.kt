package com.example.melora.data.remote.dto

data class RecoverPassDto(
    val email: String
)

// TODO: adaptar a la respuesta de la api
data class RecoverPassResponse(
    val message: String,
    val status: Boolean
)
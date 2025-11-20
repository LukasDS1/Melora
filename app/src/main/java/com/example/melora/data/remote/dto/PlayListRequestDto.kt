package com.example.melora.data.remote.dto

data class PlayListRequestDto(
    val playlistName: String,
    val userId: Long,
    val categoriaId: Long? = 1L,
    val accesoId: Long? = 1L,
    val songIds: List<Long>? = null
)

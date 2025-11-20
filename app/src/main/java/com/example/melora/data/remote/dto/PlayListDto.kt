package com.example.melora.data.remote.dto

data class PlaylistDto(
    val idPlaylist: Long,
    val playlistName: String,
    val userId: Long,
    // viene como "2025-11-13T19:30:00"
    val fechaCreacion: String?,
)

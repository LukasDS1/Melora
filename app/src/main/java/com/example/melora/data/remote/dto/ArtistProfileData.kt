package com.example.melora.data.remote.dto

data class ArtistProfileData(
    val idUser: Long,
    val nickname: String,
    val email: String,
    val profilePhotoBase64: String?,
    val roleId: Long,
    val songs: List<SongDetailedDto>
)

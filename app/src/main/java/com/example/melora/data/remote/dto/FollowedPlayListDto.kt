package com.example.melora.data.remote.dto

data class FollowedPlaylistDto(
    val idPlayListUser: Long,
    val userId: Long,
    val playlist: PlaylistDto
)

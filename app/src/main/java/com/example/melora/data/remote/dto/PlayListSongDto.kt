package com.example.melora.data.remote.dto

data class PlaylistSongDto(
    val songId: Long,
    val songName: String,
    val coverArt: String?,
    val songDescription: String?,
    val songPath: String?,
    val durationSong: Int,
    val uploadDate: Long,
    val nickname: String,
    val artistId: Long
)

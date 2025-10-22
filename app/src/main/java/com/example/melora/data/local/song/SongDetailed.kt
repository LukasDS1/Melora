package com.example.melora.data.local.song

data class SongDetailed(
    val artistId: Long,
    val songName: String,
    val coverArt: String?,
    val durationSong: Int,
    val uploadDate: Long?,
    val nickname: String
)

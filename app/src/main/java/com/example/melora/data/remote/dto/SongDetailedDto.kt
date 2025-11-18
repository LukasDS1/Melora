package com.example.melora.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SongDetailedDto(
    @SerializedName("idSong")
    val idSong: Long,

    @SerializedName("songName")
    val songName: String,

    @SerializedName("songDescription")
    val songDescription: String?,

    @SerializedName("songDuration")
    val songDuration: Int,

    @SerializedName("coverArt")
    val coverArt: String?,

    @SerializedName("songPathBase64")
    val songPathBase64: String?,

    @SerializedName("creationDate")
    val creationDate: Long,

    @SerializedName("artistId")
    val artistId: Long,

    @SerializedName("nickname")
    val nickname: String?
)



package com.example.melora.data.remote.dto

data class UploadMusicDto(
    val userId: Long,
    val songName:String,
    val songDescription:String?,
    val songPathBase64:String?,
    val coverArt:String?,
    val songDuration: Int,
    val creationDate: Long
)
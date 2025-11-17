package com.example.melora.data.local.song

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val songId: Long = 0L,
    val songName: String,
    val songDescription: String? = null,
    val songPath: String,
    val coverArt: String? = null,
    val durationSong: Int,
    val creationDate: Long =  System.currentTimeMillis(),
)
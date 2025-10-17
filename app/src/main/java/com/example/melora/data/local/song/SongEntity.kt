package com.example.melora.data.local.song

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val songId: Long = 0L,
    val songName: String,
    val songDescription: String?,
    val songPath: String,     //guardamos la url donde esta la cancion
    val coverArt: ByteArray, // despues sql lo convierte en un blob
    val durationSong: Int,
    val creationDate: Date,
)
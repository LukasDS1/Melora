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
    val songDescription: String? = null,
    val songPath: String,      //guardamos la url donde esta la cancion
    val coverArt: String? = null,
    val durationSong: Int   ,
    val creationDate: Long =  System.currentTimeMillis(),
)
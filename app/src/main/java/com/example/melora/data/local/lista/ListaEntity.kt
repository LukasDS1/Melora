package com.example.melora.data.local.lista

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.melora.data.local.playlist.PlaylistEntity
import com.example.melora.data.local.song.SongEntity
@Entity(tableName = "lista",
    foreignKeys = [ForeignKey(
        entity = SongEntity::class,
        parentColumns = ["songId"],
        childColumns = ["idSong"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["idPlaylist"],
        childColumns = ["playListId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["idSong"]), Index(value = ["playListId"])])
data class ListaEntity(
    @PrimaryKey(autoGenerate = true)
    val idLista: Long = 0,
    val idSong:Long,
    val playListId:Long
)
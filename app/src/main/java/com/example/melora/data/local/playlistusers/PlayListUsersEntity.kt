package com.example.melora.data.local.userplaylist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.melora.data.local.playlist.PlaylistEntity
import com.example.melora.data.local.users.UserEntity

@Entity(
    tableName = "playlistusers",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["idUser"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["idPlaylist"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"]), Index(value = ["playlistId"])]
)
data class PlayListUsersEntity(
    @PrimaryKey(autoGenerate = true)
    val idUserPlaylist: Long = 0,
    val userId: Long,
    val playlistId: Long
)

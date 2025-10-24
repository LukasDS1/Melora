package com.example.melora.data.local.favorites

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.local.users.UserEntity
@Entity(tableName = "favorites",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["idUser"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["songId"],
            childColumns = ["idSong"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["idSong"])
    ])
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val favId: Long,
    val favDate: Long = System.currentTimeMillis(),
    val userId: Long,
    val idSong: Long
)

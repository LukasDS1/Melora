package com.example.melora.data.local.upload

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.local.users.UserEntity
import java.sql.Date

@Entity(tableName = "upload",
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
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["idSong"])
    ])

data class UploadEntity(
    @PrimaryKey(autoGenerate = true)
    val uploadId: Long = 0L,
    val userId: Long,
    val idSong: Long,
    val uploadDate: Long? = System.currentTimeMillis(),
    val banReason: String? = null,
    val stateId: Long,
    val banDate: Long? = System.currentTimeMillis()
)
package com.example.melora.data.local.playlist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.melora.data.local.acceso.AccesoEntity
import com.example.melora.data.local.categoria.CategoriaEntity
import com.example.melora.data.local.users.UserEntity

@Entity(tableName = "playlist",
    foreignKeys = [ForeignKey(
        entity = AccesoEntity::class,
        parentColumns = ["idAcceso"],
        childColumns = ["accesoId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(entity = CategoriaEntity::class,
        parentColumns = ["idCat"],
        childColumns = ["catId"],
        onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = UserEntity::class,
            parentColumns = ["idUser"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE)]
, indices = [
        Index(value = ["accesoId"]),
        Index(value = ["catId"]),
        Index(value = ["userId"])
])
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val idPlaylist: Long = 0,
    val playListName: String,
    val creationDate: Long,
    val accesoId:Long,
    val catId:Long,
    val userId: Long

)
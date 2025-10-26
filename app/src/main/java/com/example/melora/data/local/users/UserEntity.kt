package com.example.melora.data.local.users

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.melora.data.local.estado.EstadoEntity
import com.example.melora.data.local.rol.RolEntity

@Entity(tableName = "users",
    foreignKeys = [ForeignKey(
        entity = RolEntity::class,
        parentColumns = ["idRol"],
        childColumns = ["rolId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = EstadoEntity::class,
        parentColumns = ["idEstado"],
        childColumns = ["estadoId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["rolId"]), Index(value = ["estadoId"])])
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val idUser: Long = 0L,
    val nickname: String,
    val email: String,
    val pass: String,
    val profilePicture: String? = null,
    val rolId: Long,
    val estadoId: Long = 1L
)
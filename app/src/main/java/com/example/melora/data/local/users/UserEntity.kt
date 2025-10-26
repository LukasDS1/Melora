package com.example.melora.data.local.users

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.melora.data.local.rol.RolEntity

@Entity(tableName = "users",
    foreignKeys = [ForeignKey(
        entity = RolEntity::class,
        parentColumns = ["idRol"],
        childColumns = ["rolId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["rolId"])])
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val idUser: Long = 0L,
    val nickname: String,
    val email: String,
    val pass: String,
    val profilePicture: String? = null,
    val rolId: Long
)
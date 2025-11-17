package com.example.melora.data.local.rol

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rol")
data class RolEntity(
    @PrimaryKey(autoGenerate = true)
    val idRol: Long = 0,
    val rolName:String)
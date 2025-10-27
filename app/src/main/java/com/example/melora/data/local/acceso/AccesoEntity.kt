package com.example.melora.data.local.acceso

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "acceso")
data class AccesoEntity(
    @PrimaryKey(autoGenerate = true)
    val idAcceso: Long = 0,
    val nombre: String
)
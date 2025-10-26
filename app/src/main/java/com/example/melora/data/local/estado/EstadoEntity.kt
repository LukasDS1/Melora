package com.example.melora.data.local.estado

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "estado")
data class EstadoEntity(
    @PrimaryKey(autoGenerate = true)
    val idEstado:Long = 0,
    val nameEstado:String
)
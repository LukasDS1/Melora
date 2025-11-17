package com.example.melora.data.local.categoria

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoria")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val idCat: Long = 0,
    val catName: String
)
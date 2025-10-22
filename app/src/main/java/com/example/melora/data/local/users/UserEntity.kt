package com.example.melora.data.local.users

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val idUser: Long = 0L,
    val nickname: String,
    val email: String,
    val pass: String,
    val profilePicture: String? = null
)
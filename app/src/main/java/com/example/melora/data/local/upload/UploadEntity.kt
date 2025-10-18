package com.example.melora.data.local.upload

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "upload")
data class UploadEntity(
    @PrimaryKey(autoGenerate = true)
    val uploadId: Long = 0L,
    val userId: Long,
    val songId: Long,
    val uploadDate: Date,
    val BanReason: String? = null,
    val stateId: Long,
    val BanDate: Date? = null
)
package com.example.melora.data.local.upload

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.sql.Date


@Dao
interface UploadDao {
    //insert para el registro de subida
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(upload: UploadEntity): Long
    //Para banear una cancion
    @Query("UPDATE upload SET banReason = :reason, banDate = :banDate WHERE uploadId = :uploadId")
    suspend fun banUpload(uploadId: Long, reason: String, banDate: Long)
    //para buscar canciones baneadas
    @Query("SELECT uploadId FROM upload WHERE banReason IS NOT NULL")
    suspend fun getBannedUploads(): List<Long>

}
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

    @Query("SELECT * FROM upload")
    suspend fun getAllUpload(): List<UploadEntity>

    @Query("UPDATE upload SET banReason = :banReason, banDate = :banDate, stateId = 2 WHERE uploadId = :uploadId")
    suspend fun banUpload(uploadId: Long, banReason: String, banDate: Long)

    @Query("DELETE FROM songs WHERE songId = :songId")
    suspend fun deleteSong(songId: Long)
    //para buscar canciones baneadas
    @Query("SELECT * FROM upload WHERE banReason IS NOT NULL")
    suspend fun getBannedUploads(): List<UploadEntity>

}
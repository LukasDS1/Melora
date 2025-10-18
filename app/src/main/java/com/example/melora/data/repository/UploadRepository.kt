package com.example.melora.data.repository

import com.example.melora.data.local.song.SongDao
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.local.upload.UploadDao
import com.example.melora.data.local.upload.UploadEntity
import java.sql.Date
import kotlin.Long

class UploadRepository (
    private val  uploadDao: UploadDao
) {
    suspend fun insertUpload(userID:Long,songID:Long,stateId: Long): Result<Long>{
        try {
            val uploadEntity = UploadEntity(
                userId = userID,
                songId = songID,
                uploadDate = Date(System.currentTimeMillis()) ,
                stateId = stateId)

            val id = uploadDao.insert(uploadEntity)
            return Result.success(id)

        }catch (e: Exception){
            return Result.failure(e)
        }
    }

    suspend fun bannedUploadIds() = uploadDao.getBannedUploads()

    suspend fun BanUpload(uploadId: Long,banReason : String): Result<Long>{
         try {
            val ban = uploadDao.banUpload(uploadId,banReason, Date(System.currentTimeMillis()))
            return Result.success(uploadId)
        } catch (e: Exception){
            return Result.failure(e)
        }
    }



}
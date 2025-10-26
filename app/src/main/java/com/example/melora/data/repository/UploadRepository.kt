package com.example.melora.data.repository
import com.example.melora.data.local.upload.UploadDao
import com.example.melora.data.local.upload.UploadEntity
import java.util.Date
import kotlin.Long

class UploadRepository (
    private val  uploadDao: UploadDao
) {
    suspend fun insertUpload(userID: Long, songID: Long, stateId: Long): Result<Long> {
        return try {
            val upload = UploadEntity(
                userId = userID,
                idSong = songID,
                stateId = 1L
            )
            val id = uploadDao.insert(upload)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bannedUploadIds() = uploadDao.getBannedUploads()

    suspend fun getAllUploads() = uploadDao.getAllUpload()
    suspend fun banAndDelete(uploadId: Long, songId: Long, banReason: String): Result<Unit> {
        return try {
            uploadDao.banUpload(uploadId, banReason, Date().time)
            uploadDao.deleteSong(songId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




}
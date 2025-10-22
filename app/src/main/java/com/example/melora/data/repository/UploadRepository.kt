package com.example.melora.data.repository
import com.example.melora.data.local.upload.UploadDao
import com.example.melora.data.local.upload.UploadEntity
import java.util.Date
import kotlin.Long

class UploadRepository (
    private val  uploadDao: UploadDao
) {
    suspend fun insertUpload(userID:Long,songID:Long,stateId: Long): Result<Long>{
        try {
            val uploadEntity = UploadEntity(
                userId = userID,
                idSong = songID,
                uploadDate = Date().time ,
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
            val ban = uploadDao.banUpload(uploadId,banReason, Date().time)
            return Result.success(uploadId)
        } catch (e: Exception){
            return Result.failure(e)
        }
    }



}
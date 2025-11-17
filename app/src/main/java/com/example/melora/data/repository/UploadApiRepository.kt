package com.example.melora.data.repository

import com.example.melora.data.remote.UploadApi
import com.example.melora.data.remote.UploadRemoteModule
import com.example.melora.data.remote.dto.UploadMusicDto

class UploadApiRepository (private val api: UploadApi = UploadRemoteModule.api()) {

    suspend fun uploadSong(dto: UploadMusicDto): Result<Any> {
        return try {
            val res = api.uploadSong(dto)

            if (res.isSuccessful) {
                Result.success(res.body() ?: "Upload completed")
            } else {
                Result.failure(Exception("Error ${res.code()}: ${res.message()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
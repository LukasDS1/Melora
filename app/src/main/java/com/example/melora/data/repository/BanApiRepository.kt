package com.example.melora.data.repository

import com.example.melora.data.remote.BanApi
import com.example.melora.data.remote.BanRemoteModule
import com.example.melora.data.remote.dto.BanRequestDto

class BanApiRepository(
    private val api: BanApi = BanRemoteModule.api()
) {

    suspend fun banSong(songId: Long, reason: String): Result<Unit> {
        return try {
            val response = api.banSong(songId, BanRequestDto(reason))

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ban request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
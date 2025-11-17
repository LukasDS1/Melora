package com.example.melora.data.remote

import com.example.melora.data.remote.dto.UploadMusicDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UploadApi {

    @POST("api-v1/uploads/full")
    suspend fun  uploadSong(
        @Body dto: UploadMusicDto): Response<Any>
}
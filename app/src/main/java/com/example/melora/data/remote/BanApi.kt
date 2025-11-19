package com.example.melora.data.remote

import com.example.melora.data.remote.dto.BanRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface BanApi {
    @POST("api-v1/songs/{songId}/ban")
    suspend fun banSong(
        @Path("songId") songId: Long,
        @Body dto: BanRequestDto
    ): Response<Unit>
}
package com.example.melora.data.remote

import com.example.melora.data.remote.dto.SongDetailedDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response

interface SongApi {

    @GET("api-v1/songs/getAll")
    suspend fun getAll(): List<SongDetailedDto>

    @GET("api-v1/songs/{songId}")
    suspend fun getById(@Path("songId") id: Long): SongDetailedDto

    @GET("api-v1/songs/search")
    suspend fun search(@Query("q") query: String): List<SongDetailedDto>

    @GET("api-v1/songs/artist/{artistId}")
    suspend fun getByArtist(@Path("artistId") id: Long): List<SongDetailedDto>



    @PATCH("api-v1/songs/{songId}")
    suspend fun patchSong(
        @Path("songId") id: Long,
        @Body body: Map<String, String?>
    ): Response<Unit>   // el backend devuelve 204 No Content

    // ---- NUEVO: DELETE para borrar canción ----
    @DELETE("api-v1/songs/{songId}")
    suspend fun deleteSong(
        @Path("songId") id: Long
    ): Response<Unit>   // también 204 No Content
}

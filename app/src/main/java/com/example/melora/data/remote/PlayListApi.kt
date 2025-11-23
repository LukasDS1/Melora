package com.example.melora.data.remote

import com.example.melora.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface PlayListApi {

    // ------------------------------
    // 1. CREAR PLAYLIST
    // ------------------------------
    @POST("api-v1/playlists/create")
    suspend fun createPlaylist(
        @Body dto: PlayListRequestDto
    ): Response<PlaylistDto>


    // ------------------------------
    // 2. OBTENER TODAS
    // GET /api-v1/playlists/getAll
    // ------------------------------
    @GET("api-v1/playlists/getAll")
    suspend fun getAllPlaylists(): List<PlaylistDto>


    // ------------------------------
    // 3. PLAYLIST POR ID
    // GET /api-v1/playlists/get/{id}
    // ------------------------------
    @GET("api-v1/playlists/get/{id}")
    suspend fun getPlaylistById(
        @Path("id") id: Long
    ): PlaylistDto


    // ------------------------------
    // 4. CANCIONES DE PLAYLIST
    // GET /api-v1/playlists/get/{id}/songs
    // ------------------------------
    @GET("api-v1/playlist-songs/{playlistId}/songs")
    suspend fun getSongsFromPlaylist(
        @Path("playlistId") playlistId: Long
    ): List<PlaylistSongDto>



    // ------------------------------
    // 5. PLAYLISTS POR USUARIO
    // GET /api-v1/playlists/user/{userId}
    // ------------------------------
    @GET("api-v1/playlists/user/{userId}")
    suspend fun getPlaylistsByUser(
        @Path("userId") userId: Long
    ): List<PlaylistDto>


    // ------------------------------
    // 6. BUSCAR PLAYLIST POR NOMBRE
    // GET /api-v1/playlists/search?name=
    // ------------------------------
    @GET("api-v1/playlists/search")
    suspend fun searchPlaylistsByName(
        @Query("name") name: String
    ): List<PlaylistDto>


    // 7. ACTUALIZAR PLAYLIST
    // PUT /api-v1/playlists/update/{id}
    @PUT("api-v1/playlists/update/{id}")
    suspend fun updatePlaylist(
        @Path("id") id: Long,
        @Body playlist: PlaylistDto
    ): Response<PlaylistDto>


    // ------------------------------
    // 8. ELIMINAR PLAYLIST
    // DELETE /api-v1/playlists/delete/{id}
    // ------------------------------
    @DELETE("api-v1/playlists/delete/{id}")
    suspend fun deletePlaylist(
        @Path("id") id: Long
    ): Response<Unit>


    // ---------------------------------------------------------
    // --------- FOLLOW SYSTEM (si tu microservicio lo usa)
    // ---------------------------------------------------------

    @POST("api-v1/playlists-users/follow/{userId}/{playlistId}")
    suspend fun followPlaylist(
        @Path("userId") userId: Long,
        @Path("playlistId") playlistId: Long
    ): Response<Unit>

    @DELETE("api-v1/playlists-users/unfollow/{userId}/{playlistId}")
    suspend fun unfollowPlaylist(
        @Path("userId") userId: Long,
        @Path("playlistId") playlistId: Long
    ): Response<Unit>

    @POST("api-v1/playlists-users/{playlistId}/toggle-follow/{userId}")
    suspend fun toggleFollow(
        @Path("playlistId") playlistId: Long,
        @Path("userId") userId: Long
    ): Response<String>

    @GET("api-v1/playlists-users/user/{userId}/followed")
    suspend fun getFollowedPlaylists(
        @Path("userId") userId: Long
    ): List<FollowedPlaylistDto>
}

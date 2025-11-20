package com.example.melora.data.remote

import com.example.melora.data.remote.dto.SongDetailedDto
import retrofit2.http.*

interface FavoriteApi {

    @POST("api-v1/favorites/toggle/{userId}/{songId}")
    suspend fun toggleFavorite(
        @Path("userId") userId: Long,
        @Path("songId") songId: Long
    ): Boolean

    @GET("api-v1/favorites/is-favorite/{userId}/{songId}")
    suspend fun isFavorite(
        @Path("userId") userId: Long,
        @Path("songId") songId: Long
    ): Boolean

    @GET("api-v1/favorites/user/{userId}")
    suspend fun getFavorites(
        @Path("userId") userId: Long
    ): List<SongDetailedDto>

    @DELETE("api-v1/favorites/{userId}/{songId}")
    suspend fun deleteFavorite(
        @Path("userId") userId: Long,
        @Path("songId") songId: Long
    )
}

package com.example.melora.data.repository

import com.example.melora.data.remote.FavoriteApi
import com.example.melora.data.remote.dto.SongDetailedDto

class FavoriteApiRepository(
    private val api: FavoriteApi
) {

    suspend fun getByFavorite(userId: Long): List<SongDetailedDto> {
        return api.getFavorites(userId)
    }

    suspend fun isFavorite(userId: Long, songId: Long): Boolean {
        return api.isFavorite(userId, songId)
    }

    suspend fun toggleFavorite(userId: Long, songId: Long): Boolean {
        return api.toggleFavorite(userId, songId)
    }

    suspend fun deleteFavorite(userId: Long, songId: Long) {
        api.deleteFavorite(userId, songId)
    }
}

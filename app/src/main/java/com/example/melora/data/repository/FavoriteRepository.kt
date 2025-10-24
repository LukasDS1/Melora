package com.example.melora.data.repository

import com.example.melora.data.local.favorites.FavoriteDao
import com.example.melora.data.local.favorites.FavoriteEntity
import com.example.melora.data.local.song.SongDao
import com.example.melora.data.local.users.UserDao

class FavoriteRepository(
    private val favoriteDao: FavoriteDao,
    private val userDao: UserDao,
    private val songDao: SongDao
) {
    suspend fun addFavorite(fav: FavoriteEntity) = favoriteDao.insert(fav)

    suspend fun deleteFavorite(userId: Long, songId: Long) = favoriteDao.deleteFav(userId, songId)

    suspend fun getByFavorite(userId: Long) = favoriteDao.getByFavorites(userId)

    suspend fun isFavorite(userId: Long, songId: Long) = favoriteDao.isFavorite(userId, songId)

    suspend fun seleccionarFavorito(userId: Long, songId: Long) {
        val exists = favoriteDao.isFavorite(userId, songId)
        if (exists) {
            favoriteDao.deleteFav(userId, songId)
        } else {
            val fav = FavoriteEntity(
                favId = 0,
                userId = userId,
                idSong = songId
            )
            favoriteDao.insert(fav)
        }
    }
}


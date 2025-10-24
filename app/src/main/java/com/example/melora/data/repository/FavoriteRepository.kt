package com.example.melora.data.repository

import com.example.melora.data.local.favorites.FavoriteDao
import com.example.melora.data.local.favorites.FavoriteEntity

class FavoriteRepository (
    private val favoriteDao: FavoriteDao
){
    suspend fun addFavorite(fav: FavoriteEntity) = favoriteDao.insert(fav)
    suspend fun deleteFavorite(userId:Long,songId:Long) = favoriteDao.deleteFav(userId,songId)
    suspend fun getByFavorite(userId: Long) = favoriteDao.getByFavorites(userId)
    suspend fun isFavorite(userId: Long,songId: Long) = favoriteDao.isFavorite(userId,songId)

    suspend fun seleccionarFav(userId: Long,songId: Long){
        if(favoriteDao.isFavorite(userId,songId)){
            favoriteDao.deleteFav(userId,songId)
        }else{
            favoriteDao.insert(
                FavoriteEntity(
                    favId = 0,
                    userId = userId,
                    idSong = songId
                )
            )
        }
    }


}
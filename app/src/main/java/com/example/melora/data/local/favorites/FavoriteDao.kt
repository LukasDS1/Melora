package com.example.melora.data.local.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND idSong = :idSong")
    suspend fun deleteFav(userId: Long, idSong: Long)

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    suspend fun getByFavorites(userId: Long): List<FavoriteEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND idSong = :songId)")
    suspend fun isFavorite(userId: Long, songId: Long): Boolean






}
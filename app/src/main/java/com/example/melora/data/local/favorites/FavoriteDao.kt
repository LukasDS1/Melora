package com.example.melora.data.local.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.melora.data.local.song.SongDetailed

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND idSong = :idSong")
    suspend fun deleteFav(userId: Long, idSong: Long)

    @Query("SELECT  s.songId, s.songName, s.coverArt, s.durationSong, s.songPath,up.uploadDate, us.nickname, us.idUser AS artistId FROM favorites AS f JOIN  songs AS s ON f.idSong = s.songId JOIN upload AS up ON s.songId = up.idSong JOIN users AS us  ON us.idUser = up.userId WHERE f.userId = :userId")
    suspend fun getByFavorites(userId: Long): List<SongDetailed>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND idSong = :songId)")
    suspend fun isFavorite(userId: Long, songId: Long): Boolean






}
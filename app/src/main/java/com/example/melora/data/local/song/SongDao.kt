package com.example.melora.data.local.song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongDao {

    //insertar canciones
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(songs: SongEntity): Long

    //buscar canciones por nombre
    @Query("SELECT s.songId,s.songName,s.coverArt,s.durationSong,up.uploadDate,us.nickname ,us.idUser AS artistId FROM songs AS s JOIN upload as up ON s.songId = up.idSong JOIN users us ON us.idUser = up.userId WHERE LOWER(s.songName) LIKE '%'|| LOWER(:query) ||'%'")
    suspend fun getSong(query: String): List<SongDetailed>

    //mostrar todas canciones
    @Query("SELECT s.songId,s.songName,s.coverArt,s.durationSong,up.uploadDate,us.nickname,us.idUser AS artistId FROM songs AS s JOIN upload as up ON s.songId = up.idSong JOIN users us ON us.idUser = up.userId")
    suspend fun getAllSong(): List<SongDetailed>

    //obtener todas las canciones de un artista
    @Query("SELECT s.songId,s.songName,s.coverArt,s.durationSong,up.uploadDate,us.nickname,us.idUser AS artistId FROM songs AS s JOIN upload AS up ON s.songId = up.idSong JOIN users AS us ON us.idUser = up.userId WHERE  us.idUser = :id")
    suspend fun getSongsForArtist(id: Long): List<SongDetailed>
    @Query("DELETE FROM songs")
    suspend fun clearSongs()
}
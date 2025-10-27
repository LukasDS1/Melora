package com.example.melora.data.local.song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.melora.data.local.users.UserEntity

@Dao
interface SongDao {

    //insertar canciones
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(songs: SongEntity): Long

    // Conseguir artista de canción
    @Query("SELECT * FROM users WHERE idUser = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun countSongs(): Int

    //buscar canciones por nombre
    @Query("SELECT s.songId,s.songName,s.coverArt,s.durationSong,s.songPath,up.uploadDate,us.nickname ,us.idUser AS artistId FROM songs AS s JOIN upload as up ON s.songId = up.idSong JOIN users us ON us.idUser = up.userId WHERE LOWER(s.songName) LIKE '%'|| LOWER(:query) ||'%'")
    suspend fun getSong(query: String): List<SongDetailed>

    //mostrar todas canciones
    @Query("SELECT s.songId,s.songName,s.coverArt,s.durationSong,s.songPath,up.uploadDate,us.nickname,us.idUser AS artistId FROM songs AS s JOIN upload as up ON s.songId = up.idSong JOIN users us ON us.idUser = up.userId")
    suspend fun getAllSong(): List<SongDetailed>

    //obtener todas las canciones de un artista
    @Query("SELECT s.songId,s.songName,s.coverArt,s.durationSong,s.songPath,up.uploadDate,us.nickname,us.idUser AS artistId FROM songs AS s JOIN upload AS up ON s.songId = up.idSong JOIN users AS us ON us.idUser = up.userId WHERE  us.idUser = :id")
    suspend fun getSongsForArtist(id: Long): List<SongDetailed>

    //reproducir musica
    @Query("SELECT s.songId,s.songName,s.songPath,s.coverArt,s.durationSong,up.uploadDate,us.nickname,us.idUser as artistId FROM songs as s JOIN upload as up ON s.songId  = up.idSong JOIN users AS us ON us.idUser = up.userId WHERE s.songId = :songId")
    suspend fun getSongByID(songId:Long): SongDetailed


    @Query("DELETE FROM songs WHERE songId = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM songs")
    suspend fun clearSongs()
}
package com.example.melora.data.local.song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongDao {

    //insertar canciones
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(song: SongEntity): Long

    //buscar canciones por nombre
    @Query("SELECT * FROM songs WHERE LOWER(songName) LIKE '%' || LOWER(:query) || '%'")
    suspend fun searchSongs(query: String): List<SongEntity>

    //mostrar todas canciones
    @Query("SELECT * FROM songs")
    suspend fun getAllSong(): List<SongEntity>
}
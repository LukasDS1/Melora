package com.example.melora.data.local.lista

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface ListaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lista: ListaEntity): Long

    @Delete
    suspend fun delete(lista: ListaEntity)

    @Query("DELETE FROM lista WHERE playListId = :playlistId AND idSong = :songId")
    suspend fun deleteSongFromPlaylist(playlistId: Long, songId: Long)

    @Query("SELECT * FROM lista WHERE playListId = :playlistId")
    suspend fun getListaByPlaylist(playlistId: Long): List<ListaEntity>
}

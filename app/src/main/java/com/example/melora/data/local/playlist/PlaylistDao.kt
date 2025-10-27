package com.example.melora.data.local.playlist

import androidx.room.*
import com.example.melora.data.local.lista.ListaEntity
import com.example.melora.data.local.song.SongDetailed

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist WHERE idPlaylist = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)


    @Query("SELECT * FROM playlist")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist WHERE userId = :userId")
    suspend fun getPlaylistsByUser(userId: Long): List<PlaylistEntity>

    @Query("SELECT * FROM playlist WHERE idPlaylist = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(link: ListaEntity)

    @Query("DELETE FROM lista WHERE playListId = :playlistId AND idSong = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    @Query("SELECT s.songId, s.songName, s.songPath, s.coverArt, " +
            "s.durationSong, up.uploadDate, us.nickname, " +
            "us.idUser AS artistId FROM lista l JOIN songs s ON l.idSong =" +
            " s.songId JOIN upload up ON s.songId = up.idSong JOIN users us ON us.idUser = up.userId" +
            " WHERE l.playListId = :playlistId")
    suspend fun getSongsFromPlaylist(playlistId: Long): List<SongDetailed>



    @Query("SELECT * FROM playlist WHERE LOWER(playListName) LIKE '%' || LOWER(:query) || '%'")
    suspend fun searchPlaylistsByName(query: String): List<PlaylistEntity>

}



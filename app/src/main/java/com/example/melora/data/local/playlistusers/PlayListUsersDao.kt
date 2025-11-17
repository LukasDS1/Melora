package com.example.melora.data.local.userplaylist

import androidx.room.*
import com.example.melora.data.local.playlist.PlaylistEntity

@Dao
interface PlayListUsersDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUserPlaylist(relation: PlayListUsersEntity): Long

    @Query("DELETE FROM playlistusers WHERE userId = :userId AND playlistId = :playlistId")
    suspend fun removeUserPlaylist(userId: Long, playlistId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM playlistusers WHERE userId = :userId AND playlistId = :playlistId)")
    suspend fun isPlaylistAdded(userId: Long, playlistId: Long): Boolean

    @Query("SELECT p.* FROM playlist p JOIN playlistusers up ON up.playlistId = p.idPlaylist WHERE up.userId = :userId")
    suspend fun getPlaylistsAddedByUser(userId: Long): List<PlaylistEntity>
}

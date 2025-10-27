package com.example.melora.data.repository
import com.example.melora.data.local.playlist.PlaylistEntity
import com.example.melora.data.local.userplaylist.PlayListUsersDao
import com.example.melora.data.local.userplaylist.PlayListUsersEntity

class PlayListUserRepository(private val dao: PlayListUsersDao) {

    suspend fun addPlaylistToUser(userId: Long, playlistId: Long): Result<Unit> {
        return try {
            dao.addUserPlaylist(PlayListUsersEntity(userId = userId, playlistId = playlistId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removePlaylistFromUser(userId: Long, playlistId: Long): Result<Unit> {
        return try {
            dao.removeUserPlaylist(userId, playlistId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isPlaylistAdded(userId: Long, playlistId: Long): Boolean {
        return dao.isPlaylistAdded(userId, playlistId)
    }

    suspend fun getUserPlaylists(userId: Long): List<PlaylistEntity> {
        return dao.getPlaylistsAddedByUser(userId)
    }
}

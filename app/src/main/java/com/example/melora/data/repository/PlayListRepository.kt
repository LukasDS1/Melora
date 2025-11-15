package com.example.melora.data.repository

import com.example.melora.data.local.lista.ListaEntity
import com.example.melora.data.local.playlist.PlaylistDao
import com.example.melora.data.local.playlist.PlaylistEntity
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.userplaylist.PlayListUsersDao
import com.example.melora.data.local.userplaylist.PlayListUsersEntity

class PlayListRepository(
    private val dao: PlaylistDao,
    private val userPlaylistDao: PlayListUsersDao
) {


    suspend fun createPlaylist(
        playListName: String,
        creationDate: Long,
        accesoId: Long,
        catId: Long,
        userId: Long,
        songIds: List<Long>
    ): Result<Long> {
        return try {
            if (playListName.isBlank()) {
                return Result.failure(IllegalArgumentException("Playlist name cannot be empty"))
            }

            val playlist = PlaylistEntity(
                playListName = playListName,
                creationDate = creationDate,
                accesoId = accesoId,
                catId = catId,
                userId = userId
            )

            val playlistId = dao.insertPlaylist(playlist)

            songIds.forEach { songId ->
                dao.addSongToPlaylist(ListaEntity(idSong = songId, playListId = playlistId))
            }

            userPlaylistDao.addUserPlaylist(
                PlayListUsersEntity(userId = userId, playlistId = playlistId)
            )

            Result.success(playlistId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updatePlaylist(playlist: PlaylistEntity): Result<Unit> {
        return try {
            dao.updatePlaylist(playlist)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePlaylist(id: Long): Result<Unit> {
        return try {
            dao.deletePlaylistById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlaylistsByUser(userId: Long): Result<List<PlaylistEntity>> {
        return try {
            val playlists = dao.getPlaylistsByUser(userId)
            Result.success(playlists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSongsFromPlaylist(id: Long): Result<List<SongDetailed>> {
        return try {
            val songs = dao.getSongsFromPlaylist(id)
            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long): Result<Unit> {
        return try {
            dao.addSongToPlaylist(ListaEntity(idSong = songId, playListId = playlistId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long): Result<Unit> {
        return try {
            dao.removeSongFromPlaylist(playlistId, songId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllPlaylist(): List<PlaylistEntity> = dao.getAllPlaylists()


    suspend fun searchPlaylistsByName(query: String): Result<List<PlaylistEntity>> {
        return try {
            val playlists = dao.searchPlaylistsByName(query)
            Result.success(playlists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlaylistById(id: Long): Result<PlaylistEntity?> {
        return try {
            Result.success(dao.getPlaylistById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

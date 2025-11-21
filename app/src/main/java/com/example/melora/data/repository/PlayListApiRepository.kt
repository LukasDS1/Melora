package com.example.melora.data.repository

import com.example.melora.data.remote.PlayListApi
import com.example.melora.data.remote.dto.*

class PlaylistApiRepository(
    private val api: PlayListApi
) {

    suspend fun createPlaylist(
        playlistName: String,
        userId: Long,
        accesoId: Long = 1L,
        categoriaId: Long = 1L,
        songIds: List<Long>
    ): Result<Long> {
        return try {
            val dto = PlayListRequestDto(
                playlistName = playlistName,
                userId = userId,
                categoriaId = categoriaId,
                accesoId = accesoId,
                songIds = if (songIds.isEmpty()) null else songIds
            )

            val response = api.createPlaylist(dto)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body.idPlaylist)
                } else {
                    Result.failure(IllegalStateException("Empty playlist body"))
                }
            } else {
                Result.failure(IllegalStateException("Error ${response.code()} creating playlist"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------
    //
    // Obtiene TODAS las playlists
    // -------------------------------
    suspend fun getAllPlaylists(): List<PlaylistDto> =
        api.getAllPlaylists()



    // -------------------------------
    //     PLAYLIST POR USUARIO
    // -------------------------------
    suspend fun getPlaylistsByUser(userId: Long): List<PlaylistDto> =
        api.getPlaylistsByUser(userId)



    // -------------------------------
    //     CANCIONES EN PLAYLIST
    // -------------------------------
    suspend fun getSongsFromPlaylist(playlistId: Long): List<PlaylistSongDto> =
        api.getSongsFromPlaylist(playlistId)



    // -------------------------------
    //     PLAYLIST POR ID
    // -------------------------------
    suspend fun getPlaylistById(id: Long): PlaylistDto? =
        try {
            api.getPlaylistById(id)
        } catch (e: Exception) {
            null
        }



    // -------------------------------
    //     BUSCAR PLAYLIST
    // -------------------------------
    suspend fun searchPlaylistsByName(query: String): List<PlaylistDto> =
        api.searchPlaylistsByName(query)



    // -------------------------------
    //     PLAYLISTS SEGUIDAS
    // -------------------------------
    suspend fun getFollowedPlaylists(userId: Long): List<PlaylistDto> {
        val followed = api.getFollowedPlaylists(userId)

        return followed
            .mapNotNull { it.playlist }         // evita playlist = null
            .filter { it.idPlaylist != null }   // evita playlist.id null
    }



    // -------------------------------
    //     FOLLOW SYSTEM
    // -------------------------------
    suspend fun toggleFollow(userId: Long, playlistId: Long) {
        try {
            api.toggleFollow(playlistId, userId)
        } catch (_: Exception) { }
    }

    suspend fun followPlaylist(userId: Long, playlistId: Long) {
        try {
            api.followPlaylist(userId, playlistId)
        } catch (_: Exception) { }
    }

    suspend fun unfollowPlaylist(userId: Long, playlistId: Long) {
        try {
            api.unfollowPlaylist(userId, playlistId)
        } catch (_: Exception) { }
    }
}

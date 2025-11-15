package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.playlist.PlaylistEntity
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.repository.PlayListRepository
import com.example.melora.data.repository.PlayListUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistRepo: PlayListRepository,
    private val userPlaylistRepo: PlayListUserRepository
) : ViewModel() {

    private val _myPlaylists = MutableStateFlow<List<PlaylistEntity>>(emptyList())
    val myPlaylists: StateFlow<List<PlaylistEntity>> = _myPlaylists

    private val _followedPlaylists = MutableStateFlow<List<PlaylistEntity>>(emptyList())
    val followedPlaylists: StateFlow<List<PlaylistEntity>> = _followedPlaylists

    private val _songsInPlaylist = MutableStateFlow<List<SongDetailed>>(emptyList())
    val songsInPlaylist: StateFlow<List<SongDetailed>> = _songsInPlaylist

    // 🔹 Playlist actual (para cuando se abre una ajena)
    private val _currentPlaylist = MutableStateFlow<PlaylistEntity?>(null)
    val currentPlaylist: StateFlow<PlaylistEntity?> = _currentPlaylist

    fun loadMyPlaylists(userId: Long) {
        viewModelScope.launch {
            playlistRepo.getPlaylistsByUser(userId)
                .onSuccess { _myPlaylists.value = it }
        }
    }

    fun loadFollowedPlaylists(userId: Long) {
        viewModelScope.launch {
            _followedPlaylists.value = userPlaylistRepo.getUserPlaylists(userId)
        }
    }

    fun loadSongsFromPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepo.getSongsFromPlaylist(playlistId)
                .onSuccess { _songsInPlaylist.value = it }
        }
    }

    fun loadPlaylistById(playlistId: Long) {
        viewModelScope.launch {
            playlistRepo.getPlaylistById(playlistId)
                .onSuccess { _currentPlaylist.value = it }
                .onFailure { it.printStackTrace() }
        }
    }

    fun createPlaylist(
        playListName: String,
        accesoId: Long,
        catId: Long,
        userId: Long,
        songIds: List<Long>,
        onSuccess: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = playlistRepo.createPlaylist(
                playListName = playListName,
                creationDate = System.currentTimeMillis(),
                accesoId = accesoId,
                catId = catId,
                userId = userId,
                songIds = songIds
            )

            result.onSuccess { newId ->
                loadMyPlaylists(userId)
                loadFollowedPlaylists(userId)
                onSuccess(newId)
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    fun deletePlaylist(playlistId: Long, userId: Long) {
        viewModelScope.launch {
            playlistRepo.deletePlaylist(playlistId)
            loadMyPlaylists(userId)
        }
    }

    fun addSong(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepo.addSongToPlaylist(playlistId, songId)
            loadSongsFromPlaylist(playlistId)
        }
    }

    fun removeSong(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepo.removeSongFromPlaylist(playlistId, songId)
            loadSongsFromPlaylist(playlistId)
        }
    }


    fun followPlaylist(userId: Long, playlistId: Long) {
        viewModelScope.launch {
            userPlaylistRepo.addPlaylistToUser(userId, playlistId)
            loadFollowedPlaylists(userId)
        }
    }

    fun unfollowPlaylist(userId: Long, playlistId: Long) {
        viewModelScope.launch {
            userPlaylistRepo.removePlaylistFromUser(userId, playlistId)
            loadFollowedPlaylists(userId)
        }
    }

    fun toggleFollow(userId: Long, playlistId: Long) {
        viewModelScope.launch {
            val isFollowing = userPlaylistRepo.isPlaylistAdded(userId, playlistId)
            if (isFollowing) {
                userPlaylistRepo.removePlaylistFromUser(userId, playlistId)
            } else {
                userPlaylistRepo.addPlaylistToUser(userId, playlistId)
            }
            loadFollowedPlaylists(userId)
        }
    }
}

package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.dto.PlaylistDto
import com.example.melora.data.remote.dto.PlaylistSongDto
import com.example.melora.data.repository.PlaylistApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistApiViewModel(
    private val repo: PlaylistApiRepository
) : ViewModel() {

    private val _myPlaylists = MutableStateFlow<List<PlaylistDto>>(emptyList())
    val myPlaylists: StateFlow<List<PlaylistDto>> = _myPlaylists

    private val _followedPlaylists = MutableStateFlow<List<PlaylistDto>>(emptyList())
    val followedPlaylists: StateFlow<List<PlaylistDto>> = _followedPlaylists

    private val _songsInPlaylist = MutableStateFlow<List<PlaylistSongDto>>(emptyList())
    val songsInPlaylist: StateFlow<List<PlaylistSongDto>> = _songsInPlaylist

    private val _currentPlaylist = MutableStateFlow<PlaylistDto?>(null)
    val currentPlaylist: StateFlow<PlaylistDto?> = _currentPlaylist

    fun loadMyPlaylists(userId: Long) {
        viewModelScope.launch {
            try {
                _myPlaylists.value = repo.getPlaylistsByUser(userId)
            } catch (_: Exception) { }
        }
    }

    fun loadFollowedPlaylists(userId: Long) {
        viewModelScope.launch {
            try {
                _followedPlaylists.value = repo.getFollowedPlaylists(userId)
            } catch (_: Exception) { }
        }
    }

    fun loadSongsFromPlaylist(playlistId: Long) {
        viewModelScope.launch {
            try {
                _songsInPlaylist.value = repo.getSongsFromPlaylist(playlistId)
            } catch (_: Exception) { }
        }
    }

    fun loadPlaylistById(playlistId: Long) {
        viewModelScope.launch {
            val pl = repo.getPlaylistById(playlistId)
            _currentPlaylist.value = pl
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
            val result = repo.createPlaylist(
                playlistName = playListName,
                accesoId = accesoId,
                categoriaId = catId,
                userId = userId,
                songIds = songIds
            )
            result.onSuccess { newId ->
                loadMyPlaylists(userId)
                loadFollowedPlaylists(userId)
                onSuccess(newId)
            }.onFailure { it.printStackTrace() }
        }
    }

    fun toggleFollow(userId: Long, playlistId: Long) {
        viewModelScope.launch {
            repo.toggleFollow(userId, playlistId)
            loadFollowedPlaylists(userId)
        }
    }
}

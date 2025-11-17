package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.playlist.PlaylistEntity
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.users.UserEntity
import com.example.melora.data.repository.PlayListRepository
import com.example.melora.data.repository.SongRepository
import com.example.melora.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel (private val repository: SongRepository, private val userRepository: UserRepository,private val playlistRepository: PlayListRepository): ViewModel() {
    private val _songs = MutableStateFlow<List<SongDetailed>>(emptyList())
    val songs: StateFlow<List<SongDetailed>> = _songs.asStateFlow()

    private val _artists = MutableStateFlow<List<UserEntity>>(emptyList())
    val artists: StateFlow<List<UserEntity>> = _artists.asStateFlow()

    private val _playlists = MutableStateFlow<List<PlaylistEntity>>(emptyList())
    val playlists: StateFlow<List<PlaylistEntity>> = _playlists.asStateFlow()

    fun loadAllSongs() {
        viewModelScope.launch {
            _songs.value = repository.getAllSongs()
            _playlists.value = playlistRepository.getAllPlaylist()
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _songs.value = repository.getAllSongs()
                _artists.value = emptyList()
                _playlists.value = playlistRepository.getAllPlaylist()
            } else {
                val songsResult = repository.getSong(query)
                val artistsResult = userRepository.searchByNickname(query)
                val playlistsResult = playlistRepository.searchPlaylistsByName(query)

                _playlists.value = playlistsResult.getOrElse { emptyList() }
                _songs.value = songsResult.getOrElse { emptyList() }
                _artists.value = artistsResult.getOrElse { emptyList() }
            }
        }
    }
}
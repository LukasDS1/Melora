package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.dto.ArtistProfileData
import com.example.melora.data.remote.dto.PlaylistDto
import com.example.melora.data.remote.dto.SongDetailedDto
import com.example.melora.data.repository.PlaylistApiRepository
import com.example.melora.data.repository.RegisterApiRepository
import com.example.melora.data.repository.SongApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val songApiRepository: SongApiRepository,
    private val playlistRepository: PlaylistApiRepository,
    private val registerApiRepository: RegisterApiRepository
) : ViewModel() {

    private val _songs = MutableStateFlow<List<SongDetailedDto>>(emptyList())
    val songs: StateFlow<List<SongDetailedDto>> = _songs

    private val _artists = MutableStateFlow<List<ArtistProfileData>>(emptyList())
    val artists: StateFlow<List<ArtistProfileData>> = _artists



    private val _playlists = MutableStateFlow<List<PlaylistDto>>(emptyList())
    val playlists: StateFlow<List<PlaylistDto>> = _playlists

    fun loadAll() {
        viewModelScope.launch {
            _songs.value = songApiRepository.getAllSongs().getOrElse { emptyList() }
            _playlists.value = try {
                playlistRepository.getAllPlaylists()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {

            if (query.isBlank()) {
                loadAll()
                _artists.value = emptyList()
                return@launch
            }

            val songResult = songApiRepository.search(query)
            val artistsResult = registerApiRepository.searchByNickname(query)
            val playlistResult = playlistRepository.searchPlaylistsByName(query)

            _songs.value = songResult.getOrElse { emptyList() }
            _artists.value = artistsResult.getOrElse { emptyList() }
            _playlists.value = playlistResult
        }
    }
}

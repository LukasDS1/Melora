package com.example.melora.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.local.users.UserEntity
import com.example.melora.data.repository.SongRepository
import com.example.melora.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel (private val repository: SongRepository, private val userRepository: UserRepository): ViewModel() {
    private val _songs = MutableStateFlow<List<SongDetailed>>(emptyList())
    val songs: StateFlow<List<SongDetailed>> = _songs.asStateFlow()

    private val _artists = MutableStateFlow<List<UserEntity>>(emptyList())
    val artists: StateFlow<List<UserEntity>> = _artists.asStateFlow()

    fun loadAllSongs() {
        viewModelScope.launch {
            _songs.value = repository.getAllSongs()
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _songs.value = repository.getAllSongs()
                _artists.value = emptyList()
            } else {
                val songsResult = repository.getSong(query)
                val artistsResult = userRepository.searchByNickname(query)

                _songs.value = songsResult.getOrElse { emptyList() }
                _artists.value = artistsResult.getOrElse { emptyList() }
            }
        }
    }
}
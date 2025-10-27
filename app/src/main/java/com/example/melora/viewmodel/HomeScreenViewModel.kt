package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val songRepository: SongRepository) : ViewModel() {

    private val _songs = MutableStateFlow<List<SongDetailed>>(emptyList())
    val songs: StateFlow<List<SongDetailed>> = _songs

    fun loadSongs() {
        viewModelScope.launch {
            _songs.value = songRepository.getAllSongs()
        }
    }
}

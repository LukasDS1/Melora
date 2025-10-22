package com.example.melora.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel (private val repository: SongRepository): ViewModel(){
    private val _songs = MutableStateFlow<List<SongDetailed>>(emptyList())
    val songs: StateFlow<List<SongDetailed>> = _songs.asStateFlow()

    fun loadAllSongs() {
        viewModelScope.launch {
            _songs.value = repository.getAllSongs()
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _songs.value = if (query.isBlank()) {
                repository.getAllSongs()
            } else {
                val result = repository.getSong(query)
                result.getOrElse { emptyList() }
            }
        }
    }
}
package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.dto.SongDetailedDto
import com.example.melora.data.repository.SongApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenApiViewModel(
    private val repository: SongApiRepository
) : ViewModel() {

    private val _songs = MutableStateFlow<List<SongDetailedDto>>(emptyList())
    val songs: StateFlow<List<SongDetailedDto>> = _songs

    fun loadSongs() {
        viewModelScope.launch {
            val res = repository.getAllSongs()
            _songs.value = res.getOrElse { emptyList() }
        }
    }
}

package com.example.melora.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.favorites.FavoriteEntity
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: FavoriteRepository) : ViewModel() {

    private val _favorites = MutableStateFlow<List<SongDetailed>>(emptyList())
    val favorites: StateFlow<List<SongDetailed>> = _favorites

    fun loadFavorite(userId: Long) {
        viewModelScope.launch {
            _favorites.value = repo.getByFavorite(userId)
        }
    }

    suspend fun isFavorite(userId: Long, songId: Long): Boolean {
        return repo.isFavorite(userId, songId)
    }

    fun seleccionarFavorito(userId: Long, songId: Long) {
        viewModelScope.launch {
            repo.seleccionarFavorito(userId, songId)
            _favorites.value = repo.getByFavorite(userId)
        }
    }
}

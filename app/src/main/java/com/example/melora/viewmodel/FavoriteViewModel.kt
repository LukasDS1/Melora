package com.example.melora.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.favorites.FavoriteEntity
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.repository.FavoriteRepository
import com.example.melora.data.storage.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: FavoriteRepository,
                        private val prefs: UserPreferences) : ViewModel() {

    private val _favorites = MutableStateFlow<List<SongDetailed>>(emptyList())
    val favorites: StateFlow<List<SongDetailed>> = _favorites

    private val _currentIsFavorite = MutableStateFlow(false)
    val currentIsFavorite: StateFlow<Boolean> = _currentIsFavorite


    fun loadFavorite() {
        viewModelScope.launch {
            val userId = prefs.userId.firstOrNull() ?: return@launch
            _favorites.value = repo.getByFavorite(userId)
        }
    }

    fun updateFavoriteState(songId: Long) {
        viewModelScope.launch {
            val userId = prefs.userId.firstOrNull() ?: return@launch
            _currentIsFavorite.value = repo.isFavorite(userId, songId)
        }
    }

    fun checkIfFavorite(songId: Long) {
        viewModelScope.launch {
            val userId = prefs.userId.firstOrNull() ?: return@launch
            _currentIsFavorite.value = repo.isFavorite(userId, songId)
        }
    }
    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            val userId = prefs.userId.firstOrNull() ?: return@launch
            repo.seleccionarFavorito(userId, songId)
            _currentIsFavorite.value = repo.isFavorite(userId, songId)
            _favorites.value = repo.getByFavorite(userId)
        }
    }
}

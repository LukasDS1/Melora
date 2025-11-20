package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.dto.SongDetailedDto
import com.example.melora.data.repository.FavoriteApiRepository
import com.example.melora.data.repository.FavoriteRepository
import com.example.melora.data.storage.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FavoriteApiViewModel(
    private val repo: FavoriteApiRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<SongDetailedDto>>(emptyList())
    val favorites: StateFlow<List<SongDetailedDto>> = _favorites

    private val _currentIsFavorite = MutableStateFlow(false)
    val currentIsFavorite: StateFlow<Boolean> = _currentIsFavorite


    //  carga la pantalla de favoritos
    fun loadFavorite() {
        viewModelScope.launch {
            val userId = prefs.userId.firstOrNull() ?: return@launch
            _favorites.value = repo.getByFavorite(userId)
        }
    }

    //  verifica si una canción es favorita (Player / SongDetail)
    fun checkIfFavorite(songId: Long) {
        viewModelScope.launch {
            val userId = prefs.userId.firstOrNull() ?: return@launch
            _currentIsFavorite.value = repo.isFavorite(userId, songId)
        }
    }

    //  toggle favorito desde el botón (corazón)
    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            val userId = prefs.userId.firstOrNull() ?: return@launch
            val result = repo.toggleFavorite(userId, songId)

            _currentIsFavorite.value = result  // true o false

            // actualizar lista
            _favorites.value = repo.getByFavorite(userId)
        }
    }
}

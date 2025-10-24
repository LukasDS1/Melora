package com.example.melora.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.favorites.FavoriteEntity
import com.example.melora.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel (private val repository: FavoriteRepository): ViewModel(){

    private val _favorite = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val favorite : StateFlow<List<FavoriteEntity>> = _favorite

    fun loadFavorite(userId:Long){
        viewModelScope.launch {
            _favorite.value = repository.getByFavorite(userId)
        }
    }

    fun seleccionarFavorito(userId: Long,songId: Long){
        viewModelScope.launch {
            repository.seleccionarFav(userId,songId)
            _favorite.value = repository.getByFavorite(userId)
        }
    }

    suspend fun isFavorite(userId: Long,songId: Long): Boolean{
        return  repository.isFavorite(userId,songId)
    }

}
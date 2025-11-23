package com.example.melora.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.dto.ArtistProfileData
import com.example.melora.data.repository.UserArtistApiPublicRepository
import kotlinx.coroutines.launch

class UserArtistApiPublicViewModel(
    private val repo: UserArtistApiPublicRepository
) : ViewModel() {

    // STATE CORRECTO
    var artistData by mutableStateOf<ArtistProfileData?>(null)
        private set

    fun loadArtistById(id: Long) {
        viewModelScope.launch {
            try {
                artistData = repo.getArtistProfile(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

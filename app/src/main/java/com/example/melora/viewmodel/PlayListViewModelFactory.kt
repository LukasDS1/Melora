package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.repository.PlaylistApiRepository

class PlaylistApiViewModelFactory(
    private val repo: PlaylistApiRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistApiViewModel::class.java)) {
            return PlaylistApiViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


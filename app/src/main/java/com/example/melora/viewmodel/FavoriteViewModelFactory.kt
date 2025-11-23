package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.repository.FavoriteApiRepository
import com.example.melora.data.storage.UserPreferences

class FavoriteViewModelFactory(private val repository: FavoriteApiRepository, private val prefs : UserPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(FavoriteApiViewModel::class.java)) {
            return FavoriteApiViewModel(repository,prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.melora.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.repository.PlayListRepository
import com.example.melora.data.repository.PlayListUserRepository


class PlaylistViewModelFactory(private val repository: PlayListRepository,private val userPlaylistRepo: PlayListUserRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
            return PlaylistViewModel(repository,userPlaylistRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

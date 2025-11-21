package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.data.repository.SongApiRepository
import com.example.melora.data.repository.SongRepository


class ArtistProfileViewModelFactory(
    private val repository: ArtistRepository,
    private val SongApi: SongApiRepository
): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtistProfileViewModel::class.java)){
            return ArtistProfileViewModel(repository, SongApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
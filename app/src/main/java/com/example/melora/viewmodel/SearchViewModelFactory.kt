package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.repository.PlayListRepository
import com.example.melora.data.repository.RegisterApiRepository
import com.example.melora.data.repository.SongApiRepository
import com.example.melora.data.repository.SongRepository
import com.example.melora.data.repository.UserRepository

class SearchViewModelFactory(private val repository: SongApiRepository,private val PlayListRepo: PlayListRepository
,private val registerApiRepository: RegisterApiRepository ): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchViewModel::class.java)){
            return SearchViewModel(repository,PlayListRepo,registerApiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.repository.PlayListRepository
import com.example.melora.data.repository.SongRepository
import com.example.melora.data.repository.UserRepository

class SearchViewModelFactory(private val repository: SongRepository, private val userRepository: UserRepository,private val PlayListRepo: PlayListRepository): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchViewModel::class.java)){
            return SearchViewModel(repository,userRepository,PlayListRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
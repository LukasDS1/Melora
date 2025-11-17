package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.repository.LoginApiRepository
import com.example.melora.data.storage.UserPreferences

class LoginApiViewModelFactory(
    private val repository: LoginApiRepository,
    private val prefs: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginApiViewModel::class.java)) {
            return LoginApiViewModel(repository, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

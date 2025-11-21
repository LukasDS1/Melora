package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.remote.LoginApi
import com.example.melora.data.remote.RegisterApi
import com.example.melora.data.storage.UserPreferences

class EditProfileViewModelFactory(
    private val registerApi: RegisterApi,
    private val loginApi: LoginApi,
    private val prefs: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(registerApi, loginApi, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


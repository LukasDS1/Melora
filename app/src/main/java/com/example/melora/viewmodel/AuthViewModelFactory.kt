package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.repository.UserRepository

class AuthViewModelFactory (
    private val repository: UserRepository,
    private val app: Application
) : ViewModelProvider.Factory { // Implementa la interfaz factory de ViewModelProvider

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si solicitan AuthViewModel, se crea con la dependencia repository y se fuerza el casteo a T.
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository,app) as T
        }
        // Excepción por si no sabe crear la clase solicitada.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
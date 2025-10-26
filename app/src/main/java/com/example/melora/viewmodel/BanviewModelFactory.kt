package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melora.data.repository.UploadRepository

class BanviewModelFactory(private val repo: UploadRepository, private val app: Application):
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BanViewModel::class.java)) {
            return BanViewModel(app,repo) as T
        }
        // Excepción por si no sabe crear la clase solicitada.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
    }

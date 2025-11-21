package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.repository.BanApiRepository
import kotlinx.coroutines.launch


class BanViewModel(
    app: Application,
    private val repo: BanApiRepository
) : AndroidViewModel(app) {

    fun banSong(songId: Long, reason: String) {
        viewModelScope.launch {
            repo.banSong(songId, reason)
        }
    }
}
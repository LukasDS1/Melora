package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.repository.UploadRepository
import kotlinx.coroutines.launch


class BanViewModel(
    app: Application,
    private val repo: UploadRepository
) : AndroidViewModel(app) {

    fun banSong(songId: Long, reason: String) {
        viewModelScope.launch {
            val uploads = repo.getAllUploads()
            val upload = uploads.firstOrNull { it.idSong == songId }
            upload?.let {
                repo.banAndDelete(it.uploadId, songId, reason)
            }
        }
    }
}
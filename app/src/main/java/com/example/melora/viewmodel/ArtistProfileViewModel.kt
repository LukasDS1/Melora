package com.example.melora.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.dto.ArtistProfileData
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.data.repository.SongApiRepository
import kotlinx.coroutines.launch

class ArtistProfileViewModel(
    private val repository: ArtistRepository,
    private val songRepository: SongApiRepository
) : ViewModel() {

    var artistData by mutableStateOf<ArtistProfileData?>(null)
        private set

    // ==========================
    // CARGAR PERFIL DEL USUARIO LOGGEADO
    // ==========================
    fun loadMyProfile() {
        viewModelScope.launch {
            try {
                artistData = repository.getMyProfile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==========================
    // ELIMINAR CANCIÓN
    // ==========================
    fun deleteSong(songId: Long) {
        viewModelScope.launch {
            val result = songRepository.deleteSong(songId)

            result.onSuccess {
                // refrescar el perfil
                loadMyProfile()
            }

            result.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    // ==========================
    // EDITAR NOMBRE / DESCRIPCIÓN
    // ==========================
    fun updateSongDetails(songId: Long, newName: String?, newDesc: String?) {
        viewModelScope.launch {
            val result = songRepository.changeSongDetails(
                id = songId,
                name = newName,
                desc = newDesc
            )

            result.onSuccess {
                // refrescar el perfil
                loadMyProfile()
            }

            result.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    fun banUser(userId: Long, onFinish: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteUser(userId)
            onFinish(result.isSuccess)
        }
    }



}


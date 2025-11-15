package com.example.melora.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.repository.ArtistProfilData
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.data.repository.SongRepository
import kotlinx.coroutines.launch

class ArtistProfileViewModel(private val repository: ArtistRepository,private val songRepository: SongRepository): ViewModel(){

    var artistData by mutableStateOf<ArtistProfilData?>(null)

    fun loadArtist(artistId: Long){
        viewModelScope.launch {
            artistData = repository.getSongByArtist(artistId)
        }
    }

    fun deleteSong(songId: Long) {
        viewModelScope.launch {
            try {
                songRepository.deleteSong(songId)
                artistData?.artist?.idUser?.let { loadArtist(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateSongDetails(songId: Long, newName: String?, newDesc: String?) {
        viewModelScope.launch {
            val result = songRepository.changeSongDetails(songId, newName, newDesc)
            result.onSuccess {
                artistData?.artist?.idUser?.let { loadArtist(it) }
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    fun banUser(userId: Long) {
        viewModelScope.launch {
            val result = repository.banUser(userId)
            result.onSuccess {
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }


}
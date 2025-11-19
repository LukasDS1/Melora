package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.player.MusicPlayerManager
import com.example.melora.data.remote.dto.SongDetailedDto
import com.example.melora.data.repository.SongApiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicPlayerViewModel(
    app: Application,
    private val apiRepository: SongApiRepository
) : AndroidViewModel(app) {

    private val playerManager = MusicPlayerManager(app)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentSong = MutableStateFlow<SongDetailedDto?>(null)
    val currentSong: StateFlow<SongDetailedDto?> = _currentSong.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = playerManager.getCurrentPosition()
                _duration.value = playerManager.getDuration()
                delay(500)
            }
        }
    }

    fun playSong(songId: Long) {
        viewModelScope.launch {
            val result = apiRepository.getSongById(songId)

            if (result.isSuccess) {
                val song = result.getOrNull() ?: return@launch

                _currentSong.value = song

                val audio = song.audioBase64
                if (audio.isNullOrBlank()) {
                    _isPlaying.value = false
                    return@launch
                }

                playerManager.playBase64Audio(audio, song.idSong)
                _isPlaying.value = true

            } else {
                _isPlaying.value = false
            }
        }
    }

    fun getSongDetails(songId: Long) {
        viewModelScope.launch {
            val result = apiRepository.getSongById(songId)
            _currentSong.value = result.getOrNull()
        }
    }

    fun play() {
        playerManager.resume()
        _isPlaying.value = true
    }

    fun pause() {
        playerManager.pause()
        _isPlaying.value = false
    }

    fun stop() {
        playerManager.stop()
        _isPlaying.value = false
    }

    fun seekTo(position: Long) {
        playerManager.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}

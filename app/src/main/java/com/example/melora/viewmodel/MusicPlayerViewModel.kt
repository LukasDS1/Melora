package com.example.melora.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.player.MusicPlayerManager
import com.example.melora.data.repository.SongRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicPlayerViewModel(
    app: Application,
    private val songRepository: SongRepository
): AndroidViewModel(app) {

    private val playerManager = MusicPlayerManager(app)
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _current_song = MutableStateFlow<SongDetailed?>(null)
    val currentSong: StateFlow<SongDetailed?> = _current_song

    // Current position of song, for the slider
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    // Coroutine that updates current position every half a second
    init {
        viewModelScope.launch {
            while (true) {
                val pos = playerManager.getCurrentPosition()
                val dur = playerManager.getDuration()
                _currentPosition.value = pos
                _duration.value = dur
                delay(500L)
            }
        }
    }

    fun playSong(songId: Long) {
        viewModelScope.launch {
            val song = songRepository.playSongByID(songId)

            val current = _current_song.value
            if (current == null || current.songId != song.songId) {
                _current_song.value = song
                playerManager.playSongPath(song.songPath)
            } else {
                playerManager.playSongPath(song.songPath)
            }

            _isPlaying.value = true
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

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}

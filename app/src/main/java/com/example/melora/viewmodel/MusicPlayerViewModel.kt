package com.example.melora.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.player.MusicPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicPlayerViewModel(
    app: Application
): AndroidViewModel(app) {

    private val playerManager = MusicPlayerManager(app)
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _current_song = MutableStateFlow<SongEntity?>(null)
    val currentSong: StateFlow<SongEntity?> = _current_song

    fun play(song: SongEntity) {
        _current_song.value = song
        playerManager.playSong(song)
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

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}

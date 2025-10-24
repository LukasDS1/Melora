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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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


    fun play(songId:Long) {
        viewModelScope.launch {
            val song = songRepository.playSongByID(songId)
            _current_song.value = song
            playerManager.playSongPath(song.songPath)
            _isPlaying.value = true
        }
    }

    fun getSongDetails(songId: Long) {
        viewModelScope.launch {
            val song = songRepository.playSongByID(songId)
            _current_song.value = song
        }
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

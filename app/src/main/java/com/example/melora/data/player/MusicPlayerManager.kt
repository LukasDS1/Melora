package com.example.melora.data.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import java.io.File

class MusicPlayerManager(private val context: Context) {
    private var player: ExoPlayer? = null

    fun initializePlayer() {
        if (player == null) player = ExoPlayer.Builder(context).build()
    }

    // Play song from path
    fun playSongPath(songPath: String) {
        initializePlayer()

        val cleanPath = songPath.removePrefix("file://")

        val file = File(cleanPath)
        if (!file.exists()) {
            return
        }

        val uri = Uri.fromFile(file)

        val mediaItem = MediaItem.fromUri(uri)
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.volume = 1f
        player!!.playWhenReady = true
        player!!.play()
    }

    // Pause song
    fun pause() {
        player?.pause()
    }

    fun resume() {
        player?.play()
    }

    // Stop song
    fun stop() {
        player?.stop()
    }

    // Release resources of ExoPlayer
    fun release() {
        player?.release()
        player = null
    }

    // Move to new position (miliseconds)
    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }

    // Get total duration (miliseconds)
    fun getDuration(): Long {
        return player?.duration?.coerceAtLeast(0L) ?: 0L
    }

    // Get current position of song (miliseconds)
    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0L
    }

    // Check if is playing
    fun isPlaying(): Boolean = player?.isPlaying ?: false
}
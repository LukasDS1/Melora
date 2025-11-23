package com.example.melora.data.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import java.io.File
import java.util.Base64

class MusicPlayerManager(private val context: Context) {

    private var player: ExoPlayer? = null
    private var currentTempFile: File? = null


    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }
    }

    fun playBase64Audio(base64Audio: String, songId: Long) {
        initializePlayer()

        currentTempFile?.delete()

        val audioBytes = try {
            Base64.getDecoder().decode(base64Audio)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        val tempFile = File(context.cacheDir, "melora_song_$songId.mp3")
        tempFile.writeBytes(audioBytes)

        currentTempFile = tempFile

        val uri = Uri.fromFile(tempFile)
        val mediaItem = MediaItem.fromUri(uri)

        player!!.apply {
            stop()
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            play()
        }
    }

    // Resume la reproducción
    fun resume() {
        player?.play()
    }

    // Pausa la reproducción
    fun pause() {
        player?.pause()
    }

    // Detiene la reproducción
    fun stop() {
        player?.stop()
    }

    // Llevar a una posición específica
    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }

    // Duración total
    fun getDuration(): Long {
        return player?.duration?.takeIf { it > 0 } ?: 0L
    }

    // Posición actual
    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0L
    }

    // ¿Está sonando?
    fun isPlaying(): Boolean = player?.isPlaying ?: false

    // Libera ExoPlayer y borra archivo temporal
    fun release() {
        player?.release()
        player = null

        currentTempFile?.delete()
        currentTempFile = null
    }
}

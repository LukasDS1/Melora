package com.example.melora.data.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.song.SongEntity
import java.io.File

class MusicPlayerManager(private val context: Context) {
    private var player: ExoPlayer? = null

    fun initializePlayer() {
        if (player == null) player = ExoPlayer.Builder(context).build()
    }

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
    fun pause() {
        player?.pause()
    }

    fun stop() {
        player?.stop()
    }

    fun release() {
        player?.release()
        player = null
    }

    fun isPlaying(): Boolean = player?.isPlaying ?: false
}
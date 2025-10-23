package com.example.melora.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import com.example.melora.data.player.MusicPlayerManager
import com.example.melora.viewmodel.MusicPlayerViewModel
import com.example.melora.viewmodel.MusicPlayerViewModelFactory

// hola lukas donoso dejo api por si acaso https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3
@Composable
fun PlayerScreen(vm: MusicPlayerViewModel) {
    val context = LocalContext.current
    val vm: MusicPlayerViewModel = viewModel(
        factory = MusicPlayerViewModelFactory(context.applicationContext as Application)
    )
    val currentSong by vm.currentSong.collectAsState()
    val isPlaying by vm.isPlaying.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(currentSong?.songName ?: "No song selected")
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            IconButton(onClick = {
                currentSong?.let {
                    if (isPlaying) vm.pause() else vm.play(it)
                }
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null
                )
            }
        }
    }
}

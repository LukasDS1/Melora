package com.example.melora.ui.screen

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.player.MusicPlayerManager
import com.example.melora.data.repository.SongRepository
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.viewmodel.MusicPlayerViewModel
import com.example.melora.viewmodel.MusicPlayerViewModelFactory

// hola lukas donoso dejo api por si acaso https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3
@Composable
fun PlayerScreen(songId:Long) {
    val context = LocalContext.current
    val db = MeloraDB.getInstance(context)
    val songRepository = SongRepository(db.songDao())
    val vm: MusicPlayerViewModel = viewModel(
        factory = MusicPlayerViewModelFactory(context.applicationContext as Application,songRepository)
    )

    val bg = PrimaryBg
    val currentSong by vm.currentSong.collectAsState()
    val isPlaying by vm.isPlaying.collectAsState()

    LaunchedEffect(songId){
        vm.play(songId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

        }
    }
}

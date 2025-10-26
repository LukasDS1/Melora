package com.example.melora.ui.screen

import android.app.Application
import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.player.MusicPlayerManager
import com.example.melora.data.repository.SongRepository
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.viewmodel.MusicPlayerViewModel
import com.example.melora.viewmodel.MusicPlayerViewModelFactory
import com.example.melora.R
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.ui.theme.SecondaryBg
import com.example.melora.viewmodel.BanViewModel
import com.example.melora.viewmodel.FavoriteViewModel

@Composable
fun PlayerScreenVm(
    songId: Long,
    onExitPlayer: () -> Unit,
    vm: MusicPlayerViewModel,
    favVm: FavoriteViewModel,
    roleId: Long?,
    banVm: BanViewModel
) {

    val currentSong by vm.currentSong.collectAsStateWithLifecycle()
    val isPlaying by vm.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by vm.currentPosition.collectAsStateWithLifecycle()
    val duration by vm.duration.collectAsStateWithLifecycle()
    val isFavorite by favVm.currentIsFavorite.collectAsStateWithLifecycle()
    var showBanCard by remember { mutableStateOf(false) }
    var banReason by remember { mutableStateOf("") }

    LaunchedEffect(songId){
        vm.playSong(songId)
        favVm.updateFavoriteState(songId)
    }

    PlayerScreen(
        songId = songId,
        currentSong = currentSong,
        isPlaying = isPlaying,
        isFavorite = isFavorite,
        toggleFavorite = favVm::toggleFavorite,
        exitPlayer = onExitPlayer,
        playSong = vm::playSong,
        resume = vm::play,
        pause = vm::pause,
        stop = vm::stop,
        seekTo = vm::seekTo,
        currentPosition = currentPosition,
        duration = duration,
        roleId = roleId,
        onBanSong = banVm::banSong
    )
}

@Composable
fun PlayerScreen(
    songId:Long,
    currentSong: SongDetailed?,
    isPlaying: Boolean,
    isFavorite: Boolean,
    toggleFavorite: (Long) -> Unit,
    exitPlayer: () -> Unit,
    playSong:(Long) -> Unit,
    resume:() -> Unit,
    pause:() -> Unit,
    stop: () -> Unit,
    seekTo: (Long) -> Unit,
    currentPosition: Long,
    duration: Long,
    roleId: Long?,
    onBanSong: (Long, String) -> Unit
) {
    val context = LocalContext.current

    var showBanCard by remember { mutableStateOf(false) }
    var banReason by remember { mutableStateOf("") }

    BackHandler {
        stop()
        exitPlayer()
    }


    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    val bg = PrimaryBg

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        bg,
                        Color(0xFF31453E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        stop()
                        exitPlayer()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Go back to home button"
                    )
                }

                Text(
                    text = "Melora",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                IconButton(
                    onClick = {
                        if (roleId == 1L) {
                            showBanCard = !showBanCard
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Block,
                        contentDescription = "Ban song",
                        tint = if (roleId == 1L) Color.Black else bg
                    )
                }

            }


            Spacer(Modifier.height(30.dp))

            Text(
                text = currentSong?.songName ?: "No song selected",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currentSong?.nickname ?: "Unknown Artist",
                fontSize = 18.sp
            )

            Spacer(Modifier.height(20.dp))

            AsyncImage(
                model = currentSong?.coverArt,
                contentDescription = "Cover art",
                modifier = Modifier
                    .size(250.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(
                    value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                    onValueChange = { newValue ->
                        seekTo((newValue * duration).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.Gray
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = formatTime(duration),
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(50.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.Abc,
                        contentDescription = "Lyrics button"
                    )
                }

                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                        contentDescription = "Last song button"
                    )
                }

                IconButton(
                    onClick = { if (isPlaying) pause() else resume() }
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play or Stop song button"
                    )
                }

                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                        contentDescription = "Forward song button"
                    )
                }

                IconButton(onClick = {toggleFavorite(songId)}) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }
        }
        if (showBanCard && roleId == 1L && currentSong != null) {
            ElevatedCard(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(20.dp)
                    .fillMaxWidth(0.9f),
                colors = CardDefaults.elevatedCardColors(containerColor = PrimaryBg)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ban Song", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("ID: ${currentSong.songId}", color = Color.LightGray)
                    Text("Name: ${currentSong.songName}", color = Color.White)
                    Text("Artist: ${currentSong.nickname}", color = Color.White)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = banReason,
                        onValueChange = { banReason = it },
                        label = { Text("Ban reason") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                if (banReason.isNotBlank()) {
                                    onBanSong(songId, banReason)
                                        Toast.makeText(
                                            context,
                                            "The song has been banned",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showBanCard = false
                                        banReason = ""
                                        exitPlayer()

                                } else {
                                    Toast.makeText(
                                        context,
                                        "You must enter a reason",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Confirm", color = Color.White)
                        }

                        OutlinedButton(onClick = {
                            showBanCard = false
                            banReason = ""
                        }) {
                            Text("Cancel", color = Color.White)
                        }
                    }
                }
            }
        }


    }
}

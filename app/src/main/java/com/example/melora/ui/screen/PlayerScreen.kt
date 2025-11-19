package com.example.melora.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Block
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.viewmodel.MusicPlayerViewModel
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.remote.dto.SongDetailedDto
import com.example.melora.ui.theme.Lato
import com.example.melora.viewmodel.BanViewModel
import com.example.melora.viewmodel.FavoriteViewModel

@Composable
fun PlayerScreenVm(
    songId: Long,
    onExitPlayer: () -> Unit,
    vm: MusicPlayerViewModel,
    favVm: FavoriteViewModel,
    roleName: String,
    banVm: BanViewModel
) {

    val currentSong by vm.currentSong.collectAsStateWithLifecycle()
    val isPlaying by vm.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by vm.currentPosition.collectAsStateWithLifecycle()
    val duration by vm.duration.collectAsStateWithLifecycle()
    val isFavorite by favVm.currentIsFavorite.collectAsStateWithLifecycle()

    LaunchedEffect(songId) {
        vm.playSong(songId)
        favVm.updateFavoriteState(songId)
    }

    LaunchedEffect(Unit) {
        println("DEBUG ROLE NAME = '$roleName'")
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
        roleName = roleName,
        onBanSong = banVm::banSong
    )
}

@Composable
fun PlayerScreen(
    songId: Long,
    currentSong: SongDetailedDto?,
    isPlaying: Boolean,
    isFavorite: Boolean,
    toggleFavorite: (Long) -> Unit,
    exitPlayer: () -> Unit,
    playSong: (Long) -> Unit,
    resume: () -> Unit,
    pause: () -> Unit,
    stop: () -> Unit,
    seekTo: (Long) -> Unit,
    currentPosition: Long,
    duration: Long,
    roleName: String,
    onBanSong: (Long, String) -> Unit
) {

    val context = LocalContext.current
    var showBanCard by remember { mutableStateOf(false) }
    var banReason by remember { mutableStateOf("") }

    BackHandler {
        stop()
        exitPlayer()
    }

    // --- Format time for slider
    fun formatTime(ms: Long): String {
        val total = ms / 1000
        return "%02d:%02d".format(total / 60, total % 60)
    }

    // Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryBg,
                        Color(0xFF31453E)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ============================
            //      TOP BAR
            // ============================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        stop()
                        exitPlayer()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Melora",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        if (roleName == "ADMIN")
                            showBanCard = !showBanCard
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Block,
                        contentDescription = "Ban",
                        tint = if (roleName == "ADMIN") Color.White else Color.Transparent
                    )
                }
            }

            Spacer(Modifier.height(25.dp))

            // ============================
            //       SONG INFO
            // ============================
            Text(
                text = currentSong?.songName ?: "---",
                fontSize = 26.sp,
                color = Color.White,
                fontFamily = Lato,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = currentSong?.nickname ?: "Unknown Artist",
                fontSize = 18.sp,
                color = Color.LightGray,
                fontFamily = Lato,
            )

            Spacer(Modifier.height(24.dp))

            // ============================
            //       COVER ART
            // ============================
            val coverModel: Any? = remember(currentSong) {
                when {
                    !currentSong?.coverArtBase64.isNullOrBlank() ->
                        android.util.Base64.decode(
                            currentSong!!.coverArtBase64,
                            android.util.Base64.DEFAULT
                        )

                    !currentSong?.coverArt.isNullOrBlank() ->
                        android.util.Base64.decode(
                            currentSong!!.coverArt,
                            android.util.Base64.DEFAULT
                        )

                    else -> null
                }
            }

            AsyncImage(
                model = coverModel,
                contentDescription = "Cover",
                modifier = Modifier
                    .size(260.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(30.dp))

            // ============================
            //       SLIDER
            // ============================
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(
                    value = if (duration > 0) currentPosition / duration.toFloat() else 0f,
                    onValueChange = { seekTo((it * duration).toLong()) },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.Gray
                    )
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(currentPosition), color = Color.White)
                    Text(formatTime(duration), color = Color.White)
                }
            }

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // PREV BUTTON
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Filled.KeyboardDoubleArrowLeft,
                            contentDescription = "Prev",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // PLAY / PAUSE CENTER BUTTON
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { if (isPlaying) pause() else resume() },
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                // NEXT BUTTON
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Filled.KeyboardDoubleArrowRight,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // FAVORITE BUTTON (opcional mover)
                IconButton(onClick = { toggleFavorite(songId) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }
        }

            // ============================
        //      BAN CARD
        // ============================
        if (showBanCard && roleName == "ADMIN" && currentSong != null) {
            ElevatedCard(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.9f)
                    .padding(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFF1C1C1C)
                )
            ) {

                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("Ban Song", color = Color.White, fontSize = 20.sp, fontFamily = Lato)

                    Text("ID: ${currentSong!!.idSong}", color = Color.White)
                    Text("Name: ${currentSong!!.songName}", color = Color.White)
                    Text("Artist: ${currentSong!!.nickname}", color = Color.White)

                    Spacer(Modifier.height(15.dp))

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

                    Spacer(Modifier.height(20.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Button(
                            onClick = {
                                if (banReason.isBlank()) {
                                    Toast.makeText(context, "Enter a reason", Toast.LENGTH_SHORT).show()
                                } else {
                                    onBanSong(songId, banReason)
                                    stop()
                                    banReason = ""
                                    showBanCard = false
                                    exitPlayer()
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


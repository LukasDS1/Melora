package com.example.melora.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.Resaltado
import com.example.melora.viewmodel.HomeScreenViewModel
import com.example.melora.R
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.SecondaryBg

@Composable
fun HomeScreenVm(
    vm: HomeScreenViewModel,
    goPlayer: (Long) -> Unit
) {
    val songs by vm.songs.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.loadSongs() }

    HomeScreen(
        songs = songs,
        goPlayer = goPlayer
    )
}

@Composable
fun HomeScreen(
    songs: List<SongDetailed>,
    goPlayer: (Long) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Resaltado)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (songs.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.music_not_found),
                    contentDescription = "music not found icon",
                    tint = Color.Unspecified
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "No songs available",
                    color = Color.Black,
                    fontFamily = Lato
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(songs) { song ->
                    Button(
                        onClick = { goPlayer(song.songId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Resaltado, // primarybg
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(
                            4.dp,
                            8.dp,
                            16.dp,
                            8.dp
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Portada
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(song.coverArt ?: R.drawable.music_not_found_placeholder)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Cover Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Información
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = song.songName,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    fontFamily = Lato
                                )
                                Text(
                                    text = "by ${song.nickname}",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    fontFamily = Lato
                                )
//                                Text(
//                                    text = formatTime(song.durationSong),
//                                    color = Color.Black,
//                                    fontSize = 12.sp,
//                                    fontFamily = Lato
//                                )
                            }
                            // TODO: add button to make song favorite
                        }
                    }
                }
            }
        }
    }
}

// Formatea duración en mm:ss
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

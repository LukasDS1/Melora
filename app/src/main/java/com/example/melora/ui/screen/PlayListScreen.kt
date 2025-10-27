package com.example.melora.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.viewmodel.PlaylistViewModel
import java.text.SimpleDateFormat
import kotlinx.coroutines.launch

@Composable
fun PlaylistDetailScreenVm(
    playlistId: Long,
    playlistViewModel: PlaylistViewModel,
    goPlayer: (Long) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // Estado con las canciones
    val songs by playlistViewModel.songsInPlaylist.collectAsState()
    val myPlaylists by playlistViewModel.myPlaylists.collectAsState()

    // Buscar la playlist seleccionada
    val playlist = myPlaylists.find { it.idPlaylist == playlistId }

    LaunchedEffect(playlistId) {
        playlistViewModel.loadSongsFromPlaylist(playlistId)
    }

    PlaylistDetailScreen(
        playlistName = playlist?.playListName ?: "Unknown Playlist",
        creationDate = playlist?.creationDate ?: System.currentTimeMillis(),
        songs = songs,
        goPlayer = goPlayer,
        onBack = onBack
    )
}

@Composable
fun PlaylistDetailScreen(
    playlistName: String,
    creationDate: Long,
    songs: List<SongDetailed>,
    goPlayer: (Long) -> Unit,
    onBack: () -> Unit
) {
    val formattedDate = SimpleDateFormat("dd/MM/yyyy").format(creationDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBg)
            .padding(16.dp)
    ) {


        Text(
            text = playlistName,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )

        Text(
            text = "Created on $formattedDate",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))


        if (songs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("This playlist has no songs yet", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(songs) { song ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = song.songName,
                                color = Color.Black,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "by ${song.nickname}",
                                color = Color.Gray,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { goPlayer(song.songId) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                            ) {
                                Text("Play", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

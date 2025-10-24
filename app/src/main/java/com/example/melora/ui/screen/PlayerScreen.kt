package com.example.melora.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.viewmodel.FavoriteViewModel
import com.example.melora.viewmodel.MusicPlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(
    song: SongDetailed,
    userId: Long,
    playerViewModel: MusicPlayerViewModel,
    favoriteViewModel: FavoriteViewModel
) {
    val scope = rememberCoroutineScope()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val currentSong by playerViewModel.currentSong.collectAsState()
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(song.songId, userId) {
        isFavorite = favoriteViewModel.isFavorite(userId, song.songId)
        playerViewModel.play(song.songId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = song.songName,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "by ${song.nickname}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            IconButton(onClick = {
                if (isPlaying) playerViewModel.pause() else playerViewModel.play(song.songId)
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = {
                scope.launch {
                    favoriteViewModel.seleccionarFavorito(userId, song.songId)
                    isFavorite = !isFavorite
                }
            }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Duración: ${song.durationSong} seg",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "Artista ID: ${song.artistId}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

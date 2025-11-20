package com.example.melora.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.remote.dto.PlaylistSongDto
import com.example.melora.data.storage.UserPreferences
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.viewmodel.PlaylistApiViewModel
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import kotlinx.coroutines.launch

@Composable
fun PlaylistDetailScreenVm(
    playlistId: Long,
    playlistViewModel: PlaylistApiViewModel,
    goPlayer: (Long) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var currentUserId by remember { mutableStateOf<Long?>(null) }

    // Cargar ID usuario
    LaunchedEffect(Unit) {
        currentUserId = prefs.userId.firstOrNull()
    }

    // Estados del ViewModel API
    val playlist by playlistViewModel.currentPlaylist.collectAsState()
    val songs by playlistViewModel.songsInPlaylist.collectAsState()
    val followed by playlistViewModel.followedPlaylists.collectAsState()

    val isFollowing = followed.any { it.idPlaylist == playlistId }

    // Cargar datos
    LaunchedEffect(playlistId) {
        playlistViewModel.loadPlaylistById(playlistId)
        playlistViewModel.loadSongsFromPlaylist(playlistId)
    }

    PlaylistDetailScreen(
        playlistName = playlist?.playlistName ?: "Unknown Playlist",
        creationDate = playlist?.fechaCreacion ?: "",
        songs = songs ?: emptyList(),
        isMine = playlist?.userId == currentUserId,
        isFollowing = isFollowing,
        onToggleFollow = {
            if (currentUserId != null) {
                scope.launch {
                    playlistViewModel.toggleFollow(currentUserId!!, playlistId)
                }
            }
        },
        goPlayer = goPlayer,
        onBack = onBack
    )
}


@Composable
fun PlaylistDetailScreen(
    playlistName: String,
    creationDate: String,
    songs: List<PlaylistSongDto>,
    isMine: Boolean,
    isFollowing: Boolean,
    onToggleFollow: () -> Unit,
    goPlayer: (Long) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBg)
            .padding(16.dp)
    ) {

        // Título + fecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = playlistName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
                Text(
                    text = "Created on $creationDate",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
            }

            if (!isMine) {
                val tint by animateColorAsState(
                    if (isFollowing) Color.Red else Color.Black
                )

                IconButton(onClick = onToggleFollow) {
                    Icon(
                        imageVector = if (isFollowing) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        tint = tint,
                        contentDescription = null
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (songs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("This playlist has no songs yet", color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(songs) { song ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { goPlayer(song.songId) }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(song.songName, color = Color.Black)
                        Text("by ${song.nickname}", color = Color.Gray, fontSize = 12.sp)
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

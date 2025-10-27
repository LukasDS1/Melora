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
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.storage.UserPreferences
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.viewmodel.PlaylistViewModel
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import kotlinx.coroutines.launch

@Composable
fun PlaylistDetailScreenVm(
    playlistId: Long,
    playlistViewModel: PlaylistViewModel,
    goPlayer: (Long) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var currentUserId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(Unit) {
        currentUserId = prefs.userId.firstOrNull()
    }

    val songs by playlistViewModel.songsInPlaylist.collectAsState()
    val myPlaylists by playlistViewModel.myPlaylists.collectAsState()
    val followedPlaylists by playlistViewModel.followedPlaylists.collectAsState()
    val currentPlaylist by playlistViewModel.currentPlaylist.collectAsState()

    val playlist = (myPlaylists + followedPlaylists)
        .find { it.idPlaylist == playlistId } ?: currentPlaylist

    LaunchedEffect(playlistId) {
        playlistViewModel.loadSongsFromPlaylist(playlistId)
        if (playlist == null) playlistViewModel.loadPlaylistById(playlistId)
    }

    PlaylistDetailScreen(
        playlistName = playlist?.playListName ?: "Unknown Playlist",
        creationDate = playlist?.creationDate ?: System.currentTimeMillis(),
        songs = songs,
        isMine = playlist?.userId == currentUserId,
        isFollowing = followedPlaylists.any { it.idPlaylist == playlistId },
        onToggleFollow = {
            if (currentUserId != null && playlist != null) {
                scope.launch {
                    playlistViewModel.toggleFollow(currentUserId!!, playlist.idPlaylist)
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
    creationDate: Long,
    songs: List<SongDetailed>,
    isMine: Boolean,
    isFollowing: Boolean,
    onToggleFollow: () -> Unit,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
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
                    text = "Created on $formattedDate",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
            }

            if (!isMine) {
                val tintColor by animateColorAsState(
                    targetValue = if (isFollowing) Color.Red else Color.Black,
                    label = ""
                )
                IconButton(
                    onClick = onToggleFollow,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isFollowing)
                            Icons.Filled.Favorite
                        else
                            Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFollowing) "Unfollow Playlist" else "Follow Playlist",
                        tint = tintColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(8.dp))


        if (songs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("This playlist has no songs yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(songs) { song ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PrimaryBg)
                            .clickable { goPlayer(song.songId) }
                            .padding(vertical = 10.dp, horizontal = 12.dp)
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
                    }

                    Divider(
                        color = Color.Black,
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
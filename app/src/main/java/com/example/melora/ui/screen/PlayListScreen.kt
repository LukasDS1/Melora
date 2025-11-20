package com.example.melora.ui.screen

import android.util.Base64
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.melora.R
import com.example.melora.data.remote.dto.PlaylistSongDto
import com.example.melora.data.storage.UserPreferences
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.viewmodel.PlaylistApiViewModel
import kotlinx.coroutines.flow.firstOrNull
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

    LaunchedEffect(Unit) {
        currentUserId = prefs.userId.firstOrNull()
    }

    val playlist by playlistViewModel.currentPlaylist.collectAsState()
    val songs by playlistViewModel.songsInPlaylist.collectAsState()
    val followed by playlistViewModel.followedPlaylists.collectAsState()

    val isFollowing = followed.any { it.idPlaylist == playlistId }

    LaunchedEffect(playlistId) {
        playlistViewModel.loadPlaylistById(playlistId)
        playlistViewModel.loadSongsFromPlaylist(playlistId)
    }

    PlaylistDetailScreen(
        playlistName = playlist?.playlistName ?: "Unknown playlist",
        creationDate = playlist?.fechaCreacion ?: "",
        songs = songs,
        isMine = (playlist?.userId == currentUserId),
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
    val context = LocalContext.current

    val formattedDate = remember(creationDate) {
        if (creationDate.contains("T")) creationDate.substringBefore("T") else creationDate
    }

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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = playlistName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black,
                    fontFamily = Lato,
                    fontWeight = FontWeight.SemiBold
                )

                if (formattedDate.isNotBlank()) {
                    Text(
                        text = "Created on $formattedDate",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.DarkGray,
                        fontFamily = Lato
                    )
                }
            }

            if (!isMine) {
                val tint by animateColorAsState(
                    if (isFollowing) Color.Red else Color.Black,
                    label = "followTint"
                )

                IconButton(onClick = onToggleFollow) {
                    Icon(
                        imageVector = if (isFollowing) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        tint = tint,
                        contentDescription = "Follow playlist"
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (songs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "This playlist has no songs yet",
                    color = Color.DarkGray,
                    fontFamily = Lato
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(songs) { song ->

                    val coverImageBytes = song.coverArtBase64?.let {
                        Base64.decode(it, Base64.DEFAULT)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { goPlayer(song.songId) },
                        color = Color.White,
                        shadowElevation = 4.dp,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(coverImageBytes ?: R.drawable.music_not_found_placeholder)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Cover",
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = song.songName,
                                    fontFamily = Lato,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "by ${song.nickname ?: "Unknown"}",
                                    fontFamily = Lato,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }

                            val durationText = remember(song.durationSong) {
                                val totalSec = song.durationSong
                                val min = totalSec / 60
                                val sec = totalSec % 60
                                String.format("%d:%02d", min, sec)
                            }

                            Text(
                                text = durationText,
                                fontFamily = Lato,
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

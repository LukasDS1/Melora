package com.example.melora.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.melora.R
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.ui.theme.PlayfairDisplay
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.viewmodel.ArtistProfileViewModel

@Composable
fun ArtistProfileScreenVm(
    artistId: Long,
    vm: ArtistProfileViewModel,
    goPlayer: (Long) -> Unit
) {
    var nickname by remember { mutableStateOf<String?>(null) }
    var profilePicture by remember { mutableStateOf<String?>(null) }
    var songs by remember { mutableStateOf<List<SongDetailed>>(emptyList()) }

    LaunchedEffect(artistId) {
        vm.loadArtist(artistId)
    }

    val artistData = vm.artistData

    LaunchedEffect(artistData) {
        artistData?.let {
            nickname = it.artist.nickname
            profilePicture = it.artist.profilePicture
            songs = it.songs
        }
    }

    ArtistProfileScreen(
        nickname = nickname,
        profilePicture = profilePicture,
        songs = songs,
        goPlayer = goPlayer
    )
}

@Composable
fun ArtistProfileScreen(
    nickname: String?,
    profilePicture: String?,
    songs: List<SongDetailed>,
    goPlayer: (Long) -> Unit
) {
    val bg = Resaltado

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = if (profilePicture != null)
                    rememberAsyncImagePainter(profilePicture)
                else
                    painterResource(R.drawable.defaultprofilepicture),
                contentDescription = "Artist picture",
                modifier = Modifier
                    .aspectRatio(1f)
                    .size(120.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = nickname ?: "Unknown Artist",
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = PlayfairDisplay),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Songs:",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = PlayfairDisplay),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 10.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 10.dp)
            ) {
                songs.forEach { song ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimaryBg),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = song.songName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { goPlayer(song.songId) }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow, // tu ícono de play
                                    contentDescription = "Play",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


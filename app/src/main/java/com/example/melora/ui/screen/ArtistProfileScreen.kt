package com.example.melora.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.melora.R
import com.example.melora.data.remote.dto.SongDetailedDto
import com.example.melora.ui.theme.PlayfairDisplay
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.viewmodel.ArtistProfileViewModel
import com.example.melora.viewmodel.UserArtistApiPublicViewModel
import kotlinx.coroutines.launch
import android.util.Base64
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ArtistProfileScreenVm(
    artistId: Long,
    arVm: ArtistProfileViewModel,
    vm: UserArtistApiPublicViewModel,
    goPlayer: (Long) -> Unit,
    roleName: String?
) {
    var nickname by remember { mutableStateOf<String?>(null) }
    var profilePicture by remember { mutableStateOf<String?>(null) }
    var songs by remember { mutableStateOf<List<SongDetailedDto>>(emptyList()) }

    LaunchedEffect(artistId) {
        vm.loadArtistById(artistId)
    }

    val artistData = vm.artistData

    LaunchedEffect(artistData) {
        artistData?.let {
            nickname = it.nickname
            profilePicture = it.profilePhotoBase64
            songs = it.songs
        }
    }

    ArtistProfileScreen(
        artistId = artistId,
        nickname = nickname,
        profilePicture = profilePicture,
        songs = songs,
        goPlayer = goPlayer,
        roleName = roleName,
        banUser = arVm::banUser
    )
}

@Composable
fun ArtistProfileScreen(
    artistId: Long,
    nickname: String?,
    profilePicture: String?,
    songs: List<SongDetailedDto>,
    goPlayer: (Long) -> Unit,
    roleName: String?,
    banUser: (Long, (Boolean) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showBanDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Resaltado)
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ================
            // FOTO DE PERFIL
            // ================
            val decodedArtistPhoto = profilePicture?.let {
                runCatching { Base64.decode(it, Base64.DEFAULT) }.getOrNull()
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(decodedArtistPhoto ?: R.drawable.defaultprofilepicture)
                    .crossfade(true)
                    .build(),
                contentDescription = "Artist",
                modifier = Modifier
                    .size(120.dp)
                    .clip(MaterialTheme.shapes.medium), // cuadrada con bordecito
                contentScale = ContentScale.Crop
            )


            Spacer(Modifier.height(20.dp))

            Text(
                text = nickname ?: "Unknown Artist",
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = PlayfairDisplay),
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Songs:",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                songs.forEach { song ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimaryBg)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // ================
                            // THUMBNAIL SONG
                            // ================
                            val decodedCover = song.coverArtBase64?.let {
                                runCatching { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }
                                    .getOrNull()
                            }

                            Image(
                                painter = if (decodedCover != null)
                                    rememberAsyncImagePainter(decodedCover)
                                else painterResource(R.drawable.defaultprofilepicture),
                                contentDescription = "Cover",
                                modifier = Modifier
                                    .size(55.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(Modifier.width(12.dp))

                            Text(
                                song.songName,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = { goPlayer(song.idSong) }) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = { if (roleName == "ADMIN") showBanDialog = true },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Block,
                contentDescription = "Ban user",
                tint = if (roleName == "ADMIN") Color.Red else Color.Transparent
            )
        }

        if (showBanDialog) {
            AlertDialog(
                onDismissRequest = { showBanDialog = false },
                title = { Text("Ban User") },
                text = { Text("Are you sure you want to ban this user?") },

                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                banUser(artistId) { success ->
                                    if (success) {
                                        Toast.makeText(context, "User banned successfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Error banning user", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            showBanDialog = false
                        }
                    ) {
                        Text("Yes", color = Color.Red)
                    }
                },

                dismissButton = {
                    TextButton(onClick = { showBanDialog = false }) {
                        Text("No", color = Color.White)
                    }
                },

                containerColor = Color(0xFF222222)
            )
        }
    }
}

package com.example.melora.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.melora.R
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.storage.UserPreferences
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.viewmodel.ArtistProfileViewModel
import com.example.melora.viewmodel.FavoriteViewModel
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun MyProfileScreenVm(
    vm: ArtistProfileViewModel,
    goPlayer: (Long) -> Unit,
    favVm: FavoriteViewModel,
    onEditProfile: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var nickname by remember { mutableStateOf<String?>(null) }
    var profilePicture by remember { mutableStateOf<String?>(null) }
    var songs by remember { mutableStateOf<List<SongDetailed>>(emptyList()) }





    LaunchedEffect(Unit) {
        val id = prefs.userId.firstOrNull()
        if (id != null) {
            vm.loadArtist(id)
        }
    }

    val artistData = vm.artistData

    LaunchedEffect(artistData) {
        artistData?.let {
            nickname = it.artist.nickname
            profilePicture = it.artist.profilePicture
            songs = it.songs
        }
    }

    MyProfileScreen(
        nickname = nickname,
        profilePicture = profilePicture,
        songs = songs,
        goPlayer = goPlayer,
        onDeleteSong = vm::deleteSong,
        onToggleFavorite = favVm::toggleFavorite,
        onEditProfile = onEditProfile,
        updateSong =  vm::updateSongDetails
    )
}

@Composable
fun MyProfileScreen(
    nickname: String?,
    profilePicture: String?,
    songs: List<SongDetailed>,
    goPlayer: (Long) -> Unit,
    onDeleteSong: (Long) -> Unit,
    onEditProfile: () -> Unit,
    updateSong: (Long, String?, String?) -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    val bg = Resaltado
    var expanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<SongDetailed?>(null) }
    var newName by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                contentDescription = "Profile picture",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onEditProfile,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBg
                )
            ) {
                Text("Edit profile", fontFamily = Lato)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nickname ?: "Unknown Artist",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Songs:",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                songs.forEach { song ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bg),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = { goPlayer(song.songId) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = song.songName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = song.songDescription ?: "No description yet",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Menu options",
                                        tint = Color.White
                                    )
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(Color(0xFF222222))
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Delete Song", color = Color.White) },
                                        onClick = {
                                            expanded = false
                                            onDeleteSong(song.songId)
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text("Add Favorite", color = Color.White) },
                                        onClick = {
                                            expanded = false
                                            onToggleFavorite(song.songId)
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text("Edit your song", color = Color.White) },
                                        onClick = {
                                            expanded = false
                                            selectedSong = song
                                            newName = song.songName
                                            newDesc = song.songDescription?: ""
                                            showEditDialog = true
                                        }
                                    )
                                }
                                if (showEditDialog && selectedSong != null) {
                                    AlertDialog(
                                        onDismissRequest = { showEditDialog = false },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                showEditDialog = false
                                                updateSong(selectedSong!!.songId, newName, newDesc)
                                                Toast.makeText(context,"Song updated sucefully", Toast.LENGTH_SHORT).show()
                                            }) {
                                                Text("Save", color = Color.White)
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showEditDialog = false }) {
                                                Text("Cancel", color = Color.Gray)
                                            }
                                        },
                                        title = { Text("Edit your song", color = Color.White) },
                                        text = {
                                            Column {
                                                OutlinedTextField(
                                                    value = newName,
                                                    onValueChange = { newName = it },
                                                    label = { Text("Song name") },
                                                    singleLine = true
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                OutlinedTextField(
                                                    value = newDesc,
                                                    onValueChange = { newDesc = it },
                                                    label = { Text("Description") }
                                                )
                                            }
                                        },
                                        containerColor = Color(0xFF222222)
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.melora.ui.screen
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.melora.R
import com.example.melora.data.remote.dto.SongDetailedDto
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.ui.theme.ResaltadoNegative
import com.example.melora.ui.theme.SecondaryBg
import com.example.melora.viewmodel.ArtistProfileViewModel
import com.example.melora.viewmodel.FavoriteApiViewModel
import android.util.Base64
import androidx.compose.foundation.clickable


fun isBase64Image(s: String?): Boolean {
    if (s.isNullOrBlank()) return false
    return try {
        android.util.Base64.decode(s, android.util.Base64.DEFAULT)
        true
    } catch (e: Exception) {
        false
    }
}

@Composable
fun MyProfileScreenVm(
    vm: ArtistProfileViewModel,
    goPlayer: (Long) -> Unit,
    favVm: FavoriteApiViewModel,
    onEditProfile: () -> Unit
) {
    LaunchedEffect(Unit) { vm.loadMyProfile() }

    val artist = vm.artistData

    MyProfileScreen(
        nickname = artist?.nickname,
        profilePicture = artist?.profilePhotoBase64,
        songs = artist?.songs ?: emptyList(),
        goPlayer = goPlayer,
        onDeleteSong = vm::deleteSong,
        onToggleFavorite = favVm::toggleFavorite,
        updateSong = vm::updateSongDetails,
        onEditProfile = onEditProfile
    )
}



// ------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------

@Composable
fun MyProfileScreen(
    nickname: String?,
    profilePicture: String?,
    songs: List<SongDetailedDto>,
    goPlayer: (Long) -> Unit,
    onDeleteSong: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    updateSong: (Long, String?, String?) -> Unit,
    onEditProfile: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Global UI state
    var expandedMenuForSong by remember { mutableStateOf<Long?>(null) }
    var menuOffset by remember { mutableStateOf<IntOffset?>(null) }

    var selectedSong by remember { mutableStateOf<SongDetailedDto?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var songToDelete by remember { mutableStateOf<Long?>(null) }


    var newName by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Resaltado)
            .padding(16.dp)
    ) {

        // --------------------------------------------------
        // CONTENT LIST
        // --------------------------------------------------
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                val context = LocalContext.current
                val decodedProfile = profilePicture?.let {
                    runCatching { Base64.decode(it, Base64.DEFAULT) }.getOrNull()
                }

                AsyncImage(
                    model = decodedProfile ?: R.drawable.defaultprofilepicture,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                )
            }

            item {
                Button(
                    onClick = onEditProfile,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg)
                ) {
                    Text("Edit profile", fontFamily = Lato)
                }
            }

            item {
                Text(
                    text = nickname ?: "Unknown Artist",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }

            item {
                Text(
                    text = "Your Songs:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }

            items(songs) { song ->
                SongItem(
                    song = song,
                    onExpandMenu = { id, offset ->
                        expandedMenuForSong = id
                        menuOffset = offset
                    },
                    goPlayer = goPlayer
                )
            }
        }

        // --------------------------------------------------
        // GLOBAL FIXED DROPDOWN MENU
        // --------------------------------------------------

        if (menuOffset != null) {

            DropdownMenu(
                expanded = expandedMenuForSong != null,
                onDismissRequest = {
                    expandedMenuForSong = null
                },
                offset = with(density) {
                    androidx.compose.ui.unit.DpOffset(
                        x = menuOffset!!.x.toDp(),
                        y = menuOffset!!.y.toDp()
                    )
                },
                properties = PopupProperties(
                    clippingEnabled = false,
                    focusable = true
                )
            ) {

                val currentId = expandedMenuForSong
                val song = songs.find { it.idSong == currentId }

                if (song != null) {

                    DropdownMenuItem(
                        text = { Text("Delete Song") },
                        onClick = {
                            expandedMenuForSong = null
                            songToDelete = song.idSong
                            showDeleteDialog = true
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Add Favorite") },
                        onClick = {
                            expandedMenuForSong = null
                            onToggleFavorite(song.idSong)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expandedMenuForSong = null
                            selectedSong = song
                            newName = song.songName
                            newDesc = song.songDescription ?: ""
                            showEditDialog = true
                        }
                    )
                }
            }
        }
    }

    // ------------------------------------------------------
    // EDIT SONG DIALOG
    // ------------------------------------------------------
    if (showEditDialog && selectedSong != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },

            containerColor = SecondaryBg, // Fondo blanco suave del diálogo

            title = {
                Text(
                    "Edit your song",
                    color = PrimaryBg, // texto verde oscuro
                    fontFamily = Lato
                )
            },

            text = {
                Column {

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Song name", color = PrimaryBg) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBg,
                            unfocusedBorderColor = ResaltadoNegative,
                            focusedLabelColor = PrimaryBg,
                            unfocusedLabelColor = ResaltadoNegative,
                            cursorColor = PrimaryBg,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(12.dp))


                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Description", color = PrimaryBg) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBg,
                            unfocusedBorderColor = ResaltadoNegative,
                            focusedLabelColor = PrimaryBg,
                            unfocusedLabelColor = ResaltadoNegative,
                            cursorColor = PrimaryBg,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        updateSong(selectedSong!!.idSong, newName, newDesc)
                        showEditDialog = false
                        Toast.makeText(context, "Song updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = PrimaryBg // verde oscuro
                    )
                ) {
                    Text("Save")
                }
            },


            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog && songToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },

            containerColor = SecondaryBg,

            title = {
                Text(
                    "Delete song",
                    color = PrimaryBg,
                    fontFamily = Lato
                )
            },

            text = {
                Text(
                    "Are you sure you want to delete this song?",
                    color = PrimaryBg,
                    fontFamily = Lato
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteSong(songToDelete!!)
                        showDeleteDialog = false
                        songToDelete = null
                        Toast.makeText(context, "Song deleted!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red // Botón rojo como advertencia
                    )
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        songToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black  // Texto visible y elegante
                    )
                ) {
                    Text("No")
                }
            }
        )
    }
}


@Composable
fun SongItem(
    song: SongDetailedDto,
    onExpandMenu: (Long, IntOffset?) -> Unit,
    goPlayer: (Long) -> Unit
) {
    val context = LocalContext.current

    var iconOffset by remember { mutableStateOf<IntOffset?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                goPlayer(song.idSong)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {

        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val bytes = song.coverArtBase64?.let {
                android.util.Base64.decode(it, android.util.Base64.DEFAULT)
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(bytes)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(song.songName, color = Color.White)
                Text(
                    song.songDescription ?: "No description yet",
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            IconButton(
                modifier = Modifier.onGloballyPositioned { coords ->
                    val windowPos = coords.localToWindow(Offset.Zero)
                    iconOffset = IntOffset(windowPos.x.toInt(), windowPos.y.toInt() + 40)
                },
                onClick = {
                    onExpandMenu(song.idSong, iconOffset)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}


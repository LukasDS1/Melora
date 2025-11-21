package com.example.melora.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.melora.data.remote.dto.PlaylistDto
import com.example.melora.data.remote.dto.SongDetailedDto
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.viewmodel.PlaylistApiViewModel
import kotlinx.coroutines.flow.firstOrNull
import com.example.melora.data.storage.UserPreferences
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import com.example.melora.R
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.lazy.LazyRow
import com.example.melora.viewmodel.FavoriteApiViewModel
import com.example.melora.viewmodel.SearchViewModel

@Composable
fun FavoriteScreenVm(
    favoriteViewModel: FavoriteApiViewModel,
    playlistViewModel: PlaylistApiViewModel,
    searchViewModel: SearchViewModel,
    goPlayer: (Long) -> Unit,
    goPlaylistDetail: (Long) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    var userId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        val id = prefs.userId.firstOrNull()
        if (id != null) {
            userId = id
            favoriteViewModel.loadFavorite()
            playlistViewModel.loadMyPlaylists(id)
            playlistViewModel.loadFollowedPlaylists(id)
            searchViewModel.loadAll()
        }
    }

    val favorites by favoriteViewModel.favorites.collectAsStateWithLifecycle()
    val myPlaylists by playlistViewModel.myPlaylists.collectAsStateWithLifecycle()
    val followedPlaylists by playlistViewModel.followedPlaylists.collectAsStateWithLifecycle()
    val allSongs by searchViewModel.songs.collectAsStateWithLifecycle()

    FavoriteScreen(
        favorites = favorites,
        myPlaylists = myPlaylists,
        followedPlaylists = followedPlaylists,
        allSongs = allSongs,
        playlistViewModel = playlistViewModel,
        userId = userId,
        goPlayer = goPlayer,
        goPlaylistDetail = goPlaylistDetail
    )
}

@Composable
fun FavoriteScreen(
    favorites: List<SongDetailedDto>,
    myPlaylists: List<PlaylistDto>,
    followedPlaylists: List<PlaylistDto>,
    allSongs: List<SongDetailedDto>,
    playlistViewModel: PlaylistApiViewModel,
    userId: Long?,
    goPlayer: (Long) -> Unit,
    goPlaylistDetail: (Long) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    val selectedSongs = remember { mutableStateListOf<Long>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Resaltado)
            .padding(16.dp)
    ) {

        // **************** FAVORITES SECTION ****************
        Text(
            text = "Your Favorites",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            fontFamily = Lato
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (favorites.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Don't have favorites yet",
                            color = Color.Black,
                            fontFamily = Lato
                        )
                    }
                }
            } else {
                items(favorites) { song ->
                    val context = LocalContext.current
                    Button(
                        onClick = { goPlayer(song.idSong) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Resaltado,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            val coverModel = if (!song.coverArtBase64.isNullOrBlank()) {
                                android.util.Base64.decode(
                                    song.coverArtBase64,
                                    android.util.Base64.DEFAULT
                                )
                            } else null

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(coverModel ?: R.drawable.music_not_found_placeholder)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Cover",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text(song.songName, fontFamily = Lato, color = Color.Black)
                                Text("by ${song.nickname}", fontFamily = Lato, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))


        // **************** PLAYLISTS SECTION ****************
        Text(
            text = "Your Playlists",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            fontFamily = Lato
        )

        Spacer(Modifier.height(8.dp))

        if (showForm) {
            PlaylistCreationForm(
                playlistName = playlistName,
                onNameChange = { playlistName = it },
                allSongs = allSongs,
                selectedSongs = selectedSongs,
                onSave = {
                    val uid = userId ?: return@PlaylistCreationForm
                    if (playlistName.isNotBlank()) {
                        playlistViewModel.createPlaylist(
                            playListName = playlistName,
                            accesoId = 1L,
                            catId = 1L,
                            userId = uid,
                            songIds = selectedSongs.toList()
                        ) {
                            playlistName = ""
                            selectedSongs.clear()
                            showForm = false
                        }
                    }
                }
            )

            Spacer(Modifier.height(16.dp))
        }

        val combinedPlaylists =
            (myPlaylists + followedPlaylists).distinctBy { it.idPlaylist }

        // *** MAIN PLAYLIST LIST ***
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            if (combinedPlaylists.isEmpty()) {
                item {
                    Text(
                        "No playlists found",
                        color = Color.Black,
                        fontFamily = Lato,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            } else {
                items(combinedPlaylists.filterNotNull()) { playlist ->
                    val safeName =
                        playlist.playlistName?.ifBlank { "Untitled playlist" } ?: "Untitled playlist"
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { playlist.idPlaylist?.let(goPlaylistDetail) }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(safeName, color = Color.Black, fontFamily = Lato, fontSize = 17.sp)
                        playlist.fechaCreacion?.let {
                            Text("Created on $it", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showForm = !showForm },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                if (showForm) "Cancel" else "Create new playlist",
                color = Color.White
            )
        }
    }
}


@Composable
fun PlaylistCreationForm(
    playlistName: String,
    onNameChange: (String) -> Unit,
    allSongs: List<SongDetailedDto>,
    selectedSongs: MutableList<Long>,
    onSave: () -> Unit
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        OutlinedTextField(
            value = playlistName,
            onValueChange = onNameChange,
            placeholder = { Text("Playlist name", fontFamily = Lato) },
            singleLine = true,
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.MusicVideo,
                    contentDescription = "icon",
                    tint = Color.Gray
                )
            }
        )

        Spacer(Modifier.height(10.dp))

        Text("Select songs:", fontFamily = Lato, color = Color.Black)

        LazyColumn(
            modifier = Modifier
                .height(200.dp)
                .padding(vertical = 8.dp)
        ) {
            items(allSongs) { song ->

                val isSelected = song.idSong in selectedSongs

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) selectedSongs.remove(song.idSong)
                            else selectedSongs.add(song.idSong)
                        }
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isSelected, onCheckedChange = null)

                    Spacer(Modifier.width(16.dp))

                    val coverModel =
                        if (!song.coverArtBase64.isNullOrBlank()) {
                            android.util.Base64.decode(
                                song.coverArtBase64,
                                android.util.Base64.DEFAULT
                            )
                        } else null

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(coverModel ?: R.drawable.music_not_found_placeholder)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Cover Art",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(song.songName, fontFamily = Lato, fontSize = 16.sp)
                        Text("by ${song.nickname}", fontFamily = Lato, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg)
        ) {
            Text("Save Playlist", color = Color.White)
        }
    }
}

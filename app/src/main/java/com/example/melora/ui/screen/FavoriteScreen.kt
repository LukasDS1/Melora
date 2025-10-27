package com.example.melora.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.melora.data.local.playlist.PlaylistEntity
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.storage.UserPreferences
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.viewmodel.FavoriteViewModel
import com.example.melora.viewmodel.PlaylistViewModel
import com.example.melora.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@Composable
fun FavoriteScreenVm(
    favoriteViewModel: FavoriteViewModel,
    playlistViewModel: PlaylistViewModel,
    searchViewModel: SearchViewModel,
    goPlayer: (Long) -> Unit,
    goPlaylistDetail: (Long) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var userId by remember { mutableStateOf<Long?>(null) }

    // 🔄 Cargar datos iniciales
    LaunchedEffect(Unit) {
        val id = prefs.userId.firstOrNull()
        if (id != null) {
            userId = id
            favoriteViewModel.loadFavorite()
            playlistViewModel.loadMyPlaylists(id)
            playlistViewModel.loadFollowedPlaylists(id)
            searchViewModel.loadAllSongs()
        }
    }

    val favorites by favoriteViewModel.favorites.collectAsState()
    val myPlaylists by playlistViewModel.myPlaylists.collectAsState()
    val followedPlaylists by playlistViewModel.followedPlaylists.collectAsState()
    val allSongs by searchViewModel.songs.collectAsState()

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
    favorites: List<SongDetailed>,
    myPlaylists: List<PlaylistEntity>,
    followedPlaylists: List<PlaylistEntity>,
    allSongs: List<SongDetailed>,
    playlistViewModel: PlaylistViewModel,
    userId: Long?,
    goPlayer: (Long) -> Unit,
    goPlaylistDetail: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showForm by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    val selectedSongs = remember { mutableStateListOf<Long>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBg)
            .padding(16.dp)
    ) {
        // 🎵 FAVORITOS
        Text(
            text = "Your Favorites",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )

        if (favorites.isEmpty()) {
            Text("Don't have favorites yet", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(favorites) { song ->
                    Button(
                        onClick = { goPlayer(song.songId) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Column {
                            Text(song.songName, color = Color.Black)
                            Text("by ${song.nickname}", color = Color.Gray)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Playlists",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )

        Button(
            onClick = { showForm = !showForm },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(if (showForm) "Cancel" else "Create new playlist", color = Color.White)
        }

        if (showForm) {
            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text("Playlist name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // 🎶 LISTADO DE TODAS LAS CANCIONES SELECCIONABLES
            Text("Select songs to include:", color = Color.Black)
            LazyColumn(
                modifier = Modifier
                    .height(220.dp)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(allSongs) { song ->
                    val isSelected = song.songId in selectedSongs
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isSelected) selectedSongs.remove(song.songId)
                                else selectedSongs.add(song.songId)
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isSelected, onCheckedChange = null)
                        Text(song.songName, Modifier.padding(start = 8.dp))
                    }
                }
            }

            Button(
                onClick = {
                    if (playlistName.isNotBlank() && userId != null) {
                        scope.launch {
                            playlistViewModel.createPlaylist(
                                name = playlistName,
                                playListName = playlistName,
                                accesoId = 1L,
                                catId = 1L,
                                userId = userId,
                                songIds = selectedSongs
                            ) {

                                playlistName = ""
                                selectedSongs.clear()
                                showForm = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222))
            ) {
                Text("Save Playlist", color = Color.White)
            }

            Spacer(Modifier.height(12.dp))
        }

        if (myPlaylists.isEmpty() && followedPlaylists.isEmpty()) {
            Text("No playlists found", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(myPlaylists + followedPlaylists) { playlist ->
                    Card(modifier = Modifier.fillMaxWidth().clickable{ goPlaylistDetail(playlist.idPlaylist)}.padding(8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(playlist.playListName, color = Color.Black)
                            Text(
                                "Date: ${SimpleDateFormat("dd/MM/yyyy").format(playlist.creationDate)}",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

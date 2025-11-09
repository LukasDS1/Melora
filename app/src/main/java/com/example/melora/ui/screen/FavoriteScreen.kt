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
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PlayfairDisplay
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

        Text(
            text = "Your Favorites",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            fontFamily = Lato
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("You don't have favorites yet", color = Color.Gray)
            }
            Text("Don't have favorites yet", color = Color.Gray, fontFamily = PlayfairDisplay)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.heightIn(max = 220.dp)
            ) {
                items(favorites) { song ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { goPlayer(song.songId) }
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                    ) {
                        Text(song.songName, color = Color.Black, style = MaterialTheme.typography.titleMedium)
                        Text("by ${song.nickname}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        Column {
                            Text(song.songName, color = Color.Black, fontFamily = Lato)
                            Text("by ${song.nickname}", color = Color.Gray, fontFamily = Lato)
                        }
                    }
                    Divider(color = Color.Black, thickness = 1.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Playlists",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showForm = !showForm },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(if (showForm) "Cancel" else "Create new playlist", color = Color.White)
        }

        if (showForm) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text("Playlist name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text("Select songs to include:", color = Color.Black)

            LazyColumn(
                modifier = Modifier
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(allSongs) { song ->
                    val isSelected = song.songId in selectedSongs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isSelected) selectedSongs.remove(song.songId)
                                else selectedSongs.add(song.songId)
                            }
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isSelected, onCheckedChange = null)
                        Text(song.songName, Modifier.padding(start = 8.dp), color = Color.Black)
                    }
                    Divider(color = Color.Black, thickness = 1.dp)
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

            Spacer(modifier = Modifier.height(16.dp))
        }

        val combinedPlaylists = (myPlaylists + followedPlaylists).distinctBy { it.idPlaylist }

        if (combinedPlaylists.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No playlists found", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                items(combinedPlaylists) { playlist ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { goPlaylistDetail(playlist.idPlaylist) }
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = playlist.playListName,
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Created on ${
                                SimpleDateFormat("dd/MM/yyyy").format(playlist.creationDate)
                            }",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Divider(color = Color.Black, thickness = 1.dp)
                }
            }
        }
    }
}

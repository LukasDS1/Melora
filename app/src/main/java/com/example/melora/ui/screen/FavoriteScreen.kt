package com.example.melora.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.melora.data.remote.dto.PlaylistDto
import com.example.melora.data.remote.dto.SongDetailedDto
import com.example.melora.data.storage.UserPreferences
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.viewmodel.FavoriteApiViewModel
import com.example.melora.viewmodel.PlaylistApiViewModel
import com.example.melora.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.firstOrNull
import com.example.melora.R


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
    val scope = rememberCoroutineScope()

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
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Resaltado)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {

        // -------- FAVORITES ----------
        Text(
            text = "Your Favorites",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            fontFamily = Lato
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Don't have favorites yet",
                    color = Color.Black,
                    fontFamily = Lato,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            val context = LocalContext.current

            LazyColumn(
                modifier = Modifier.heightIn(max = 260.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(favorites) { song ->
                    Button(
                        onClick = { goPlayer(song.idSong) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Resaltado,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(4.dp, 8.dp, 16.dp, 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            val coverModel: Any? =
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
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = song.songName,
                                    color = Color.Black,
                                    fontFamily = Lato,
                                    fontSize = 16.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = "by ${song.nickname}",
                                    color = Color.Black,
                                    fontFamily = Lato,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // -------- PLAYLISTS ----------
        Text(
            text = "Your Playlists",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            fontFamily = Lato
        )
        Spacer(modifier = Modifier.height(8.dp))

        // FORMULARIO CREAR PLAYLIST
        if (showForm) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                placeholder = { Text("Playlist name", fontFamily = Lato) },
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = PrimaryBg,
                    unfocusedIndicatorColor = PrimaryBg
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.MusicVideo,
                        contentDescription = "Playlist icon",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text("Select songs to include:", color = Color.Black)
            Spacer(modifier = Modifier.height(6.dp))

            val context = LocalContext.current

            LazyColumn(
                modifier = Modifier
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        val coverModel: Any? =
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

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = song.songName,
                                color = Color.Black,
                                fontFamily = Lato,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "by ${song.nickname}",
                                color = Color.DarkGray,
                                fontFamily = Lato,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val uid = userId ?: return@Button
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
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg)
            ) {
                Text("Save Playlist", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        val combinedPlaylists = (myPlaylists + followedPlaylists)
            .distinctBy { it.idPlaylist }

        if (combinedPlaylists.isEmpty()) {
            if (!showForm) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No playlists found",
                        color = Color.Black,
                        fontFamily = Lato,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

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
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                items(combinedPlaylists.filterNotNull()) { playlist ->
                    val safeName = playlist.playlistName?.ifBlank { "Untitled playlist" } ?: "Untitled playlist"
                    val createdText = playlist.fechaCreacion ?: "Unknown date"

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { playlist.idPlaylist?.let(goPlaylistDetail) }
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = safeName,
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Created on $createdText",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall
                        )
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
}


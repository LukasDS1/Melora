package com.example.melora.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.melora.data.local.playlist.PlaylistEntity
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.storage.UserPreferences
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PlayfairDisplay
import com.example.melora.ui.theme.Resaltado
import com.example.melora.viewmodel.FavoriteViewModel
import com.example.melora.viewmodel.PlaylistViewModel
import com.example.melora.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import com.example.melora.R
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.ResaltadoNegative
import com.example.melora.ui.theme.SecondaryBg

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
//            searchViewModel.loadAllSongs()
        }
    }

    val favorites by favoriteViewModel.favorites.collectAsState()
    val myPlaylists by playlistViewModel.myPlaylists.collectAsState()
    val followedPlaylists by playlistViewModel.followedPlaylists.collectAsState()
    val allSongs by searchViewModel.songs.collectAsState()

//    FavoriteScreen(
//        favorites = favorites,
//        myPlaylists = myPlaylists,
//        followedPlaylists = followedPlaylists,
//       allSongs = allSongs,
//        playlistViewModel = playlistViewModel,
//        userId = userId,
//        goPlayer = goPlayer,
//        goPlaylistDetail = goPlaylistDetail
//    )
}

//@Composable
//fun FavoriteScreen(
//    favorites: List<SongDetailed>,
//    myPlaylists: List<PlaylistEntity>,
//    followedPlaylists: List<PlaylistEntity>,
//    allSongs: List<SongDetailed>,
//    playlistViewModel: PlaylistViewModel,
//    userId: Long?,
//    goPlayer: (Long) -> Unit,
//    goPlaylistDetail: (Long) -> Unit
//) {
//    val scope = rememberCoroutineScope()
//    var showForm by remember { mutableStateOf(false) }
//    var playlistName by remember { mutableStateOf("") }
//    val selectedSongs = remember { mutableStateListOf<Long>() }
//    val scrollState = rememberScrollState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Resaltado)
//            .padding(16.dp)
//            .verticalScroll(scrollState)
//    ) {
//
//        Text(
//            text = "Your Favorites",
//            style = MaterialTheme.typography.headlineSmall,
//            color = Color.Black,
//            fontFamily = Lato
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//
//        if (favorites.isEmpty()) {
//            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
//                Text(
//                    text ="Don't have favorites yet",
//                    color = Color.Black,
//                    fontFamily = Lato,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        } else {
//            val context = LocalContext.current
//
//            LazyColumn(
//                modifier = Modifier.heightIn(max = 260.dp),
//                verticalArrangement = Arrangement.spacedBy(1.dp)
//            ) {
//                items(favorites) { song ->
//                    Button(
//                        onClick = { goPlayer(song.songId) },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 2.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Resaltado,
//                            contentColor = Color.Black
//                        ),
//                        contentPadding = PaddingValues(4.dp, 8.dp, 16.dp, 8.dp),
//                        shape = RoundedCornerShape(12.dp)
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//
//                            AsyncImage(
//                                model = ImageRequest.Builder(context)
//                                    .data(song.coverArt ?: R.drawable.music_not_found_placeholder)
//                                    .crossfade(true)
//                                    .build(),
//                                contentDescription = "Cover Art",
//                                contentScale = ContentScale.Crop,
//                                modifier = Modifier
//                                    .size(44.dp)
//                                    .clip(RoundedCornerShape(4.dp))
//                            )
//
//                            Spacer(modifier = Modifier.width(12.dp))
//
//                            Column(
//                                verticalArrangement = Arrangement.Center,
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                Text(
//                                    text = song.songName,
//                                    color = Color.Black,
//                                    fontFamily = Lato,
//                                    fontSize = 16.sp,
//                                    maxLines = 1
//                                )
//                                Text(
//                                    text = "by ${song.nickname}",
//                                    color = Color.Black,
//                                    fontFamily = Lato,
//                                    fontSize = 14.sp,
//                                    maxLines = 1
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Text(
//            text = "Your Playlists",
//            style = MaterialTheme.typography.headlineSmall,
//            color = Color.Black,
//            fontFamily = Lato
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//
//        if (showForm) {
//            Spacer(modifier = Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = playlistName,
//                onValueChange = { playlistName = it },
//                placeholder = {
//                    Text("Playlist name", fontFamily = Lato)
//                },
//                singleLine = true,
//                shape = RoundedCornerShape(30.dp),
//                colors = TextFieldDefaults.colors(
//                    unfocusedIndicatorColor = SecondaryBg,
//                    focusedIndicatorColor = Resaltado,
//                    focusedContainerColor = Color.White,
//                    unfocusedContainerColor = Color.White
//                ),
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Filled.MusicVideo,
//                        contentDescription = "Playlist icon",
//                        tint = Color.Gray
//                    )
//                },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(10.dp))
//            Text("Select songs to include:", color = Color.Black)
//            Spacer(modifier = Modifier.height(6.dp))
//            LazyColumn(
//                modifier = Modifier
//                    .height(200.dp)
//                    .padding(vertical = 8.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(allSongs) { song ->
//                    val isSelected = song.songId in selectedSongs
//                    val context = LocalContext.current
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                if (isSelected) selectedSongs.remove(song.songId)
//                                else selectedSongs.add(song.songId)
//                            }
//                            .padding(6.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Checkbox(
//                            checked = isSelected,
//                            onCheckedChange = null
//                        )
//
//                        Spacer(modifier = Modifier.width(16.dp))
//
//                        AsyncImage(
//                            model = ImageRequest.Builder(context)
//                                .data(song.coverArt ?: R.drawable.music_not_found_placeholder)
//                                .crossfade(true)
//                                .build(),
//                            contentDescription = "Cover Art",
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier
//                                .size(48.dp)
//                                .clip(RoundedCornerShape(4.dp))
//                        )
//
//                        Spacer(modifier = Modifier.width(12.dp))
//
//                        Column(
//                            modifier = Modifier.weight(1f),
//                            verticalArrangement = Arrangement.Center
//                        ) {
//                            Text(
//                                text = song.songName,
//                                color = Color.Black,
//                                fontFamily = Lato,
//                                fontSize = 16.sp,
//                                maxLines = 1
//                            )
//                            Text(
//                                text = "by ${song.nickname}",
//                                color = Color.DarkGray,
//                                fontFamily = Lato,
//                                fontSize = 14.sp,
//                                maxLines = 1
//                            )
//                        }
//                    }
//                }
//            }
//
//
//
//            Button(
//                onClick = {
//                    val uid = userId ?: run {
//                        android.util.Log.e("FAVORITE_DEBUG", "userId is null, abort createPlaylist")
//                        return@Button
//                    }
//                    if (playlistName.isNotBlank()) {
//                        try {
//                            playlistViewModel.createPlaylist(
//                                playListName = playlistName,
//                                accesoId = 1L,
//                                catId = 1L,
//                                userId = uid,
//                                songIds = selectedSongs.toList()
//                            ) { newId ->
//                                android.util.Log.d("FAVORITE_DEBUG", "createPlaylist success newId=$newId")
//                                // refrescar explícitamente (aunque ViewModel ya lo hace)
//                                playlistViewModel.loadMyPlaylists(uid)
//                                playlistViewModel.loadFollowedPlaylists(uid)
//
//                                playlistName = ""
//                                selectedSongs.clear()
//                                showForm = false
//                            }
//                        } catch (e: Exception) {
//                            Log.e("FavoriteScreen", "createPlaylist failed", e)
//                        }
//                    } else {
//                        android.util.Log.w("FAVORITE_DEBUG", "playlistName blank -> not creating")
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg)
//            ) {
//                Text("Save Playlist", color = Color.White)
//            }
//
//
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//
//        // --- reemplaza el LazyColumn que mostraba combinedPlaylists por ESTE bloque ---
//        val combinedPlaylists = (myPlaylists + followedPlaylists).distinctBy { it.idPlaylist }
//
//        if (combinedPlaylists.isEmpty()) {
//            if (!showForm) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 12.dp, bottom = 24.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "No playlists found",
//                        color = Color.Black,
//                        fontFamily = Lato,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//
//            Button(
//                onClick = { showForm = !showForm },
//                modifier = Modifier.fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(containerColor = ResaltadoNegative)
//            ) {
//                Text(
//                    if (showForm) "Cancel" else "Create new playlist",
//                    color = Color.White
//                )
//            }
//        } else {
//            // Lista segura: cada item protegido contra exceptions
//            LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
//                items(combinedPlaylists) { playlist ->
//                    // protección: coger valores seguros y evitar NPEs o formato erróneo
//                    val safeName = runCatching { playlist.playListName ?: playlist.toString() }
//                        .getOrElse { "Untitled playlist" }
//
//                    val createdText = runCatching {
//                        // si creationDate está fuera de rango o viene corrupto, lo captura
//                        val created = playlist.creationDate
//                        // si por alguna razón creationDate es 0 o negativo, mostrar Unknown
//                        if (created <= 0L) "Unknown date"
//                        else SimpleDateFormat("dd/MM/yyyy").format(created)
//                    }.getOrElse {
//                        it.printStackTrace()
//                        "Unknown date"
//                    }
//
//                    // LOG para depuración (temporal)
//                    android.util.Log.d("FAVORITE_DEBUG", "Playlist item: id=${playlist.idPlaylist} name=$safeName creation=$createdText")
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                try {
//                                    goPlaylistDetail(playlist.idPlaylist)
//                                } catch (e: Exception) {
//                                    android.util.Log.e("FAVORITE_DEBUG", "error navigating to playlistDetail", e)
//                                }
//                            }
//                            .padding(vertical = 10.dp, horizontal = 8.dp)
//                    ) {
//                        Text(
//                            text = safeName,
//                            color = Color.Black,
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                        Text(
//                            text = "Created on $createdText",
//                            color = Color.Gray,
//                            style = MaterialTheme.typography.labelSmall
//                        )
//                    }
//                    // divider left intentionally out (or put one if you prefer)
//                }
//            }
//
//            Spacer(Modifier.height(16.dp))
//            Button(
//                onClick = { showForm = !showForm },
//                modifier = Modifier.fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
//            ) {
//                Text(
//                    if (showForm) "Cancel" else "Create new playlist",
//                    color = Color.White
//                )
//            }
//        }
//
//    }
//}

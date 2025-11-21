package com.example.melora.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.users.UserEntity
import com.example.melora.R
import com.example.melora.data.local.playlist.PlaylistEntity
import com.example.melora.data.remote.dto.ArtistProfileData
import com.example.melora.data.remote.dto.PlaylistDto
import com.example.melora.data.remote.dto.SongDetailedDto
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeloraSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<SongDetailedDto>,
    artistResult: List<ArtistProfileData>,
    playlistResults: List<PlaylistDto>,
    modifier: Modifier = Modifier,
    goArtistProfile: (Long) -> Unit,
    goPlayer: (Long) -> Unit,
    goPlaylist: (Long) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter),
            colors = SearchBarDefaults.colors(
                containerColor = Color.White,
                dividerColor = Color.Transparent,
            ),
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { new ->
                        textFieldState.edit { replace(0, length, new) }
                        onSearch(new)
                    },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search") }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                items(artistResult) { artist ->
                    ArtistItem(artist = artist, goArtistProfile = goArtistProfile)
                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(searchResults) { song ->
                    SongItem(song = song, goPlayer = goPlayer)
                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(playlistResults) { playlist ->
                    PlaylistItem(playlist = playlist, goPlaylist = goPlaylist)
                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistItem(playlist: PlaylistDto, goPlaylist: (Long) -> Unit) {

    Button(
        onClick = { goPlaylist(playlist.idPlaylist) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                playlist.playlistName,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            Text(
                text = "Created on ${playlist.fechaCreacion ?: "Unknown"}",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ArtistItem(
    artist: ArtistProfileData,
    goArtistProfile: (Long) -> Unit
) {
    val context = LocalContext.current

    // Decodificar Base64
    val decodedProfile = artist.profilePhotoBase64?.let {
        runCatching { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }
            .getOrNull()
    }

    Button(
        onClick = { goArtistProfile(artist.idUser) },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nombre del artista pegado a la izquierda
            Text(
                text = artist.nickname,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Foto pegada a la DERECHA
            AsyncImage(
                model = decodedProfile ?: R.drawable.defaultprofilepicture,
                contentDescription = "Artist photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)               // pequeño
                    .clip(RoundedCornerShape(8.dp)) // cuadrado con bordes
            )
        }
    }
}



@Composable
fun SongItem(song: SongDetailedDto, goPlayer: (Long) -> Unit) {

    val decodedCover = song.coverArtBase64?.let {
        runCatching { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }.getOrNull()
    }

    Button(
        onClick = { goPlayer(song.idSong) },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            AsyncImage(
                model = decodedCover ?: R.drawable.defaultprofilepicture,
                contentDescription = "Cover",
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(song.songName, color = Color.Black)
                Text(song.nickname ?: "Unknown", color = Color.Gray)
            }
        }
    }
}





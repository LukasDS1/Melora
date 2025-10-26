package com.example.melora.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Divider
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
import java.io.File
import com.example.melora.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeloraSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<SongDetailed>,
    artistResult: List<UserEntity>,
    modifier: Modifier = Modifier,
    goArtistProfile: (Long) -> Unit,
    goPlayer: (Long) -> Unit
    ) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter),
            colors = SearchBarDefaults.colors(
                containerColor = Color.White,
                dividerColor = Color.Transparent,
            ),
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { new -> textFieldState.edit { replace(0, length, new) }
                    onSearch(new)},
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
                        items(artistResult){ artist ->
                            ArtistItem(artist = artist,goArtistProfile = goArtistProfile)
                            HorizontalDivider(
                                color = Color.LightGray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(searchResults) { song ->
                            SongItem(song = song,goPlayer = goPlayer)
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
fun ArtistItem(artist: UserEntity,goArtistProfile: (Long) -> Unit){
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Button(
            onClick = { goArtistProfile(artist.idUser) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Foto de perfil del artista
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(artist.profilePicture ?: R.drawable.defaultprofilepicture)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Artist photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)       // tamaño uniforme
                        .clip(CircleShape) // círculo
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Nombre del artista
                Text(
                    text = artist.nickname,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    maxLines = 1
                )
            }
        }
    }
}
@Composable
fun SongItem(song: SongDetailed, goPlayer: (Long) -> Unit) {
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }

    val context = LocalContext.current

    Button(
        onClick = { goPlayer(song.songId) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Portada
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(song.coverArt ?: R.drawable.defaultprofilepicture)
                    .crossfade(true)
                    .build(),
                contentDescription = "Cover Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información de la canción
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.songName,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                // Artista
                Text(
                    text = song.nickname,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                // Duración
                Text(
                    text = formatTime(song.durationSong),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
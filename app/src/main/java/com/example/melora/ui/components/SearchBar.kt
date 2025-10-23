package com.example.melora.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.users.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<SongDetailed>,
    artistResult: List<UserEntity>,
    modifier: Modifier = Modifier,
    goArtistProfile: (Long) -> Unit,
    goPlayer: (Long) -> Unit
    ) {
    // Controls expansion state of the search bar
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
                        }
                        items(searchResults) { song ->
                            SongItem(song = song,goPlayer = goPlayer)
                        }

                }
            }
        }
    }



@Composable
fun ArtistItem(artist: UserEntity,goArtistProfile: (Long) -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(onClick = {goArtistProfile(artist.idUser)},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black)
        ){ Column (modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center){
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = artist.nickname,
                color = Color.Black
            )
        }
        }
    }
}
@Composable
fun SongItem(song: SongDetailed, goPlayer: (Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(
            onClick = { goPlayer(song.songId) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.songName,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.nickname,
                    color = Color.Gray
                )
            }
        }
    }
}
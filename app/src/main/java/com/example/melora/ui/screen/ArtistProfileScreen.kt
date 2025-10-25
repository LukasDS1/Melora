package com.example.melora.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.ui.theme.Resaltado
import com.example.melora.viewmodel.ArtistProfileViewModel
import com.example.melora.viewmodel.ArtistProfileViewModelFactory
import com.example.melora.R

@Composable
fun ArtistProfileScreen(
    artistId: Long,
    repository: ArtistRepository,
    goPlayer: (Long) -> Unit
) {
    val vm: ArtistProfileViewModel = viewModel(
        factory = ArtistProfileViewModelFactory(repository)
    )

    val artistData = vm.artistData

    LaunchedEffect(artistId) {
        vm.loadArtist(artistId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Resaltado)
            .padding(16.dp)
    ) {
        if (artistData != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (artistData.artist.profilePicture != null) {
                    Image(
                        painter = rememberAsyncImagePainter(artistData.artist.profilePicture),
                        contentDescription = "Foto del artista",
                        modifier = Modifier.size(120.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.defaultprofilepicture),
                        contentDescription = "Default picture",
                        modifier = Modifier.size(120.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = artistData.artist.nickname,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Songs:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    artistData.songs.forEach { song ->
                        Button(
                            onClick = { goPlayer(song.songId) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Resaltado,
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = song.songName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            } } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                Text("Not songs Upload Yet", color = Color.White)
            }
        }
    }
}



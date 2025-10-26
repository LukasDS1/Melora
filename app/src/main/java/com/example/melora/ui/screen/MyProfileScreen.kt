package com.example.melora.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.melora.R
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.data.storage.UserPreferences
import com.example.melora.ui.theme.Resaltado
import com.example.melora.viewmodel.ArtistProfileViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun MyProfileScreenVm(
    vm: ArtistProfileViewModel,
    goPlayer: (Long) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    var nickname by remember { mutableStateOf<String?>(null) }
    var profilePicture by remember { mutableStateOf<String?>(null) }
    var songs by remember { mutableStateOf<List<SongDetailed>>(emptyList()) }

    LaunchedEffect(Unit) {
        val id = prefs.userId.firstOrNull()
        if (id != null) {
            vm.loadArtist(id)
        }
    }

    val artistData = vm.artistData

    LaunchedEffect(artistData) {
        artistData?.let {
            nickname = it.artist.nickname
            profilePicture = it.artist.profilePicture
            songs = it.songs
        }
    }

    MyProfileScreen(
        nickname = nickname,
        profilePicture = profilePicture,
        songs = songs,
        goPlayer = goPlayer
    )
}

@Composable
fun MyProfileScreen(
    nickname: String?,
    profilePicture: String?,
    songs: List<SongDetailed>,
    goPlayer: (Long) -> Unit
) {
    val bg = Resaltado

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = if (profilePicture != null)
                    rememberAsyncImagePainter(profilePicture)
                else
                    painterResource(R.drawable.defaultprofilepicture),
                contentDescription = "Profile picture",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = nickname ?: "Unknown Artist",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Songs:",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                songs.forEach { song ->
                    Button(
                        onClick = { goPlayer(song.songId) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = bg,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = song.songName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

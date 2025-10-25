package com.example.melora.ui.screen

import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.repository.FavoriteRepository
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.viewmodel.FavoriteViewModel
import com.example.melora.viewmodel.FavoriteViewModelFactory


@Composable
fun FavoriteScreenVm(
    userId: Long,
    favoriteViewModel: FavoriteViewModel,
    goPlayer: (Long) -> Unit
){

    LaunchedEffect(userId) {
        favoriteViewModel.loadFavorite(userId)
    }

    FavoriteScreen(
        userId = userId,
        favoriteViewModel = favoriteViewModel,
        goPlayer = goPlayer
    )

}

@Composable
fun FavoriteScreen(
    userId: Long,
    favoriteViewModel: FavoriteViewModel,
    goPlayer: (Long) -> Unit
) {
    val favorites by favoriteViewModel.favorites.collectAsState()

    LaunchedEffect(userId) {
        favoriteViewModel.loadFavorite(userId)
    }

    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(PrimaryBg),
            contentAlignment = Alignment.Center
        ) {
            Text("No favorites yet", color = Color.Gray)
        }
    } else {
        Text("Your Favorites", style = MaterialTheme.typography.headlineLarge, color = Color.Black)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize().background(PrimaryBg),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favorites) { song ->
                Button(
                    onClick = { goPlayer(song.songId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = song.songName,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "by ${song.nickname}",
                            color = Color.Black,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

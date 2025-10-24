package com.example.melora.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.viewmodel.FavoriteViewModel

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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No favorites yet", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favorites) { song ->
                Button(
                    onClick = { goPlayer(song.songId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = song.songName,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "by ${song.nickname}",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

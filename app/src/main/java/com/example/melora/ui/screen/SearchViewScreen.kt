package com.example.melora.ui.screen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.melora.viewmodel.SearchViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.util.query
import com.example.melora.ui.components.MeloraSearchBar
import com.example.melora.ui.theme.Resaltado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchViewScreen(
    vm: SearchViewModel,
    goArtistProfile: (Long) -> Unit,
    goPlayer: (Long) -> Unit){
    val textState = remember { TextFieldState() }
    val onSearch: (String) -> Unit = { query -> vm.search(query) }
    val bg = (Resaltado)
    val songs by vm.songs.collectAsStateWithLifecycle()
    val artists by vm.artists.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(bg)
    ){
        MeloraSearchBar(textFieldState = textState,onSearch,songs, artistResult =artists , goArtistProfile = goArtistProfile, goPlayer = goPlayer)
    }



}


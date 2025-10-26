package com.example.melora

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.data.repository.FavoriteRepository
import com.example.melora.data.repository.PlayListRepository
import com.example.melora.data.repository.PlayListUserRepository
import com.example.melora.data.repository.SongRepository
import com.example.melora.data.repository.UploadRepository
import com.example.melora.data.repository.UserRepository
import com.example.melora.data.storage.UserPreferences
import com.example.melora.navigation.AppNavGraph
import com.example.melora.ui.system.ApplySystemBars
import com.example.melora.viewmodel.ArtistProfileViewModel
import com.example.melora.viewmodel.ArtistProfileViewModelFactory
import com.example.melora.viewmodel.AuthViewModel
import com.example.melora.viewmodel.AuthViewModelFactory
import com.example.melora.viewmodel.BanViewModel
import com.example.melora.viewmodel.BanviewModelFactory
import com.example.melora.viewmodel.EditProfileViewModel
import com.example.melora.viewmodel.EditProfileViewModelFactory
import com.example.melora.viewmodel.FavoriteViewModel
import com.example.melora.viewmodel.FavoriteViewModelFactory
import com.example.melora.viewmodel.MusicPlayerViewModel
import com.example.melora.viewmodel.MusicPlayerViewModelFactory
import com.example.melora.viewmodel.PlaylistViewModel
import com.example.melora.viewmodel.PlaylistViewModelFactory
import com.example.melora.viewmodel.SearchViewModel
import com.example.melora.viewmodel.SearchViewModelFactory
import com.example.melora.viewmodel.UploadViewModel
import com.example.melora.viewmodel.UploadViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(), Color.Transparent.toArgb()
            ),
        )
        setContent {
           AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = MeloraDB.getInstance(context)

    val songDao = db.songDao()

    val uploadDao = db.uploadDao()

    val  userDao = db.userDao()

    val estadoDao = db.estadoDao()

    val rolDao = db.rolDao()
    val favoriteDao = db.favoriteDao()
    val playListDao = db.PlaylistDao()
    val playListUsersDao = db.playListUsersDao()

    val playlistUserRepository = PlayListUserRepository(playListUsersDao)
    val playlistRepository = PlayListRepository(playListDao,playListUsersDao)
    val songRepository = SongRepository(songDao)

    val userRepository = UserRepository(userDao,rolDao,estadoDao)

    val uploadRepository = UploadRepository(uploadDao)

    val prefs = UserPreferences(context)

    val loginRepository = UserRepository(userDao,rolDao,estadoDao)

    val artistRepository = ArtistRepository(userDao,songDao)

    val favoriteRepository = FavoriteRepository(favoriteDao,userDao,songDao)

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(UserRepository(userDao,rolDao,estadoDao), application)
    )

    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val currentUserId = currentUser?.idUser

    val userPreferences = UserPreferences(context)

    val editProfileViewModel: EditProfileViewModel = viewModel(
        factory = EditProfileViewModelFactory(userRepository, userPreferences)
    )

    val playlistViewModel: PlaylistViewModel = viewModel(
        factory = PlaylistViewModelFactory(playlistRepository,playlistUserRepository)
    )

    val uploadViewModel: UploadViewModel = viewModel(
        factory = UploadViewModelFactory(songRepository,uploadRepository)
    )
    val artistProfileViewModel: ArtistProfileViewModel = viewModel(
        factory = ArtistProfileViewModelFactory(artistRepository,songRepository)
    )

    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(songRepository,userRepository,playlistRepository)
    )
    val musicPlayerViewModel : MusicPlayerViewModel = viewModel (
        factory = MusicPlayerViewModelFactory(application,songRepository)
    )
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(favoriteRepository,prefs)
    )
    val banViewModel: BanViewModel = viewModel(
        factory = BanviewModelFactory(uploadRepository, application)
    )
    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                uploadViewModel = uploadViewModel,
                searchViewModel = searchViewModel,
                authViewModel =  authViewModel,
                artistModel = artistProfileViewModel,
                favoriteModel = favoriteViewModel,
                musicPlayerViewModel = musicPlayerViewModel,
                banViewModel = banViewModel,
                editProfileViewModel = editProfileViewModel,
                playlistViewModel =playlistViewModel
            )
        }
    }
    ApplySystemBars(navController)
}
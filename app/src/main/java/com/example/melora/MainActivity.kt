package com.example.melora
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.data.repository.SongRepository
import com.example.melora.data.repository.UploadRepository
import com.example.melora.data.repository.UserRepository
import com.example.melora.navigation.AppNavGraph
import com.example.melora.viewmodel.ArtistProfileViewModel
import com.example.melora.viewmodel.ArtistProfileViewModelFactory
import com.example.melora.viewmodel.AuthViewModel
import com.example.melora.viewmodel.AuthViewModelFactory
import com.example.melora.viewmodel.MusicPlayerViewModel
import com.example.melora.viewmodel.MusicPlayerViewModelFactory
import com.example.melora.viewmodel.SearchViewModel
import com.example.melora.viewmodel.SearchViewModelFactory
import com.example.melora.viewmodel.UploadViewModel
import com.example.melora.viewmodel.UploadViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

    val songRepository = SongRepository(songDao)

    val userRepository = UserRepository(userDao)

    val uploadRepository = UploadRepository(uploadDao)

    val loginRepository = UserRepository(userDao)

    val artistRepository = ArtistRepository(userDao,songDao)

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(loginRepository)
    )

    val uploadViewModel: UploadViewModel = viewModel(
        factory = UploadViewModelFactory(songRepository,uploadRepository)
    )
    val artistProfileViewModel: ArtistProfileViewModel = viewModel(
        factory = ArtistProfileViewModelFactory(artistRepository)
    )

    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(songRepository,userRepository)
    )
    val musicPlayerViewModel: MusicPlayerViewModel = viewModel (
        factory = MusicPlayerViewModelFactory(application,songRepository)
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
                musicModel = musicPlayerViewModel,
            )
        }
    }
}
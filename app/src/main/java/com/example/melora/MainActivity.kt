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
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.melora.data.remote.FavoriteRemoteModule
import com.example.melora.data.remote.LoginRemoteModule
import com.example.melora.data.remote.PlaylistRemoteModule
import com.example.melora.data.remote.RecoverPassRemoteModule
import com.example.melora.data.remote.RegisterRemoteModule
import com.example.melora.data.remote.SongRemoteModule
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.data.repository.BanApiRepository
import com.example.melora.data.repository.FavoriteApiRepository
import com.example.melora.data.repository.LoginApiRepository
import com.example.melora.data.repository.PlaylistApiRepository
import com.example.melora.data.repository.RecoverPassApiRepository
import com.example.melora.data.repository.RegisterApiRepository
import com.example.melora.data.repository.SongApiRepository
import com.example.melora.data.repository.UploadApiRepository
import com.example.melora.data.repository.UserArtistApiPublicRepository
import com.example.melora.data.storage.UserPreferences
import com.example.melora.navigation.AppNavGraph
import com.example.melora.ui.system.ApplySystemBars
import com.example.melora.ui.theme.MeloraTheme
import com.example.melora.viewmodel.ArtistProfileViewModel
import com.example.melora.viewmodel.ArtistProfileViewModelFactory
import com.example.melora.viewmodel.BanViewModel
import com.example.melora.viewmodel.BanviewModelFactory
import com.example.melora.viewmodel.EditProfileViewModel
import com.example.melora.viewmodel.EditProfileViewModelFactory
import com.example.melora.viewmodel.FavoriteApiViewModel
import com.example.melora.viewmodel.FavoriteViewModelFactory
import com.example.melora.viewmodel.HomeScreenApiViewModel
import com.example.melora.viewmodel.HomeScreenViewModelFactory
import com.example.melora.viewmodel.LoginApiViewModel
import com.example.melora.viewmodel.LoginApiViewModelFactory
import com.example.melora.viewmodel.MusicPlayerViewModel
import com.example.melora.viewmodel.MusicPlayerViewModelFactory
import com.example.melora.viewmodel.RecoverPassViewModel
import com.example.melora.viewmodel.RecoverPassViewModelFactory
import com.example.melora.viewmodel.RegisterApiViewModel
import com.example.melora.viewmodel.ResetPasswordViewModel
import com.example.melora.viewmodel.ResetPasswordViewModelFactory
import com.example.melora.viewmodel.PlaylistApiViewModel
import com.example.melora.viewmodel.PlaylistApiViewModelFactory
import com.example.melora.viewmodel.RegisterApiViewModelFactory
import com.example.melora.viewmodel.SearchViewModel
import com.example.melora.viewmodel.SearchViewModelFactory
import com.example.melora.viewmodel.UploadApiViewModel
import com.example.melora.viewmodel.UploadApiViewModelFactory
import com.example.melora.viewmodel.UserArtistApiPublicViewModel
import com.example.melora.viewmodel.UserArtistApiPublicViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(), Color.Transparent.toArgb()
            ),
        )
        setContent {
            MeloraTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val registerApiRepository = RegisterApiRepository()

    val songApi = SongRemoteModule.api()
    val userArtistApiPublicRepository = UserArtistApiPublicRepository(songApi,registerApiRepository)

    val prefs = UserPreferences(context)


    val artistRepository = ArtistRepository(songApi, prefs,registerApiRepository )


    val songApiRepository = SongApiRepository()

    val banApiRepository = BanApiRepository()

    val registerApi = RegisterRemoteModule.api()

    val loginApi = LoginRemoteModule.api()

    val playListApi = PlaylistRemoteModule.api()
    val favoriteApi = FavoriteRemoteModule.api()
    val recoverPassApi = RecoverPassRemoteModule.api()


    val loginApiViewModel: LoginApiViewModel = viewModel(factory = LoginApiViewModelFactory(repository = LoginApiRepository(),
        prefs = prefs
        )
    )
    val recoverPassApiRepository  = RecoverPassApiRepository(recoverPassApi)

    val playListRepository = PlaylistApiRepository(playListApi)


    val favoriteApiRepository = FavoriteApiRepository(favoriteApi)

    val userPreferences = UserPreferences(context)

    val editProfileViewModel: EditProfileViewModel = viewModel(
        factory = EditProfileViewModelFactory(registerApi , loginApi,userPreferences)
    )

    val homeScreenApiViewModel: HomeScreenApiViewModel = viewModel(
        factory = HomeScreenViewModelFactory(songApiRepository)
    )

    val recoverPassViewModel: RecoverPassViewModel = viewModel(
        factory = RecoverPassViewModelFactory(recoverPassApiRepository)
    )

    val resetPasswordViewModel: ResetPasswordViewModel = viewModel(
        factory = ResetPasswordViewModelFactory(application)
    )
    val playlistViewModel: PlaylistApiViewModel = viewModel(
        factory = PlaylistApiViewModelFactory(playListRepository)
    )

    val userArtistApiPublicViewModel: UserArtistApiPublicViewModel = viewModel(
        factory = UserArtistApiPublicViewModelFactory(UserArtistApiPublicRepository(songApi,registerApiRepository))
    )

    val artistProfileViewModel: ArtistProfileViewModel = viewModel(
        factory = ArtistProfileViewModelFactory(artistRepository, songApiRepository  )
    )

    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(songApiRepository,playListRepository,registerApiRepository)
    )
    val musicPlayerViewModel : MusicPlayerViewModel = viewModel (
        factory = MusicPlayerViewModelFactory(application,songApiRepository)
    )
    val favoriteViewModel: FavoriteApiViewModel = viewModel(
        factory = FavoriteViewModelFactory(favoriteApiRepository,prefs)
    )
    val banViewModel: BanViewModel = viewModel(
        factory = BanviewModelFactory(banApiRepository, application)
    )

    val registerApiViewModel: RegisterApiViewModel = viewModel(
        factory = RegisterApiViewModelFactory(RegisterApiRepository())
    )

    val uploadApiViewModel: UploadApiViewModel = viewModel(
        factory = UploadApiViewModelFactory(UploadApiRepository())
    )

    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                searchViewModel = searchViewModel,
                artistModel = artistProfileViewModel,
                favoriteModel = favoriteViewModel,
                musicPlayerViewModel = musicPlayerViewModel,
                banViewModel = banViewModel,
                editProfileViewModel = editProfileViewModel,
                playlistApiViewModel =playlistViewModel,
                homeScreenApiViewModel = homeScreenApiViewModel,
                registerApiViewModel = registerApiViewModel,
                loginApiViewModel = loginApiViewModel,
                uploadApiViewModel = uploadApiViewModel,
                recoverPassViewModel = recoverPassViewModel,
                resetPasswordViewModel = resetPasswordViewModel,
                userArtistApiPublicViewModel = userArtistApiPublicViewModel
            )
        }
    }
    ApplySystemBars(navController)
}
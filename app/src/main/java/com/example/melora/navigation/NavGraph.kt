package com.example.melora.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.ui.components.*
import com.example.melora.ui.screen.*
import com.example.melora.viewmodel.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    uploadViewModel: UploadViewModel,
    searchViewModel: SearchViewModel,
    authViewModel: AuthViewModel,
    artistModel: ArtistProfileViewModel,
    musicPlayerViewModel: MusicPlayerViewModel,
    favoriteModel: FavoriteViewModel,
    banViewModel : BanViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val roleId by authViewModel.currentRoleId.collectAsStateWithLifecycle()

    val startDestination = if (currentUser != null) Route.Home.path else Route.Login.path

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = androidx.compose.ui.platform.LocalContext.current
    val db = MeloraDB.getInstance(context)
    val artistRepository = ArtistRepository(userDao = db.userDao(), songDao = db.songDao())

    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) { popUpTo(0) } }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) { popUpTo(0) } }
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) { popUpTo(0) } }
    val goUpload: () -> Unit = { navController.navigate(Route.UploadScreenForm.path) }
    val goSucces: () -> Unit = { navController.navigate(Route.SuccesUpload.path) }
    val goSearch: () -> Unit = { navController.navigate(Route.SearchView.path) }
    val goArtistProfile: (Long) -> Unit = { id -> navController.navigate("artistProfile/$id") }
    val goPlayer: (Long) -> Unit = { id -> navController.navigate("player/$id") }
    val goFavorites: () -> Unit = { navController.navigate(Route.Favorites.path) }

    Scaffold(
        topBar = {
            if (currentRoute !in listOf(Route.Login.path, Route.Register.path, Route.Player.path)) {
                AppTopBar(
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister
                )
            }
        },
        bottomBar = {
            if (currentRoute !in listOf(Route.Login.path, Route.Register.path)) {
                AppNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Login.path) {
                LoginScreenVm(
                    vm = authViewModel,
                    onLoginOk = goHome,
                    onGoRegister = goRegister
                )
            }

            composable(Route.Register.path) {
                RegisterScreenVm(
                    vm = authViewModel,
                    onGoLogin = goLogin,
                    onRegistered = goLogin
                )
            }

            composable(Route.Home.path) {
                HomeScreen(
                    onGoLogin = goLogin,
                    onGoRegister = goRegister,
                    onGoUpload = goUpload
                )
            }

            composable(Route.UploadScreenForm.path) {
                val user = authViewModel.currentUser.collectAsStateWithLifecycle().value
                if (user != null) {
                    UploadScreenVm(
                        vm = uploadViewModel,
                        onGoSucces = goSucces,
                        userId = user.idUser
                    )
                } else {
                    goLogin()
                }
            }

            composable(Route.SuccesUpload.path) {
                SuccesUpload(
                    onLoginOk = goLogin,
                    onGoUpload = goUpload
                )
            }

            composable(Route.SearchView.path) {
                SearchViewScreen(
                    vm = searchViewModel,
                    goArtistProfile = goArtistProfile,
                    goPlayer = goPlayer
                )
            }

            composable("artistProfile/{artistId}") {
                val artistId = it.arguments?.getString("artistId")?.toLongOrNull() ?: 0L
                ArtistProfileScreen(
                    artistId = artistId,
                    repository = artistRepository,
                    goPlayer = goPlayer
                )
            }

            composable(
                route = "player/{songId}",
                arguments = listOf(navArgument("songId") { type = NavType.LongType })
            ) { backStackEntry ->
                val songId = backStackEntry.arguments?.getLong("songId") ?: return@composable

                LaunchedEffect(songId) {
                    musicPlayerViewModel.getSongDetails(songId)
                }
                val roleId by authViewModel.currentRoleId.collectAsStateWithLifecycle()
                val currentSong by musicPlayerViewModel.currentSong.collectAsStateWithLifecycle()
                currentSong?.let {
                    PlayerScreenVm(
                        songId = songId,
                        vm = musicPlayerViewModel,
                        onExitPlayer = goSearch,
                        favVm = favoriteModel,
                        roleId = roleId,
                        banVm = banViewModel
                    )
                }
            }

            composable(Route.Favorites.path) {
                FavoriteScreenVm(
                    favoriteViewModel = favoriteModel,
                    goPlayer = goPlayer
                )
            }
        }
    }
}

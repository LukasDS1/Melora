package com.example.melora.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
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
    banViewModel: BanViewModel
) {

    val startDestination by rememberSaveable { mutableStateOf(if (authViewModel.currentUser.value != null) Route.Home.path else Route.Login.path)
    }

    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val roleId by authViewModel.currentRoleId.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val goHome = {
        navController.navigate(Route.Home.path) {
            popUpTo(Route.Login.path) { inclusive = true }
            launchSingleTop = true
        }
    }

    val goSearch = {
        navController.navigate(Route.SearchView.path) { popUpTo(Route.Home.path)
            launchSingleTop = true
        }
    }

    val goFavorites = {
        navController.navigate(Route.Favorites.path) { popUpTo(Route.Home.path)
            launchSingleTop = true
        }
    }

    val goUpload = {
        navController.navigate(Route.UploadScreenForm.path) { launchSingleTop = true }
    }

    val goSucces = {
        navController.navigate(Route.SuccesUpload.path) { launchSingleTop = true }
    }

    val goArtistProfile: (Long) -> Unit = { id ->
        navController.navigate("artistProfile/$id") { launchSingleTop = true }
    }

    val goPlayer: (Long) -> Unit = { id -> navController.navigate("player/$id") { launchSingleTop = true } }

    val goLogin = {
        navController.navigate(Route.Login.path) {popUpTo(Route.Home.path) { inclusive = true }
            launchSingleTop = true }
    }

    val goRegister = { navController.navigate(Route.Register.path) { popUpTo(Route.Login.path) { inclusive = false }
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = {
            if (currentRoute !in listOf(
                    Route.Login.path,
                    Route.Register.path,
                    Route.Player.path
                )
            ) {
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
                val user = currentUser
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

            composable(
                route = "artistProfile/{artistId}",
                arguments = listOf(navArgument("artistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val artistId = backStackEntry.arguments?.getLong("artistId") ?: 0L
                ArtistProfileScreenVm(
                    artistId = artistId,
                    vm = artistModel,
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

                val currentSong by musicPlayerViewModel.currentSong.collectAsStateWithLifecycle()

                currentSong?.let {
                    PlayerScreenVm(
                        songId = songId,
                        vm = musicPlayerViewModel,
                        onExitPlayer = { navController.popBackStack() },
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

            composable("profile") {
                MyProfileScreenVm(
                    vm = artistModel,
                    goPlayer = { songId ->
                        navController.navigate("player/$songId") {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

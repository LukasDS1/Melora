package com.example.melora.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.melora.ui.components.*
import com.example.melora.ui.screen.*
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.ui.theme.SecondaryBg
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
    banViewModel: BanViewModel,
    editProfileViewModel: EditProfileViewModel,
    playlistViewModel: PlaylistViewModel,
    homeScreenViewModel: HomeScreenViewModel
) {


    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val roleId by authViewModel.currentRoleId.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)


    val startDestination = when {
        currentUser != null && isLoggedIn -> Route.Home.path
        else -> Route.Login.path
    }

    val goHome = {
        navController.navigate(Route.Home.path) {
            popUpTo(Route.Home.path) { inclusive = false }
            launchSingleTop = true
        }
    }

    val goSearch = {
        navController.navigate(Route.SearchView.path) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val goPlaylist: (Long) -> Unit =
        { playlistId -> navController.navigate("playlist/$playlistId") { launchSingleTop = true } }

    val goFavorites = {
        navController.navigate(Route.Favorites.path) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }


    val goUpload = {
        navController.navigate(Route.UploadScreenForm.path) { launchSingleTop = true }
    }

    val goEditProfile: () -> Unit =
        { navController.navigate(Route.editProfile.path) { launchSingleTop = true } }


    val goSucces: () -> Unit = {
        navController.navigate(Route.SuccesUpload.path) { launchSingleTop = true }
    }


    val goArtistProfile: (Long?) -> Unit = { id ->
        navController.navigate("artistProfile/$id") { launchSingleTop = true }
    }

    val goPlayer: (Long) -> Unit =
        { id -> navController.navigate("player/$id") { launchSingleTop = true } }

    val goLogin = {
        navController.navigate(Route.Login.path) {
            popUpTo(Route.Home.path) { inclusive = true }
            launchSingleTop = true
        }
    }

    val goRegister: () -> Unit = {
        navController.navigate(Route.Register.path) {
            popUpTo(Route.Login.path) { inclusive = false }
            launchSingleTop = true
        }
    }

    val goMyProfile: () -> Unit = {
        navController.navigate(Route.MyProfile.path) { launchSingleTop = true }
    }

    val hideBars = currentRoute in listOf(
        Route.Login.path,
        Route.Register.path,
        Route.UploadScreenForm.path,
        Route.editProfile.path,
        Route.Player.path
    )

    Scaffold(
        containerColor = Resaltado,
        topBar = {
            if (!hideBars) {
                AppTopBar(
                    onUpload = goUpload,
                    profileImageUrl = currentUser?.profilePicture,
                    onMyProfile = goMyProfile
                )
            }
        },
        bottomBar = {
            if (!hideBars) {
                AppNavigationBar(
                    navController = navController,
                    onGoHome = goHome,
                    onGoSearch = goSearch,
                    onGoFavorites = goFavorites
                )
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
                HomeScreenVm(
                    vm = homeScreenViewModel,
                    goPlayer = { songId ->
                        navController.navigate("player/$songId") { launchSingleTop = true }
                    }
                )
            }
            composable(Route.UploadScreenForm.path) {
                val user = currentUser
                if (user != null) {
                    UploadScreenVm(
                        vm = uploadViewModel,
                        onGoSucces = goSucces,
                        userId = user.idUser,
                        goHome = goHome
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
                    goPlayer = goPlayer,
                    goPlaylist = goPlaylist
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
                    goPlayer = goPlayer,
                    playlistViewModel = playlistViewModel,
                    searchViewModel = searchViewModel,
                    goPlaylistDetail = goPlaylist
                )
            }

            composable(Route.editProfile.path) {
                EditProfileScreenVm(
                    vm = editProfileViewModel,
                    onExit = goMyProfile,
                    onProfileUpdated = { navController.popBackStack() }, // Go back to home if success
                    onLogout = goLogin
                )
            }

            composable(Route.MyProfile.path) {
                MyProfileScreenVm(
                    favVm = favoriteModel,
                    vm = artistModel,
                    goPlayer = { songId ->
                        navController.navigate("player/$songId") {
                            launchSingleTop = true
                        }
                    },
                    onEditProfile = goEditProfile
                )
            }

            composable(
                route = "playlist/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId =
                    backStackEntry.arguments?.getLong("playlistId") ?: return@composable
                PlaylistDetailScreenVm(
                    playlistId = playlistId,
                    playlistViewModel = playlistViewModel,
                    goPlayer = goPlayer,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}


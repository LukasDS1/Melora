package com.example.melora.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.melora.data.storage.UserPreferences
import com.example.melora.ui.components.*
import com.example.melora.ui.screen.*
import com.example.melora.ui.theme.Resaltado
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
    homeScreenViewModel: HomeScreenViewModel,
    registerApiViewModel: RegisterApiViewModel,
    loginApiViewModel: LoginApiViewModel,
    uploadApiViewModel: UploadApiViewModel
) {

    // =============== NUEVO: leer el login real desde UserPreferences =====================
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val isLoggedIn by prefs.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    // ====================================================================================

    val userId by prefs.userId.collectAsStateWithLifecycle(initialValue = null)
    val profilePicture by prefs.profilePicture.collectAsStateWithLifecycle(initialValue = null)
    val roleId by prefs.userRoleId.collectAsStateWithLifecycle(initialValue = null)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ================= START DESTINATION CORRECTO =====================
    val startDestination = if (isLoggedIn) Route.Home.path else Route.Login.path
    // ==================================================================

    // ================= Navegaciones ====================

    val goHome = {
        navController.navigate(Route.Home.path) {
            popUpTo(Route.Login.path) { inclusive = true }
            launchSingleTop = true
        }
    }

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

    val goSearch = {
        navController.navigate(Route.SearchView.path) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val goPlaylist: (Long) -> Unit = { id ->
        navController.navigate("playlist/$id") { launchSingleTop = true }
    }

    val goFavorites = {
        navController.navigate(Route.Favorites.path) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val goUpload = {
        navController.navigate(Route.UploadScreenForm.path) { launchSingleTop = true }
    }

    val goEditProfile = {
        navController.navigate(Route.editProfile.path) { launchSingleTop = true }
    }

    val goSucces = {
        navController.navigate(Route.SuccesUpload.path) { launchSingleTop = true }
    }

    val goArtistProfile: (Long?) -> Unit = { id ->
        navController.navigate("artistProfile/$id") { launchSingleTop = true }
    }

    val goPlayer: (Long) -> Unit = { id ->
        navController.navigate("player/$id") { launchSingleTop = true }
    }

    val goMyProfile = {
        navController.navigate(Route.MyProfile.path) { launchSingleTop = true }
    }

    val hideBars = currentRoute in listOf(
        Route.Login.path,
        Route.Register.path,
        Route.UploadScreenForm.path,
        Route.editProfile.path,
        Route.Player.path
    )

    // ======================= UI + NavHost ======================
    Scaffold(
        containerColor = Resaltado,
        topBar = {
            if (!hideBars) {
                AppTopBar(
                    onUpload = goUpload,
                    profileImageUrl = profilePicture,
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

            // =================== LOGIN =====================
            composable(Route.Login.path) {
                LoginScreenVm(
                    vm = loginApiViewModel,
                    onLoginOk = goHome,
                    onGoRegister = goRegister
                )
            }

            // ================== REGISTER ====================
            composable(Route.Register.path) {
                RegisterScreenVm(
                    vm = registerApiViewModel,
                    onGoLogin = goLogin,
                    onRegistered = goLogin
                )
            }

            // =================== HOME =======================
            composable(Route.Home.path) {
                HomeScreenVm(
                    vm = homeScreenViewModel,
                    goPlayer = { id ->
                        navController.navigate("player/$id") { launchSingleTop = true }
                    }
                )
            }

            // =================== RESTO IGUAL ====================
            composable(Route.UploadScreenForm.path) {
                if (userId != null) {
                    UploadScreenVm(
                        vm = uploadApiViewModel,
                        onGoSuccess = goSucces,
                        userId = userId!!
                    )
                } else {
                    goLogin()
                }
            }

            composable(Route.SuccesUpload.path) {
                SuccesUpload(
                    onGoHome = goHome,
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
                val id = backStackEntry.arguments?.getLong("artistId") ?: 0L
                ArtistProfileScreenVm(
                    artistId = id,
                    vm = artistModel,
                    goPlayer = goPlayer,
                    roleId = roleId
                )
            }

            composable(
                route = "player/{songId}",
                arguments = listOf(navArgument("songId") { type = NavType.LongType })
            ) { entry ->
                val songId = entry.arguments?.getLong("songId") ?: return@composable

                LaunchedEffect(songId) { musicPlayerViewModel.getSongDetails(songId) }

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

            composable(Route.   editProfile.path) {
                EditProfileScreenVm(
                    vm = editProfileViewModel,
                    onExit = goMyProfile,
                    onProfileUpdated = { navController.popBackStack() },
                    onLogout = goLogin
                )
            }

            composable(Route.MyProfile.path) {
                MyProfileScreenVm(
                    favVm = favoriteModel,
                    vm = artistModel,
                    goPlayer = goPlayer,
                    onEditProfile = goEditProfile
                )
            }

            composable(
                route = "playlist/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { entry ->
                val playlistId = entry.arguments?.getLong("playlistId") ?: return@composable
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

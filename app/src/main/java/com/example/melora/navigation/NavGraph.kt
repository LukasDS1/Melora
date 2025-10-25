package com.example.melora.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.data.storage.UserPreferences
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
    favoriteModel: FavoriteViewModel
) {

    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val db = MeloraDB.getInstance(context)
    val artistRepository = ArtistRepository(
        userDao = db.userDao(),
        songDao = db.songDao()
    )

    val prefs = remember { UserPreferences(context) }
    val isLoggedIn by prefs.isLoggedIn.collectAsState(initial = false)
    val savedUserId by prefs.userId.collectAsState(initial = null)

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        prefs.isLoggedIn.collect { logged ->
            startDestination = if (logged) Route.Home.path else Route.Login.path
        }
    }

    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }


    val goLogin: () -> Unit = { navController.navigate(Route.Login.path){popUpTo(0)} }
    val goRegister: () -> Unit = {navController.navigate(Route.Register.path){popUpTo(0)}}
    val goHome: () -> Unit = {navController.navigate(Route.Home.path){popUpTo(0)}}
    val goUpload: () -> Unit = { navController.navigate(Route.UploadScreenForm.path) }
    val goSucces: () -> Unit = { navController.navigate(Route.SuccesUpload.path) }
    val goSearch: () -> Unit = { navController.navigate(Route.SearchView.path) }
    val goArtistProfile: (Long) -> Unit = { id -> navController.navigate("artistProfile/$id") }
    val goPlayer: (Long) -> Unit = { id -> navController.navigate("player/$id") }
    val goFavorites: () -> Unit = { navController.navigate(Route.Favorites.path) }


        Scaffold(
            topBar = {
                if (currentRoute != Route.Login.path && currentRoute != Route.Register.path && currentRoute != Route.Player.path) {
                    AppTopBar(
                        onHome = goHome,
                        onLogin = goLogin,
                        onRegister = goRegister
                    )
                }
            },
            bottomBar = {
                if (currentRoute != Route.Login.path && currentRoute != Route.Register.path) {
                    AppNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination!!,
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
                        onGoUpload = goUpload,
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

                composable(route = "player/{songId}", arguments = listOf(navArgument("songId") { type = NavType.LongType }))
                    { backStackEntry ->
                    val songId = backStackEntry.arguments?.getLong("songId") ?: return@composable

                    LaunchedEffect(songId) {
                        musicPlayerViewModel.getSongDetails(songId)
                    }

                    val currentSong by musicPlayerViewModel.currentSong.collectAsStateWithLifecycle()

                    currentSong?.let {
                        PlayerScreenVm(
                            songId = songId,
                            vm = musicPlayerViewModel,
                            onExitPlayer = goSearch,
                            favVm = favoriteModel
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



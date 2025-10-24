package com.example.melora.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.repository.ArtistRepository
import com.example.melora.ui.components.*
import com.example.melora.ui.screen.*
import com.example.melora.viewmodel.*
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    uploadViewModel: UploadViewModel,
    searchViewModel: SearchViewModel,
    authViewModel: AuthViewModel,
    artistModel: ArtistProfileViewModel,
    musicModel: MusicPlayerViewModel,
    favoriteModel: FavoriteViewModel
) {

    val currentUser by authViewModel.currentUser.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val db = MeloraDB.getInstance(context)
    val artistRepository = ArtistRepository(
        userDao = db.userDao(),
        songDao = db.songDao()
    )

    val goHome: () -> Unit = {
        navController.navigate(Route.Home.path) {
            popUpTo(0)
        }
    }
    val goLogin: () -> Unit = {
        navController.navigate(Route.Login.path) {
            popUpTo(0)
        }
    }
    val goRegister: () -> Unit = {
        navController.navigate(Route.Register.path) {
            popUpTo(0)
        }
    }
    val goUpload: () -> Unit = { navController.navigate(Route.UploadScreenForm.path) }
    val goSucces: () -> Unit = { navController.navigate(Route.SuccesUpload.path) }
    val goSearch: () -> Unit = { navController.navigate(Route.SearchView.path) }
    val goArtistProfile: (Long) -> Unit = { id -> navController.navigate("artistProfile/$id") }
    val goPlayer: (Long) -> Unit = { id -> navController.navigate("player/$id") }

    val startDestination = if (currentUser == null) {
        Route.Login.path
    } else {
        Route.Home.path
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = null,
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawerState.close() }
                        goHome()
                    },
                    onLogin = {
                        scope.launch {
                            drawerState.close()
                            authViewModel.logout()
                            goLogin()
                        }
                    },
                    onRegister = {
                        scope.launch { drawerState.close() }
                        goRegister()
                    }
                )
            )
        }
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            topBar = {
                if (currentRoute != Route.Login.path && currentRoute != Route.Register.path) {
                    AppTopBar(
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onHome = goHome,
                        onLogin = goLogin,
                        onRegister = goRegister
                    )
                }
            },
            bottomBar = {
                if (currentRoute != Route.Login.path && currentRoute != Route.Register.path) {
                    AppNavigationBar(navController = navController, authViewModel = authViewModel)
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
                        onGoUpload = goUpload,
                    )
                }

                composable(Route.UploadScreenForm.path) {
                    val user = authViewModel.currentUser.collectAsState().value
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
                    val user = currentUser

                    if (user == null) {
                        LaunchedEffect(Unit) { goLogin() }
                        return@composable
                    }
                    LaunchedEffect(songId) {
                        musicModel.getSongDetails(songId)
                    }

                    val currentSong by musicModel.currentSong.collectAsState()
                    currentSong?.let { songDetailed ->
                        PlayerScreen(
                            song = songDetailed,
                            userId = user.idUser,
                            playerViewModel = musicModel,
                            favoriteViewModel = favoriteModel
                        )
                    }
                }

                composable(
                    route = "favorites/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
                    FavoriteScreen(
                        userId = userId,
                        favoriteViewModel = favoriteModel,
                        goPlayer = goPlayer
                    )
                }
            }
        }
    }
}

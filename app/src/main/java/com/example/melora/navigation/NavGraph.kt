package com.example.melora.navigation
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.melora.ui.components.AppDrawer
import com.example.melora.ui.components.AppNavigationBar
import com.example.melora.ui.components.AppTopBar
import com.example.melora.ui.components.defaultDrawerItems
import com.example.melora.ui.screen.HomeScreen
import com.example.melora.ui.screen.LoginScreen
import com.example.melora.ui.screen.RegisterScreenVm
import com.example.melora.ui.screen.SuccesUpload
import com.example.melora.ui.screen.UploadScreenVm
import com.example.melora.viewmodel.UploadViewModel
import com.example.melora.ui.screen.SearchViewScreen
import com.example.melora.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    uploadViewModel: UploadViewModel,
    searchViewModel: SearchViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ---- Navegaciones ----
    val goUpload: () -> Unit = { navController.navigate(Route.UploadScreenForm.path) }
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goSucces: () -> Unit = { navController.navigate(Route.SuccesUpload.path) }
    val goSearch: () -> Unit = { navController.navigate(Route.SearchView.path) }

    // ---- Drawer lateral ----
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
                        scope.launch { drawerState.close() }
                        goLogin()
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
                    AppNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Home.path) {
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoUpload = goUpload,
                    )
                }
                composable(Route.Login.path) {
                    LoginScreen(
                        onLoginOk = goHome,
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        onGoLogin = goLogin,
                        onRegistered = goRegister
                    )
                }
                composable(Route.UploadScreenForm.path) {
                    UploadScreenVm(
                        vm = uploadViewModel,
                        onGoSucces = goSucces
                    )
                }
                composable(Route.SuccesUpload.path) {
                    SuccesUpload(
                        onLoginOk = goLogin,
                        onGoUpload = goUpload
                    )
                }
                composable(Route.SearchView.path) {
                    SearchViewScreen(searchViewModel)
                }
            }
        }
    }
}

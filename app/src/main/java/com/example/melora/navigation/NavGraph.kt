package com.example.melora.navigation
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.melora.ui.components.AppDrawer
import com.example.melora.ui.components.AppNavigationBar
import com.example.melora.ui.components.AppTopBar
import com.example.melora.ui.components.defaultDrawerItems
import com.example.melora.ui.screen.HomeScreen
import com.example.melora.ui.screen.LoginScreen
import com.example.melora.ui.screen.RegisterScreen
import com.example.melora.ui.screen.SuccesUpload
import com.example.melora.ui.screen.UploadScreenVm
import com.example.melora.viewmodel.UploadViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController, uploadViewModel: UploadViewModel) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val goUpload : () -> Unit = {navController.navigate(Route.UploadScreenForm.path)}
    val goHome: () -> Unit    = { navController.navigate(Route.Home.path) }    // Ir a Home
    val goLogin: () -> Unit   = { navController.navigate(Route.Login.path) }   // Ir a Login
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } // Ir a Registro
    val goSucces: () -> Unit = {navController.navigate(Route.SuccesUpload.path)} // ir a la pagina de succes

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
                        scope.launch { drawerState.close() } // Cierra drawer
                        goLogin()
                    },
                    onRegister = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goRegister()
                    }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister,
                    onOpenDrawer = goLogin
                )
            }, bottomBar = {
                AppNavigationBar( navController = navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable (Route.Home.path) {
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoUpload = goUpload
                    )
                }
                composable(Route.Login.path) { // Destino Login
                    LoginScreen(
                        onLoginOk = goHome,
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Register.path) {
                    RegisterScreen(
                        onRegistered = goLogin,
                        onGoLogin = goLogin
                    )
                }
                composable (Route.UploadScreenForm.path){
                    UploadScreenVm(
                        vm = uploadViewModel,
                        onGoSucces = goSucces
                    )
                }
                composable (Route.SuccesUpload.path){
                    SuccesUpload (
                        onLoginOk = goLogin,
                        onGoUpload = goUpload
                    )
                }
            }
        }
    }
}
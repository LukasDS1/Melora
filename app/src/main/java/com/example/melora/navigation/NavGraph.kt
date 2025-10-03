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
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.melora.ui.components.AppDrawer
import com.example.melora.ui.components.AppTopBar
import com.example.melora.ui.components.defaultDrawerItems
import com.example.melora.ui.screen.HomeScreen
import com.example.melora.ui.screen.LoginScreen
import com.example.melora.ui.screen.RegisterScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val goHome: () -> Unit    = { navController.navigate(Route.Home.path) }    // Ir a Home
    val goLogin: () -> Unit   = { navController.navigate(Route.Login.path) }   // Ir a Login
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } // Ir a Registro

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

        val navBackStackEntry by navController.currentBackStackEntryAsState() // Guarda la pantalla que está encima de la "pila" de pantallas
        val currentRoute = navBackStackEntry?.destination?.route // Guarda el string de la ruta actual

        Scaffold(
            topBar = {
                if (currentRoute != Route.Login.path) {
                    AppTopBar(
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onHome = goHome,
                        onLogin = goLogin,
                        onRegister = goRegister
                    )
                }
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
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Login.path) { // Destino Login
                    LoginScreen(
                        onLoginOk = goHome,      // Botón para volver al Home
                        onGoRegister = goRegister // Botón para ir a Registro
                    )
                }
                composable(Route.Register.path) { // Destino Registro
                    RegisterScreen(
                        onRegistered = goLogin, // Botón para ir a Login
                        onGoLogin = goLogin     // Botón alternativo a Login
                    )
                }
            }
        }
    }
}
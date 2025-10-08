package com.example.melora.navigation

import android.widget.SearchView
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
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
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController) {
    val textFieldState = remember { TextFieldState() }
    val searchResults = remember { listOf<String>() }
    val onSearch: (String) -> Unit = { query ->
        println("Buscando: $query")

    }

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
        Scaffold(
            topBar = {
                AppTopBar(
                    textFieldState = textFieldState,
                    searchResults = searchResults,
                    onSearch = { query ->
                        // Aquí manejas la búsqueda
                        println("Buscando: $query")
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister,
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
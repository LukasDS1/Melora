package com.example.melora.ui.system

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.melora.navigation.Route
import com.example.melora.ui.theme.PrimaryBg

@Composable
fun ApplySystemBars(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val activity = androidx.compose.ui.platform.LocalContext.current as ComponentActivity

    SideEffect {
        when (currentRoute) {
            Route.Login.path, Route.Register.path -> {
                activity.enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(
                        PrimaryBg.toArgb()
                    ),
                    navigationBarStyle = SystemBarStyle.dark(
                        PrimaryBg.toArgb()
                    )
                )
            }

            else -> {
                activity.enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ),
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    )
                )
            }
        }
    }
}

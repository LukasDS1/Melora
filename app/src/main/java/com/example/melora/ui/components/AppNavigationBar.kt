package com.example.melora.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.melora.navigation.AppNavGraph


@Composable
fun AppNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val destinations = listOf(
        Destination("uploadForm", "Upload", Icons.Filled.AddCircle, "Página de subida de musica"),
        Destination("SearchView", "Search", Icons.Filled.Search, "Ir a buscar"),
        Destination("register", "Library", Icons.Filled.Star, "Ir a la bibloteca" +"")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = modifier, containerColor = Color(0xFF393939)) {
        destinations.forEach { destination ->
            val isSelected = currentRoute == destination.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(destination.icon, contentDescription = destination.contentDescription,
                    tint = if(isSelected) destination.selectedColor else destination.color)},
                label = { Text(destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.Black,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}


data class Destination(
    val route: String,
    val label : String,
    val icon: ImageVector,
    val contentDescription: String,
    val color: Color = Color.Black,
    val selectedColor: Color = Color.White
)
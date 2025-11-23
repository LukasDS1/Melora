package com.example.melora.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.melora.navigation.Route
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PrimaryBg

@Composable
fun AppNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onGoHome: () -> Unit,
    onGoSearch: () -> Unit,
    onGoFavorites: () -> Unit
) {

    val destinations = listOf(
        Destination(Route.Favorites.path, "Library", Icons.Default.Favorite, "Check favorites"),
        Destination(Route.SearchView.path, "Search", Icons.Filled.Search, "Search songs"),
        Destination(Route.Home.path, "Home", Icons.Filled.Home, "Go home")
    )

    val bg = MaterialTheme.colorScheme.background
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier.clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
        containerColor = bg) {
        destinations.forEach { destination ->

            val isSelected = currentRoute?.startsWith(destination.route) == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    when (destination.route) {
                        Route.Home.path -> onGoHome()
                        Route.SearchView.path -> onGoSearch()
                        Route.Favorites.path -> onGoFavorites()
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.contentDescription,
                        tint = if (isSelected) destination.selectedColor else destination.color
                    )
                },
                label = { Text(
                    text = destination.label,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = Lato)
                ) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
    val color: Color = Color.White,
    val selectedColor: Color = Color.Black
)

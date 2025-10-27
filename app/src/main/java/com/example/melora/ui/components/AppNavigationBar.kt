package com.example.melora.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.melora.navigation.Route
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PrimaryBg

@Composable
fun AppNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val destinations = listOf(
        Destination(Route.UploadScreenForm.path, "Upload", Icons.Filled.AddCircle, "Subir música"),
        Destination(Route.SearchView.path, "Search", Icons.Filled.Search, "Buscar canciones"),
        Destination(Route.Favorites.path, "Library", Icons.Default.Favorite, "Ver biblioteca"),
        Destination(Route.MyProfile.path,"MyProfile",Icons.Default.MusicNote,"Perfil artistico")
    )

    val bg = PrimaryBg
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = modifier, containerColor = bg) {
        destinations.forEach { destination ->
            val isSelected = currentRoute?.startsWith(destination.route) == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
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
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
    val color: Color = Color.Black,
    val selectedColor: Color = Color.White
)
